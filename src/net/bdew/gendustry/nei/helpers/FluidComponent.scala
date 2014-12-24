/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.nei.helpers

import codechicken.nei.recipe.{GuiCraftingRecipe, GuiUsageRecipe}
import net.bdew.gendustry.gui.Textures
import net.bdew.gendustry.nei.NEIDrawTarget
import net.bdew.lib.DecFormat
import net.bdew.lib.gui.{Point, Rect, Texture}
import net.minecraftforge.fluids.FluidStack
import org.lwjgl.opengl.GL11

class FluidComponent(rect: Rect, fStack: FluidStack, capacity: Int) extends RecipeComponent(rect) {
  def getTooltip = List(fStack.getFluid.getLocalizedName(fStack), "%s mB".format(DecFormat.round(fStack.amount)))

  def mouseClick(button: Int) = button match {
    case 0 => GuiCraftingRecipe.openRecipeGui("liquid", fStack)
    case 1 => GuiUsageRecipe.openRecipeGui("liquid", fStack)
    case _ => false
  }

  def render(offset: Point) {
    val oRect = rect - offset

    GL11.glEnable(GL11.GL_BLEND)
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

    if (fStack.getFluid.getStillIcon != null) {
      val icon = Texture(Texture.BLOCKS, fStack.getFluid.getStillIcon)
      var fillHeight = oRect.h * fStack.amount / capacity
      var yStart = 0

      while (fillHeight > 0) {
        if (fillHeight > 16) {
          NEIDrawTarget.drawTexture(new Rect(oRect.x, oRect.y2 - 16 - yStart, oRect.w, 16), icon)
          fillHeight -= 16
        } else {
          NEIDrawTarget.drawTextureInterpolate(new Rect(oRect.x, oRect.y2 - 16 - yStart, oRect.w, 16), icon, 0, 1 - fillHeight / 16, 1, 1)
          fillHeight = 0
        }
        yStart = yStart + 16
      }
    }

    GL11.glDisable(GL11.GL_BLEND)
    NEIDrawTarget.drawTexture(oRect, Textures.tankOverlay)
  }
}