/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.machines.mutatron

import forestry.api.apiculture.EnumBeeType
import forestry.api.apiculture.IBee
import forestry.api.apiculture.IBeeRoot
import forestry.api.arboriculture.{ITree, EnumGermlingType, ITreeRoot}
import forestry.api.genetics._
import net.minecraft.item.ItemStack
import java.util.Random
import net.bdew.gendustry.config.{Items, Machines}

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
          if (random.nextInt(100) < Machines.mutatron.secretChance) {
            secret(random.nextInt(secret.size))
          } else {
            normal(random.nextInt(normal.size))
          }
        } else {
          // Only secret mutations - choose 1 randomly
          secret(random.nextInt(secret.size))
        }
      } else {
        // All calid mutations aren't secret - choose 1 randomly
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
      case newbee: IBee =>
        val orig = root.getMember(fromStack).asInstanceOf[IBee]
        newbee.mate(newbee)
        newbee.setIsNatural(orig.isNatural)
        root.getMemberStack(newbee, EnumBeeType.QUEEN.ordinal)
      case newtree: ITree =>
        root.getMemberStack(newtree, EnumGermlingType.SAPLING.ordinal)
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
          if (random.nextInt(100) < Machines.mutatron.degradeChanceNatural)
            beeResult.setIsNatural(false)
        } else {
          if (random.nextInt(100) < Machines.mutatron.deathChanceArtificial)
            return new ItemStack(Items.waste)
        }

        beeResult.writeToNBT(result.getTagCompound)
        return result

      case _ =>
        return result
    }

  }
}