/*
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.nei.helpers

import net.bdew.lib.gui.{Point, Rect}
import codechicken.nei.recipe.{GuiUsageRecipe, GuiCraftingRecipe}
import net.minecraftforge.fluids.FluidStack
import java.text.DecimalFormat
import org.lwjgl.opengl.GL11
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.texture.TextureMap
import codechicken.core.gui.GuiDraw
import net.bdew.gendustry.gui.Textures

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
      val icon = fstack.getFluid.getStillIcon
      Minecraft.getMinecraft.renderEngine.bindTexture(TextureMap.locationBlocksTexture)
      var fillHeight: Int = (orect.h * fstack.amount / capacity).round
      var yStart: Int = 0

      while (fillHeight > 0) {
        if (fillHeight > 16) {
          GuiDraw.gui.drawTexturedModelRectFromIcon(orect.x, orect.y + orect.h - 16 - yStart, icon, 16, 16)
          fillHeight -= 16
        } else {
          GuiDraw.gui.drawTexturedModelRectFromIcon(orect.x, orect.y + orect.h - fillHeight - yStart, icon, 16, fillHeight)
          fillHeight = 0
        }
        yStart = yStart + 16
      }
    }

    GL11.glDisable(GL11.GL_BLEND)

    Minecraft.getMinecraft.renderEngine.bindTexture(Textures.tankOverlay.resource)
    GuiDraw.drawTexturedModalRect(orect.x, orect.y, Textures.tankOverlay.x, Textures.tankOverlay.y, orect.w, orect.h)
  }
}
