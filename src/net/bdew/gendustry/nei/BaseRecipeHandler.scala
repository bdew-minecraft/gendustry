/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.nei

import java.awt.Rectangle
import java.util

import codechicken.nei.PositionedStack
import codechicken.nei.recipe.TemplateRecipeHandler.RecipeTransferRect
import codechicken.nei.recipe.{GuiRecipe, TemplateRecipeHandler}
import net.bdew.gendustry.nei.helpers.RecipeComponent
import net.bdew.lib.gui._
import net.minecraft.item.ItemStack
import org.lwjgl.opengl.GL11

abstract class BaseRecipeHandler(val offX: Int, val offY: Int) extends TemplateRecipeHandler {

  import codechicken.lib.gui.GuiDraw._

  abstract class CachedRecipeWithComponents extends CachedRecipe {
    var components = List.empty[RecipeComponent]
    def position(s: ItemStack, x: Int, y: Int) = new PositionedStack(s, x - offX, y - offY)
  }

  def addTransferRect(r: Rect, id: String) {
    transferRects.add(new RecipeTransferRect(new Rectangle(r.x.round - offX, r.y.round - offY, r.w.round, r.h.round), id))
  }

  override def drawExtras(recipe: Int) {
    for (component <- arecipes.get(recipe).asInstanceOf[CachedRecipeWithComponents].components)
      component.render(Point(offX, offY))
    super.drawExtras(recipe)
  }

  override def mouseClicked(gui: GuiRecipe, button: Int, recipe: Int): Boolean = {
    val mrel = getMousePosition - gui.getRecipePosition(recipe) +(offX, offY) -(gui.guiLeft, gui.guiTop)
    for (component <- arecipes.get(recipe).asInstanceOf[CachedRecipeWithComponents].components)
      if (component.rect.contains(mrel) && component.mouseClick(button)) return true
    super.mouseClicked(gui, button, recipe)
  }

  override def handleTooltip(gui: GuiRecipe, currenttip: util.List[String], recipe: Int) = {
    import scala.collection.JavaConversions._
    val mrel = getMousePosition - gui.getRecipePosition(recipe) +(offX, offY) -(gui.guiLeft, gui.guiTop)
    for (component <- arecipes.get(recipe).asInstanceOf[CachedRecipeWithComponents].components)
      if (component.rect.contains(mrel)) currenttip.addAll(component.getTooltip)
    super.handleTooltip(gui, currenttip, recipe)
  }

  override def drawBackground(recipe: Int) {
    GL11.glColor4f(1, 1, 1, 1)
    changeTexture(getGuiTexture)
    drawTexturedModalRect(0, 0, offX, offY, 166, 65)
  }
}
