/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.nei

import java.util

import codechicken.nei.recipe.GuiRecipe
import forestry.api.apiculture.{EnumBeeType, IBeeRoot}
import forestry.api.arboriculture.{EnumGermlingType, ITreeRoot}
import forestry.api.genetics._
import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.config.Items
import net.bdew.gendustry.forestry.GeneSampleInfo
import net.bdew.gendustry.items.GeneSample
import net.bdew.gendustry.machines.sampler.MachineSampler
import net.bdew.gendustry.misc.GeneticsCache
import net.bdew.gendustry.nei.helpers.PowerComponent
import net.bdew.lib.Misc
import net.bdew.lib.gui.Rect
import net.bdew.lib.items.IStack
import net.minecraft.item.ItemStack

class SamplerHandler extends BaseRecipeHandler(5, 13) {
  val mutagenRect = new Rect(32, 19, 16, 58)
  val mjRect = new Rect(8, 19, 16, 58)

  import net.bdew.gendustry.misc.GeneticsCache.SampleOrdering

import scala.collection.JavaConversions._

  class SamplerRecipe(sample: GeneSampleInfo, input: ItemStack) extends CachedRecipeWithComponents {
    val getResult = position(GeneSample.newStack(sample), 137, 49)
    val individual = position(input, 41, 49)
    val sampleBlank = position(new ItemStack(Items.geneSampleBlank), 74, 28)
    val labware = position(new ItemStack(Items.labware), 98, 28)

    components :+= new PowerComponent(mjRect, MachineSampler.mjPerItem, MachineSampler.maxStoredEnergy)

    override def getOtherStacks = List(individual, sampleBlank, labware)
  }

  def getRecipeStack(species: IAlleleSpecies): ItemStack = {
    val root = species.getRoot
    val individual = root.templateAsIndividual(root.getTemplate(species.getUID))
    individual.analyze()
    return root match {
      case bees: IBeeRoot => bees.getMemberStack(individual, EnumBeeType.DRONE.ordinal())
      case trees: ITreeRoot => trees.getMemberStack(individual, EnumGermlingType.SAPLING.ordinal())
      case _ => root.getMemberStack(individual, 0)
    }
  }

  def getRecipe(i: Int) = arecipes.get(i).asInstanceOf[SamplerRecipe]

  override def loadTransferRects() {
    addTransferRect(Rect(63, 49, 66, 15), "Sampler")
  }

  def addAllRecipes() {
    for (info <- GeneticsCache.geneSamples; species <- GeneticsCache.speciesChromosomes(info)) {
      arecipes.add(new SamplerRecipe(info, getRecipeStack(species)))
    }
  }

  override def loadUsageRecipes(outputId: String, results: AnyRef*): Unit = {
    Some(outputId, results) collect {
      case ("item", Seq(IStack(x))) if x == Items.labware => addAllRecipes()
      case ("item", Seq(IStack(x))) if x == Items.geneSampleBlank => addAllRecipes()
      case ("item", Seq(stack: ItemStack)) =>
        val individual = AlleleManager.alleleRegistry.getIndividual(stack)
        if (individual != null) {
          val root = individual.getGenome.getSpeciesRoot
          var alleles = collection.SortedSet.empty[GeneSampleInfo]
          for ((chromosome, n) <- individual.getGenome.getChromosomes.zipWithIndex if chromosome != null) {
            if (chromosome.getPrimaryAllele != null && !AlleleManager.alleleRegistry.isBlacklisted(chromosome.getPrimaryAllele.getUID))
              alleles += GeneSampleInfo(root, n, chromosome.getPrimaryAllele)
            if (chromosome.getSecondaryAllele != null && !AlleleManager.alleleRegistry.isBlacklisted(chromosome.getSecondaryAllele.getUID))
              alleles += GeneSampleInfo(root, n, chromosome.getSecondaryAllele)
          }
          alleles.foreach(sample => arecipes.add(new SamplerRecipe(sample, stack)))
        }
      case ("Sampler", _) => addAllRecipes()
    }
  }

  override def loadCraftingRecipes(outputId: String, results: AnyRef*): Unit = {
    Some(outputId, results) collect {
      case ("item", Seq(stack: ItemStack)) if stack.getItem == GeneSample =>
        val info = GeneSample.getInfo(stack)
        if (info == null) {
          addAllRecipes()
        } else {
          for (species <- GeneticsCache.speciesChromosomes(info)) {
            arecipes.add(new SamplerRecipe(info, getRecipeStack(species)))
          }
        }
      case ("Sampler", _) => addAllRecipes()
    }
  }

  override def handleItemTooltip(gui: GuiRecipe, stack: ItemStack, currenttip: util.List[String], recipe: Int): util.List[String] = {
    if (stack == getRecipe(recipe).labware.item)
      currenttip += Misc.toLocalF("gendustry.label.consume", MachineSampler.labwareConsumeChance.toInt)
    super.handleItemTooltip(gui, stack, currenttip, recipe)
  }

  def getGuiTexture = Gendustry.modId + ":textures/gui/sampler.png"
  def getRecipeName = Misc.toLocal("tile.gendustry.sampler.name")
}
