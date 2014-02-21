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
import forestry.api.lepidopterology.IButterflyRoot

class ImprinterHandler extends BaseRecipeHandler {
  lazy val offset = new Point(5, 13)
  val mutagenRect = new Rect(32, 19, 16, 58)
  val mjRect = new Rect(8, 19, 16, 58)

  import scala.collection.JavaConversions._

  class ImprinterRecipe(tpl: ItemStack) extends CachedRecipeWithComponents {
    val getResult = position(getExampleStack(tpl, true), 137, 49)
    val input = position(getExampleStack(tpl, false), 41, 49)
    val template = position(tpl, 74, 28)
    val labware = position(new ItemStack(Items.labware), 98, 28)

    components :+= new PowerComponent(mjRect, Machines.imprinter.mjPerItem, Machines.imprinter.maxStoredEnergy)

    override def getOtherStacks = List(input, template, labware)
  }

  def getExampleStack(template: ItemStack, modded: Boolean): ItemStack = {
    val root = Items.geneTemplate.getSpecies(template)

    val tpl = root match {
      case bees: IBeeRoot => bees.getTemplate("forestry.speciesForest").clone()
      case trees: ITreeRoot => trees.getTemplate("forestry.treeOak").clone()
      case flies: IButterflyRoot => flies.getTemplate("forestry.lepiCabbageWhite").clone()
    }

    if (modded) {
      for (sample <- Items.geneTemplate.getSamples(template))
        tpl(sample.chromosome) = sample.allele
    }

    val indiv = root.templateAsIndividual(tpl)
    indiv.analyze()

    root match {
      case bees: IBeeRoot => bees.getMemberStack(indiv, EnumBeeType.PRINCESS.ordinal())
      case trees: ITreeRoot => trees.getMemberStack(indiv, EnumGermlingType.SAPLING.ordinal())
      case flies: IButterflyRoot => flies.getMemberStack(indiv, 0)
    }
  }

  def getRecipe(i: Int) = arecipes.get(i).asInstanceOf[ImprinterRecipe]

  override def loadTransferRects() {
    transferRects.add(new RecipeTransferRect(new Rectangle(63 - offset.x, 49 - offset.y, 66, 15), "Imprinter"))
  }

  def addExample() {
    val bees = AlleleManager.alleleRegistry.getSpeciesRoot("rootBees")
    val cult = AlleleManager.alleleRegistry.getAllele("forestry.speciesCultivated")
    val template = new ItemStack(Items.geneTemplate)
    Items.geneTemplate.addSample(template, GeneSampleInfo(bees, 0, cult))
    arecipes.add(new ImprinterRecipe(template))
  }

  override def loadUsageRecipes(outputId: String, results: AnyRef*): Unit = {
    Some(outputId, results) collect {
      case ("item", Seq(x: ItemStack)) if x.itemID == Items.labware.itemID => addExample()
      case ("item", Seq(stack: ItemStack)) if stack.itemID == Items.geneTemplate.itemID =>
        if (Items.geneTemplate.getSpecies(stack) == null)
          addExample()
        else
          arecipes.add(new ImprinterRecipe(stack))
      case ("Imprinter", _) => addExample()
    }
  }

  override def loadCraftingRecipes(outputId: String, results: AnyRef*): Unit = {
    Some(outputId, results) collect {
      case ("Imprinter", _) => addExample()
    }
  }

  override def handleItemTooltip(gui: GuiRecipe, stack: ItemStack, currenttip: util.List[String], recipe: Int): util.List[String] = {
    if (stack == getRecipe(recipe).labware.item)
      currenttip += Misc.toLocalF("gendustry.label.consume", Machines.mutatron.labwareConsumeChance.toInt)
    super.handleItemTooltip(gui, stack, currenttip, recipe)
  }

  def getGuiTexture = Gendustry.modId + ":textures/gui/imprinter.png"
  def getRecipeName = Misc.toLocal("tile.gendustry.imprinter.name")
}
