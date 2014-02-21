/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.nei

import net.bdew.lib.gui.{Rect, Point}
import net.bdew.gendustry.config.{Fluids, Items, Blocks, Machines}
import net.minecraftforge.fluids.FluidStack
import net.bdew.gendustry.nei.helpers.{PowerComponent, FluidComponent}
import forestry.api.genetics.{AlleleManager, ISpeciesRoot, IMutation}
import forestry.api.apiculture.{EnumBeeType, IBeeRoot}
import forestry.api.arboriculture.{EnumGermlingType, ITreeRoot}
import net.minecraft.item.ItemStack
import codechicken.nei.recipe.TemplateRecipeHandler.RecipeTransferRect
import java.awt.Rectangle
import net.bdew.gendustry.Gendustry
import codechicken.nei.recipe.GuiRecipe
import java.util
import net.bdew.lib.Misc

class MutatronHandler extends BaseRecipeHandler {
  lazy val offset = new Point(5, 13)
  val mutagenRect = new Rect(32, 19, 16, 58)
  val mjRect = new Rect(8, 19, 16, 58)

  import scala.collection.JavaConversions._

  class MutatronRecipe(mutation: IMutation) extends CachedRecipeWithComponents {
    val getResult = position(getRecipeStack(2, mutation), 142, 41)
    val in1 = position(getRecipeStack(0, mutation), 60, 30)
    val in2 = position(getRecipeStack(1, mutation), 60, 53)
    val labware = position(new ItemStack(Items.labware), 98, 17)

    components :+= new FluidComponent(mutagenRect, new FluidStack(Fluids.mutagen, Machines.mutatron.mutagenPerItem), Machines.mutatron.tankSize)
    components :+= new PowerComponent(mjRect, Machines.mutatron.mjPerItem, Machines.mutatron.maxStoredEnergy)

    override def getOtherStacks = List(in1, in2, labware)
  }

  def getRecipeStack(slot: Int, mutation: IMutation): ItemStack = {
    val root = mutation.getRoot
    val individual = slot match {
      case 0 => root.templateAsIndividual(root.getTemplate(mutation.getAllele0.getUID))
      case 1 => root.templateAsIndividual(root.getTemplate(mutation.getAllele1.getUID))
      case 2 => root.templateAsIndividual(mutation.getTemplate)
    }
    individual.analyze()
    return (root, slot) match {
      case (bees: IBeeRoot, 0) => bees.getMemberStack(individual, EnumBeeType.PRINCESS.ordinal())
      case (bees: IBeeRoot, 1) => bees.getMemberStack(individual, EnumBeeType.DRONE.ordinal())
      case (bees: IBeeRoot, 2) => bees.getMemberStack(individual, EnumBeeType.QUEEN.ordinal())
      case (trees: ITreeRoot, 0) => trees.getMemberStack(individual, EnumGermlingType.SAPLING.ordinal())
      case (trees: ITreeRoot, 1) => trees.getMemberStack(individual, EnumGermlingType.POLLEN.ordinal())
      case (trees: ITreeRoot, 2) => trees.getMemberStack(individual, EnumGermlingType.SAPLING.ordinal())
      case (root: ISpeciesRoot, _) => root.getMemberStack(individual, 0)
    }
  }

  def getRecipe(i: Int) = arecipes.get(i).asInstanceOf[MutatronRecipe]

  override def loadTransferRects() {
    transferRects.add(new RecipeTransferRect(new Rectangle(89 - offset.x, 41 - offset.y, 40, 15), "Mutatron"))
  }

  def addAllRecipes() {
    for ((_, root) <- AlleleManager.alleleRegistry.getSpeciesRoot; mutation <- root.getMutations(false)) {
      arecipes.add(new MutatronRecipe(mutation))
    }
  }

  override def loadUsageRecipes(outputId: String, results: AnyRef*): Unit = {
    Some(outputId, results) collect {
      case ("liquid", Seq(x: FluidStack)) if x.fluidID == Fluids.mutagen.getID => addAllRecipes()
      case ("item", Seq(x: ItemStack)) if x.itemID == Blocks.mutagen.blockID => addAllRecipes()
      case ("item", Seq(x: ItemStack)) if x.itemID == Items.labware.itemID => addAllRecipes()
      case ("item", Seq(x: ItemStack)) =>
        val individual = AlleleManager.alleleRegistry.getIndividual(x)
        if (individual != null) {
          for (mutation <- NEICache.speciesUsedMutations(individual.getGenome.getPrimary))
            arecipes.add(new MutatronRecipe(mutation))
        }
      case ("Mutatron", _) => addAllRecipes()
    }
  }

  override def loadCraftingRecipes(outputId: String, results: AnyRef*): Unit = {
    Some(outputId, results) collect {
      case ("item", Seq(x: ItemStack)) =>
        val individual = AlleleManager.alleleRegistry.getIndividual(x)
        if (individual != null) {
          for (mutation <- NEICache.speciesResultMutations(individual.getGenome.getPrimary))
            arecipes.add(new MutatronRecipe(mutation))
        }
      case ("Mutatron", _) => addAllRecipes()
    }
  }

  override def handleItemTooltip(gui: GuiRecipe, stack: ItemStack, currenttip: util.List[String], recipe: Int): util.List[String] = {
    if (stack == getRecipe(recipe).labware.item)
      currenttip += Misc.toLocalF("gendustry.label.consume", Machines.mutatron.labwareConsumeChance.toInt)
    if (stack == getRecipe(recipe).getResult.item) {
      currenttip += Misc.toLocalF("gendustry.label.mutatron.degrade", Machines.mutatron.degradeChanceNatural.toInt)
      currenttip += Misc.toLocalF("gendustry.label.mutatron.death", Machines.mutatron.deathChanceArtificial.toInt)
    }
    super.handleItemTooltip(gui, stack, currenttip, recipe)
  }

  def getGuiTexture = Gendustry.modId + ":textures/gui/mutatron.png"
  def getRecipeName = Misc.toLocal("tile.gendustry.mutatron.name")
}
