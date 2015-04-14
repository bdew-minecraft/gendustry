/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.nei

import java.util

import codechicken.nei.recipe.GuiRecipe
import forestry.api.apiculture.{EnumBeeType, IBeeRoot}
import forestry.api.arboriculture.{EnumGermlingType, ITreeRoot}
import forestry.api.genetics.{AlleleManager, IAlleleSpecies}
import forestry.api.lepidopterology.{EnumFlutterType, IButterflyRoot}
import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.config.{Fluids, Items}
import net.bdew.gendustry.fluids.LiquidDNASources
import net.bdew.gendustry.machines.extractor.MachineExtractor
import net.bdew.gendustry.nei.helpers.{FluidComponent, PowerComponent}
import net.bdew.lib.Misc
import net.bdew.lib.gui.Rect
import net.bdew.lib.items.IStackBlock
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack

class ExtractorHandler extends BaseRecipeHandler(5, 13) {
  val dnaRect = new Rect(152, 19, 16, 58)
  val mjRect = new Rect(8, 19, 16, 58)

  import scala.collection.JavaConversions._

  class ExtractorRecipe(val in: ItemStack, val out: Int) extends CachedRecipeWithComponents {

    val inPositioned = position(in, 44, 41)
    val labware = position(new ItemStack(Items.labware), 94, 19)
    val getResult = null

    components :+= new FluidComponent(dnaRect, new FluidStack(Fluids.dna, out), MachineExtractor.tankSize)
    components :+= new PowerComponent(mjRect, MachineExtractor.mjPerItem, MachineExtractor.maxStoredEnergy)

    override def getOtherStacks = List(inPositioned, labware)
  }

  def getRecipe(i: Int) = arecipes.get(i).asInstanceOf[ExtractorRecipe]

  override def loadTransferRects() {
    addTransferRect(Rect(79, 41, 53, 15), "Extractor")
  }

  def getRecipeIndividuals(sp: IAlleleSpecies) = {
    val root = sp.getRoot
    val template = root.getTemplate(sp.getUID)
    if (template == null) {
      List.empty
    } else {
      val individual = root.templateAsIndividual(template)
      individual.analyze()
      root match {
        case bees: IBeeRoot =>
          List(
            bees.getMemberStack(individual, EnumBeeType.PRINCESS.ordinal()),
            bees.getMemberStack(individual, EnumBeeType.QUEEN.ordinal()),
            bees.getMemberStack(individual, EnumBeeType.DRONE.ordinal())
          )
        case trees: ITreeRoot =>
          List(
            trees.getMemberStack(individual, EnumGermlingType.POLLEN.ordinal()),
            trees.getMemberStack(individual, EnumGermlingType.SAPLING.ordinal())
          )
        case butterflies: IButterflyRoot =>
          List(
            butterflies.getMemberStack(individual, EnumFlutterType.BUTTERFLY.ordinal()),
            butterflies.getMemberStack(individual, EnumFlutterType.SERUM.ordinal()),
            butterflies.getMemberStack(individual, EnumFlutterType.CATERPILLAR.ordinal())
          )
        case _ => List.empty
      }
    }
  }

  def addAllRecipes() {
    val species = Misc.filterType(AlleleManager.alleleRegistry.getRegisteredAlleles.values(), classOf[IAlleleSpecies])
    val stacks = species.map(getRecipeIndividuals).flatten.filter(LiquidDNASources.getValue(_) > 0)
    stacks.foreach(x => arecipes.add(new ExtractorRecipe(x, LiquidDNASources.getValue(x))))
  }

  override def loadCraftingRecipes(outputId: String, results: AnyRef*): Unit = {
    Some(outputId, results) collect {
      case ("liquid", Seq(x: FluidStack)) if x.getFluid == Fluids.dna => addAllRecipes()
      case ("item", Seq(IStackBlock(x))) if x == Fluids.dna.getBlock => addAllRecipes()
      case ("Extractor", _) => addAllRecipes()
    }
  }

  override def loadUsageRecipes(stack: ItemStack) {
    val res = LiquidDNASources.getValue(stack)
    if (res > 0) {
      arecipes.add(new ExtractorRecipe(stack, res))
    }
  }

  override def handleItemTooltip(gui: GuiRecipe, stack: ItemStack, tip: util.List[String], recipe: Int): util.List[String] = {
    if (stack == getRecipe(recipe).labware.item)
      tip.add(Misc.toLocalF("gendustry.label.consume", MachineExtractor.labwareConsumeChance.toInt))
    super.handleItemTooltip(gui, stack, tip, recipe)
  }

  def getGuiTexture = Gendustry.modId + ":textures/gui/extractor.png"
  def getRecipeName = Misc.toLocal("tile.gendustry.extractor.name")
}
