/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.nei

import net.bdew.gendustry.Gendustry
import net.bdew.lib.gui.Rect
import net.bdew.gendustry.config.{Items, Fluids, Machines}
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack
import net.bdew.gendustry.fluids.LiquidDNASources
import net.bdew.gendustry.nei.helpers.{PowerComponent, FluidComponent}
import net.bdew.lib.Misc
import codechicken.nei.recipe.GuiRecipe
import java.util
import forestry.api.genetics.{IAlleleSpecies, AlleleManager}
import forestry.api.arboriculture.{EnumGermlingType, ITreeRoot}
import forestry.api.apiculture.{EnumBeeType, IBeeRoot}
import forestry.api.lepidopterology.{EnumFlutterType, IButterflyRoot}

class ExtractorHandler extends BaseRecipeHandler(5, 13) {
  val dnaRect = new Rect(152, 19, 16, 58)
  val mjRect = new Rect(8, 19, 16, 58)

  import scala.collection.JavaConversions._

  class ExtractorRecipe(val in: ItemStack, val out: Int) extends CachedRecipeWithComponents {

    val inPositioned = position(in, 44, 41)
    val labware = position(new ItemStack(Items.labware), 94, 19)
    val getResult = null

    components :+= new FluidComponent(dnaRect, new FluidStack(Fluids.dna, out), Machines.extractor.tankSize)
    components :+= new PowerComponent(mjRect, Machines.extractor.mjPerItem, Machines.extractor.maxStoredEnergy)

    override def getOtherStacks = List(inPositioned, labware)
  }

  def getRecipe(i: Int) = arecipes.get(i).asInstanceOf[ExtractorRecipe]

  override def loadTransferRects() {
    addTransferRect(Rect(79, 41, 53, 15), "Extractor")
  }

  def getRecipeIndividuals(sp: IAlleleSpecies) = {
    val root = sp.getRoot
    val indiv = root.templateAsIndividual(root.getTemplate(sp.getUID))
    indiv.analyze()
    root match {
      case bees: IBeeRoot =>
        List(
          bees.getMemberStack(indiv, EnumBeeType.PRINCESS.ordinal()),
          bees.getMemberStack(indiv, EnumBeeType.QUEEN.ordinal()),
          bees.getMemberStack(indiv, EnumBeeType.DRONE.ordinal())
        )
      case trees: ITreeRoot =>
        List(
          trees.getMemberStack(indiv, EnumGermlingType.POLLEN.ordinal()),
          trees.getMemberStack(indiv, EnumGermlingType.SAPLING.ordinal())
        )
      case butterflies: IButterflyRoot =>
        List(
          butterflies.getMemberStack(indiv, EnumFlutterType.BUTTERFLY.ordinal()),
          butterflies.getMemberStack(indiv, EnumFlutterType.SERUM.ordinal()),
          butterflies.getMemberStack(indiv, EnumFlutterType.CATERPILLAR.ordinal())
        )
    }
  }

  def addAllRecipes() {
    val species = Misc.filterType(AlleleManager.alleleRegistry.getRegisteredAlleles.values(), classOf[IAlleleSpecies])
    val stacks = species.map(getRecipeIndividuals).flatten.filter(LiquidDNASources.getValue(_) > 0)
    stacks.foreach(x => arecipes.add(new ExtractorRecipe(x, LiquidDNASources.getValue(x))))
  }

  override def loadCraftingRecipes(outputId: String, results: AnyRef*): Unit = {
    Some(outputId, results) collect {
      case ("liquid", Seq(x: FluidStack)) if x.fluidID == Fluids.dna.getID => addAllRecipes()
      case ("item", Seq(x: ItemStack)) if x.itemID == Fluids.dna.getBlockID => addAllRecipes()
      case ("Extractor", _) => addAllRecipes()
    }
  }

  override def loadUsageRecipes(stack: ItemStack) {
    val res = LiquidDNASources.getValue(stack)
    if (res > 0) {
      arecipes.add(new ExtractorRecipe(stack, res))
    }
  }

  override def handleItemTooltip(gui: GuiRecipe, stack: ItemStack, currenttip: util.List[String], recipe: Int): util.List[String] = {
    if (stack == getRecipe(recipe).labware.item)
      currenttip.add(Misc.toLocalF("gendustry.label.consume", Machines.extractor.labwareConsumeChance.toInt))
    super.handleItemTooltip(gui, stack, currenttip, recipe)
  }

  def getGuiTexture = Gendustry.modId + ":textures/gui/extractor.png"
  def getRecipeName = Misc.toLocal("tile.gendustry.extractor.name")
}
