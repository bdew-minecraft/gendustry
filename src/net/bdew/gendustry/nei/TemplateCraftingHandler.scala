/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.nei

import codechicken.nei.recipe.ShapelessRecipeHandler
import net.minecraft.item.ItemStack
import net.bdew.gendustry.config.Items
import net.bdew.lib.Misc
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.inventory.Container
import codechicken.core.gui.GuiDraw

class TemplateCraftingHandler extends ShapelessRecipeHandler {

  def addRecipe() {
    import scala.collection.JavaConversions._
    val rec = List(new ItemStack(Items.geneTemplate), new ItemStack(Items.geneSample), new ItemStack(Items.geneSample))
    val out = new ItemStack(Items.geneTemplate)
    arecipes.add(new CachedShapelessRecipe(rec, out))
  }

  override def loadCraftingRecipes(result: ItemStack) {
    if (result.itemID == Items.geneTemplate.itemID)
      addRecipe()
  }

  override def loadUsageRecipes(ingredient: ItemStack) {
    if (ingredient.itemID == Items.geneTemplate.itemID || ingredient.itemID == Items.geneSample.itemID)
      addRecipe()
  }

  override def drawExtras(recipe: Int) = {
    GuiDraw.fontRenderer.drawSplitString(Misc.toLocal("gendustry.label.template.crafting"), 5, 65, 155, 0x404040)
  }

  override def recipiesPerPage() = 1
  override def hasOverlay(gui: GuiContainer, container: Container, recipe: Int) = false
  override def loadTransferRects() {}
  override def getRecipeName = Misc.toLocal("item.gendustry.GeneTemplate.name")
}
