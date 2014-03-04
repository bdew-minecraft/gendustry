/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.nei.helpers

import net.bdew.lib.gui.{Texture, Point, Rect}
import codechicken.nei.recipe.{GuiUsageRecipe, GuiCraftingRecipe}
import net.minecraftforge.fluids.FluidStack
import java.text.DecimalFormat
import org.lwjgl.opengl.GL11
import net.bdew.gendustry.gui.Textures
import net.bdew.gendustry.nei.NEIDrawTarget

class FluidComponent(rect: Rect, fstack: FluidStack, capacity: Int) extends RecipeComponent(rect) {
  val formater = new DecimalFormat("#,###")

  def getTooltip = List(fstack.getFluid.getLocalizedName, "%s mB".format(formater.format(fstack.amount)))

  def mouseClick(button: Int) = button match {
    case 0 => GuiCraftingRecipe.openRecipeGui("liquid", fstack)
    case 1 => GuiUsageRecipe.openRecipeGui("liquid", fstack)
    case _ => false
  }

  def render(offset: Point) {
    val orect = rect - offset

    GL11.glEnable(GL11.GL_BLEND)
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

    if (fstack.getFluid.getStillIcon != null) {
      val icon = Texture(Texture.BLOCKS, fstack.getFluid.getStillIcon)
      var fillHeight = orect.h * fstack.amount / capacity
      var yStart = 0

      while (fillHeight > 0) {
        if (fillHeight > 16) {
          NEIDrawTarget.drawTexture(new Rect(orect.x, orect.y2 - 16 - yStart, orect.w, 16), icon)
          fillHeight -= 16
        } else {
          NEIDrawTarget.drawTextureInterpolate(new Rect(orect.x, orect.y2 - 16 - yStart, orect.w, 16), icon, 0, 1 - fillHeight / 16, 1, 1)
          fillHeight = 0
        }
        yStart = yStart + 16
      }
    }

    GL11.glDisable(GL11.GL_BLEND)
    NEIDrawTarget.drawTexture(orect, Textures.tankOverlay)
  }
}