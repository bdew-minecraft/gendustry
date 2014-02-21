/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.nei

import codechicken.nei.recipe.{GuiRecipe, TemplateRecipeHandler}
import net.bdew.lib.gui.Point
import codechicken.core.gui.GuiDraw
import org.lwjgl.opengl.GL11
import codechicken.core.gui.GuiDraw._
import net.bdew.gendustry.nei.helpers.RecipeComponent
import java.util
import net.minecraft.item.ItemStack
import codechicken.nei.PositionedStack

abstract class BaseRecipeHandler extends TemplateRecipeHandler {
  val offset: Point

  abstract class CachedRecipeWithComponents extends CachedRecipe {
    var components = List.empty[RecipeComponent]
    def position(s: ItemStack, x: Int, y: Int) = new PositionedStack(s, x - offset.x, y - offset.y)
  }

  override def drawExtras(recipe: Int) {
    for (component <- arecipes.get(recipe).asInstanceOf[CachedRecipeWithComponents].components)
      component.render(offset)
    super.drawExtras(recipe)
  }

  override def mouseClicked(gui: GuiRecipe, button: Int, recipe: Int): Boolean = {
    val mrel = new Point(GuiDraw.getMousePosition) - gui.getRecipePosition(recipe) + offset - ((gui.guiLeft, gui.guiTop))
    for (component <- arecipes.get(recipe).asInstanceOf[CachedRecipeWithComponents].components)
      if (component.rect.contains(mrel) && component.mouseClick(button)) return true
    super.mouseClicked(gui, button, recipe)
  }

  override def handleTooltip(gui: GuiRecipe, currenttip: util.List[String], recipe: Int) = {
    import scala.collection.JavaConversions._
    val mrel = new Point(GuiDraw.getMousePosition) - gui.getRecipePosition(recipe) + offset - ((gui.guiLeft, gui.guiTop))
    for (component <- arecipes.get(recipe).asInstanceOf[CachedRecipeWithComponents].components)
      if (component.rect.contains(mrel)) currenttip.addAll(component.getTooltip)
    super.handleTooltip(gui, currenttip, recipe)
  }

  override def drawBackground(recipe: Int) {
    GL11.glColor4f(1, 1, 1, 1)
    changeTexture(getGuiTexture)
    drawTexturedModalRect(0, 0, offset.x, offset.y, 166, 65)
  }
}
