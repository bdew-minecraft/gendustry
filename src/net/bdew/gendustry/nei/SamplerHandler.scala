/*
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.nei

import net.bdew.lib.gui.{Rect, Point}
import net.bdew.gendustry.config.{Items, Machines}
import net.bdew.gendustry.nei.helpers.PowerComponent
import forestry.api.genetics._
import forestry.api.apiculture.{EnumBeeType, IBeeRoot}
import forestry.api.arboriculture.{EnumGermlingType, ITreeRoot}
import net.minecraft.item.ItemStack
import codechicken.nei.recipe.TemplateRecipeHandler.RecipeTransferRect
import java.awt.Rectangle
import net.bdew.gendustry.Gendustry
import codechicken.nei.recipe.GuiRecipe
import java.util
import net.bdew.lib.Misc
import net.bdew.gendustry.forestry.GeneSampleInfo
import scala.Some
import net.bdew.gendustry.compat.ExtraBeesProxy

class SamplerHandler extends BaseRecipeHandler {
  lazy val offset = new Point(5, 13)
  val mutagenRect = new Rect(32, 19, 16, 58)
  val mjRect = new Rect(8, 19, 16, 58)

  import scala.collection.JavaConversions._
  import NEICache.SampleOrdering

  class SamplerRecipe(sample: GeneSampleInfo, input: ItemStack) extends CachedRecipeWithComponents {
    val getResult = position(Items.geneSample.newStack(sample), 137, 49)
    val individual = position(input, 41, 49)
    val sampleBlank = position(new ItemStack(Items.geneSampleBlank), 74, 28)
    val labware = position(new ItemStack(Items.labware), 98, 28)

    components :+= new PowerComponent(mjRect, Machines.sampler.mjPerItem, Machines.sampler.maxStoredEnergy)

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
    transferRects.add(new RecipeTransferRect(new Rectangle(63 - offset.x, 49 - offset.y, 66, 15), "Sampler"))
  }

  def addAllRecipes() {
    for (info <- NEICache.geneSamples; species <- NEICache.speciesChromosomes(info)) {
      arecipes.add(new SamplerRecipe(info, getRecipeStack(species)))
    }
    if (Machines.sampler.convertEBSerums && ExtraBeesProxy.ebLoaded) {
      for (sample <- NEICache.geneSamples if sample.root.isInstanceOf[IBeeRoot]) {
        val serum = ExtraBeesProxy.makeSerumFromSample(sample)
        if (serum != null) arecipes.add(new SamplerRecipe(sample, serum))
      }
    }
  }

  override def loadUsageRecipes(outputId: String, results: AnyRef*): Unit = {
    Some(outputId, results) collect {
      case ("item", Seq(x: ItemStack)) if x.itemID == Items.labware.itemID => addAllRecipes()
      case ("item", Seq(x: ItemStack)) if x.itemID == Items.geneSampleBlank.itemID => addAllRecipes()
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
        } else if (Machines.sampler.convertEBSerums && ExtraBeesProxy.isSerum(stack)) {
          val sample = ExtraBeesProxy.getSerumSample(stack)
          if (sample != null)
            arecipes.add(new SamplerRecipe(sample, stack))
        }
      case ("Sampler", _) => addAllRecipes()
    }
  }

  override def loadCraftingRecipes(outputId: String, results: AnyRef*): Unit = {
    Some(outputId, results) collect {
      case ("item", Seq(stack: ItemStack)) if stack.itemID == Items.geneSample.itemID =>
        val info = Items.geneSample.getInfo(stack)
        for (species <- NEICache.speciesChromosomes(info)) {
          arecipes.add(new SamplerRecipe(info, getRecipeStack(species)))
        }
        if (Machines.sampler.convertEBSerums && ExtraBeesProxy.ebLoaded) {
          val serum = ExtraBeesProxy.makeSerumFromSample(info)
          if (serum != null) arecipes.add(new SamplerRecipe(info, serum))
        }
      case ("Sampler", _) => addAllRecipes()
    }
  }

  override def handleItemTooltip(gui: GuiRecipe, stack: ItemStack, currenttip: util.List[String], recipe: Int): util.List[String] = {
    if (stack == getRecipe(recipe).labware.item)
      currenttip += Misc.toLocalF("gendustry.label.consume", Machines.mutatron.labwareConsumeChance.toInt)
    super.handleItemTooltip(gui, stack, currenttip, recipe)
  }

  def getGuiTexture = Gendustry.modId + ":textures/gui/sampler.png"
  def getRecipeName = Misc.toLocal("tile.gendustry.sampler.name")
}
