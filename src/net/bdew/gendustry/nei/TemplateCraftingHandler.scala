/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.nei

import codechicken.lib.gui.GuiDraw
import codechicken.nei.recipe.ShapelessRecipeHandler
import net.bdew.gendustry.items.{GeneSample, GeneTemplate}
import net.bdew.lib.Misc
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.inventory.Container
import net.minecraft.item.ItemStack

class TemplateCraftingHandler extends ShapelessRecipeHandler {

  def addRecipe() {
    import scala.collection.JavaConversions._
    val rec = List(new ItemStack(GeneTemplate), new ItemStack(GeneSample), new ItemStack(GeneSample))
    val out = new ItemStack(GeneTemplate)
    arecipes.add(new CachedShapelessRecipe(rec, out))
  }

  override def loadCraftingRecipes(result: ItemStack) {
    if (result.getItem == GeneTemplate)
      addRecipe()
  }

  override def loadUsageRecipes(ingredient: ItemStack) {
    if (ingredient.getItem == GeneTemplate || ingredient.getItem == GeneSample)
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
