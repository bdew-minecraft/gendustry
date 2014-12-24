/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.forestry

import java.util.Random

import com.mojang.authlib.GameProfile
import forestry.api.apiculture.{EnumBeeType, IBee, IBeeRoot}
import forestry.api.arboriculture.{EnumGermlingType, ITree, ITreeRoot}
import forestry.api.genetics._
import forestry.api.lepidopterology.{EnumFlutterType, IButterflyRoot}
import net.bdew.gendustry.config.Items
import net.bdew.gendustry.items.GeneTemplate
import net.bdew.gendustry.machines.mutatron.MachineMutatron
import net.minecraft.item.ItemStack
import net.minecraft.world.World

object GeneticsHelper {
  val random = new Random

  def checkIndividualType(root: ISpeciesRoot, stack: ItemStack, slot: Int): Boolean = {
    (root, slot) match {
      case (bees: IBeeRoot, 0) => bees.getType(stack) == EnumBeeType.PRINCESS
      case (bees: IBeeRoot, 1) => bees.getType(stack) == EnumBeeType.DRONE
      case (trees: ITreeRoot, 0) => trees.getType(stack) == EnumGermlingType.SAPLING
      case (trees: ITreeRoot, 1) => trees.getType(stack) == EnumGermlingType.POLLEN
      case _ => true
    }
  }

  def isValidItemForSlot(stack: ItemStack, slot: Int): Boolean = {
    val root = AlleleManager.alleleRegistry.getSpeciesRoot(stack)
    if (root == null)
      return false
    return checkIndividualType(root, stack, slot)
  }

  def checkMutation(m: IMutation, s1: IAlleleSpecies, s2: IAlleleSpecies): Boolean = {
    if (m.getAllele0 == s1 && m.getAllele1 == s2) return true
    if (m.getAllele0 == s2 && m.getAllele1 == s1) return true
    return false
  }

  def getValidMutations(fromStack: ItemStack, toStack: ItemStack): Seq[IMutation] = {
    val emptyMutations = Seq.empty[IMutation]

    if (fromStack == null || toStack == null) return emptyMutations

    val root = AlleleManager.alleleRegistry.getSpeciesRoot(fromStack)
    if (root == null || !root.isMember(toStack)) return emptyMutations
    if (!checkIndividualType(root, fromStack, 0)) return emptyMutations
    if (!checkIndividualType(root, toStack, 1)) return emptyMutations

    val fromIndividual = root.getMember(fromStack)
    val toIndividual = root.getMember(toStack)
    if (fromIndividual == null || toIndividual == null) return emptyMutations

    val fromSpecies = fromIndividual.getGenome.getPrimary
    val toSpecies = toIndividual.getGenome.getPrimary
    if (fromSpecies == null || toSpecies == null) return emptyMutations

    import scala.collection.JavaConverters._

    val mutations = root.getCombinations(fromSpecies).asScala

    return mutations.filter(checkMutation(_, fromSpecies, toSpecies)).toSeq
  }

  def isPotentialMutationPair(fromStack: ItemStack, toStack: ItemStack): Boolean = {
    if (fromStack == null && toStack == null) return false
    if (toStack == null) return isValidItemForSlot(fromStack, 0)
    if (fromStack == null) return isValidItemForSlot(toStack, 1)
    return getValidMutations(fromStack, toStack).size > 0
  }

  def getMutationResult(fromStack: ItemStack, toStack: ItemStack): ItemStack = {
    val valid = getValidMutations(fromStack, toStack)
    if (valid.size == 0) return null

    val selected = if (valid.size > 1) {
      val secret = valid.filter(_.isSecret)
      val normal = valid.filter(!_.isSecret)
      if (secret.size > 0) {
        if (normal.size > 0) {
          // Have both, check chance
          if (random.nextInt(100) < MachineMutatron.secretChance) {
            secret(random.nextInt(secret.size))
          } else {
            normal(random.nextInt(normal.size))
          }
        } else {
          // Only secret mutations - choose 1 randomly
          secret(random.nextInt(secret.size))
        }
      } else {
        // All valid mutations aren't secret - choose 1 randomly
        normal(random.nextInt(normal.size))
      }
    } else {
      // only 1 valid mutation - use it
      valid(0)
    }
    return getFinalMutationResult(selected, fromStack, true)
  }

  def getFinalMutationResult(selected: IMutation, fromStack: ItemStack, applyDecay: Boolean): ItemStack = {
    val root = selected.getRoot
    val individual = root.templateAsIndividual(selected.getTemplate)

    val res = individual match {
      case newBee: IBee =>
        val orig = root.getMember(fromStack).asInstanceOf[IBee]
        newBee.mate(newBee)
        newBee.setIsNatural(orig.isNatural)
        root.getMemberStack(newBee, EnumBeeType.QUEEN.ordinal)
      case newTree: ITree =>
        root.getMemberStack(newTree, EnumGermlingType.SAPLING.ordinal)
      case _ =>
        root.getMemberStack(individual, 0)
    }

    if (applyDecay)
      return applyMutationDecayChance(res, fromStack)
    else
      return res
  }

  def applyMutationDecayChance(result: ItemStack, original: ItemStack): ItemStack = {
    val root = AlleleManager.alleleRegistry.getSpeciesRoot(result)
    root match {
      case bees: IBeeRoot =>
        val beeResult = bees.getMember(result)
        val beeOriginal = bees.getMember(original)

        if (beeOriginal.isNatural) {
          if (random.nextInt(100) < MachineMutatron.degradeChanceNatural)
            beeResult.setIsNatural(false)
        } else {
          if (random.nextInt(100) < MachineMutatron.deathChanceArtificial)
            return new ItemStack(Items.waste)
        }

        beeResult.writeToNBT(result.getTagCompound)
        return result

      case _ =>
        return result
    }
  }

  def addMutationToTracker(in1: ItemStack, in2: ItemStack, out: ItemStack, player: GameProfile, world: World) {
    val root = AlleleManager.alleleRegistry.getSpeciesRoot(in1)
    if (root == null || !root.isMember(in1) || !root.isMember(in2) || !root.isMember(out)) return
    val sp1 = root.getMember(in1).getGenome.getPrimary
    val sp2 = root.getMember(in2).getGenome.getPrimary
    val spR = root.getMember(out).getGenome.getPrimary
    val tracker = root.getBreedingTracker(world, player)

    import scala.collection.JavaConverters._

    root.getCombinations(sp1).asScala.filter(x => {
      checkMutation(x, sp1, sp2) && x.getTemplate()(0) == spR
    }).foreach(tracker.registerMutation)
  }

  def individualFromTemplate(tpl: ItemStack, pristine: Boolean = false) = {
    val root = GeneTemplate.getSpecies(tpl)
    val samples = GeneTemplate.getSamples(tpl)
    val template = root.getDefaultTemplate
    samples.foreach(x => template(x.chromosome) = x.allele)
    val individual = root.templateAsIndividual(template)
    individual.analyze()
    root match {
      case bees: IBeeRoot =>
        val bee = individual.asInstanceOf[IBee]
        bee.setIsNatural(pristine)
        bee.mate(bee)
        bees.getMemberStack(bee, EnumBeeType.QUEEN.ordinal())
      case trees: ITreeRoot =>
        trees.getMemberStack(individual, EnumGermlingType.SAPLING.ordinal())
      case butterflies: IButterflyRoot =>
        butterflies.getMemberStack(individual, EnumFlutterType.BUTTERFLY.ordinal())
      case other: ISpeciesRoot =>
        other.getMemberStack(individual, 0)
    }
  }

  def templateFromSpeciesUID(uid: String) = {
    val root = AlleleManager.alleleRegistry.getAllele(uid).asInstanceOf[IAlleleSpecies].getRoot
    val template = root.getTemplate(uid)
    val item = new ItemStack(GeneTemplate)

    for ((allele, chromosome) <- template.zipWithIndex if allele != null)
      GeneTemplate.addSample(item, GeneSampleInfo(root, chromosome, allele))

    item
  }

  /**
   * Returns the list of chromosomes as a map, filtering out unused ones
   */
  def getCleanKaryotype(root: ISpeciesRoot) = (
    root.getKaryotype
      filter { x => root.getDefaultTemplate()(x.ordinal()) != null }
      map { x => x.ordinal() -> x }
    ).toMap
}