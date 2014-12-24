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
import forestry.api.genetics._
import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.config.Items
import net.bdew.gendustry.forestry.GeneSampleInfo
import net.bdew.gendustry.items.{GeneSample, GeneTemplate}
import net.bdew.gendustry.machines.transposer.MachineTransposer
import net.bdew.gendustry.nei.helpers.PowerComponent
import net.bdew.lib.Misc
import net.bdew.lib.gui.Rect
import net.bdew.lib.items.IStack
import net.minecraft.item.ItemStack

class TransposerHandler extends BaseRecipeHandler(5, 13) {
  val mutagenRect = new Rect(32, 19, 16, 58)
  val mjRect = new Rect(8, 19, 16, 58)

  import scala.collection.JavaConversions._

  class TransposerRecipe(blankStack: ItemStack, outStack: ItemStack, templateStack: ItemStack) extends CachedRecipeWithComponents {
    val getResult = position(outStack, 137, 49)
    val blank = position(blankStack, 41, 49)
    val template = position(templateStack, 74, 28)
    val labware = position(new ItemStack(Items.labware), 98, 28)

    components :+= new PowerComponent(mjRect, MachineTransposer.mjPerItem, MachineTransposer.maxStoredEnergy)

    override def getOtherStacks = List(template, blank, labware)
  }

  def addRecipe(template: ItemStack) = template match {
    case IStack(GeneSample) =>
      arecipes.add(new TransposerRecipe(new ItemStack(Items.geneSampleBlank), template, template))
    case IStack(GeneTemplate) =>
      arecipes.add(new TransposerRecipe(new ItemStack(GeneTemplate), template, template))
  }

  def getRecipe(i: Int) = arecipes.get(i).asInstanceOf[TransposerRecipe]

  override def loadTransferRects() {
    addTransferRect(Rect(63, 49, 66, 15), "Transposer")
  }

  def addSampleRecipe() = {
    import scala.collection.JavaConversions._
    for ((name, root) <- AlleleManager.alleleRegistry.getSpeciesRoot) {
      val allele = root.getDefaultTemplate()(0)
      val sample = GeneSampleInfo(root, 0, allele)
      addRecipe(GeneSample.newStack(sample))
    }
  }

  def addTemplateRecipe() {
    import scala.collection.JavaConversions._
    for ((name, root) <- AlleleManager.alleleRegistry.getSpeciesRoot) {
      val allele = root.getDefaultTemplate()(0)
      val sample = GeneSampleInfo(root, 0, allele)
      val tpl = new ItemStack(GeneTemplate)
      GeneTemplate.addSample(tpl, sample)
      addRecipe(tpl)
    }
  }

  def addAllRecipes() {
    addSampleRecipe()
    addTemplateRecipe()
  }

  override def loadUsageRecipes(outputId: String, results: AnyRef*): Unit = {
    Some(outputId, results) collect {
      case ("item", Seq(IStack(Items.labware))) => addAllRecipes()
      case ("item", Seq(IStack(Items.geneSampleBlank))) => addSampleRecipe()
      case ("item", Seq(stack: ItemStack)) if stack.getItem == GeneSample =>
        addRecipe(stack)
      case ("item", Seq(stack: ItemStack)) if stack.getItem == GeneTemplate =>
        if (GeneTemplate.getSpecies(stack) == null)
          addTemplateRecipe()
        else
          addRecipe(stack)

      case ("Transposer", _) => addAllRecipes()
    }
  }

  override def loadCraftingRecipes(outputId: String, results: AnyRef*): Unit = {
    Some(outputId, results) collect {
      case ("item", Seq(stack: ItemStack)) if stack.getItem == GeneSample =>
        addRecipe(stack)
      case ("item", Seq(stack: ItemStack)) if stack.getItem == GeneTemplate =>
        if (GeneTemplate.getSpecies(stack) == null)
          addTemplateRecipe()
        else
          addRecipe(stack)
      case ("Transposer", _) => addAllRecipes()
    }
  }

  override def handleItemTooltip(gui: GuiRecipe, stack: ItemStack, tip: util.List[String], recipe: Int): util.List[String] = {
    if (stack == getRecipe(recipe).labware.item)
      tip += Misc.toLocalF("gendustry.label.consume", MachineTransposer.labwareConsumeChance.toInt)
    super.handleItemTooltip(gui, stack, tip, recipe)
  }

  def getGuiTexture = Gendustry.modId + ":textures/gui/transposer.png"
  def getRecipeName = Misc.toLocal("tile.gendustry.transposer.name")
}
