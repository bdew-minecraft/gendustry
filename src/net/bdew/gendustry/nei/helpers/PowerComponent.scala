/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.nei.helpers

import net.bdew.lib.gui.{Point, Rect}
import java.text.DecimalFormat
import net.minecraft.client.Minecraft
import net.bdew.gendustry.gui.Textures
import codechicken.core.gui.GuiDraw
import net.bdew.gendustry.config.Config

class PowerComponent(rect: Rect, power: Float, capacity: Float) extends RecipeComponent(rect) {
  val formater = new DecimalFormat("#,###")
  def getTooltip = List("%s %s".format(formater.format(power * Config.powerShowMultiplier), Config.powerShowUnits))
  def mouseClick(button: Int) = false
  def render(offset: Point) {
    Minecraft.getMinecraft.renderEngine.bindTexture(Textures.powerFill.resource)
    val orect = rect - offset
    val fillArea = (orect.h * (power / capacity)).round
    GuiDraw.drawTexturedModalRect(orect.x, orect.y + orect.h - fillArea, Textures.powerFill.x, Textures.powerFill.y + orect.h - fillArea, orect.w, fillArea)
  }
}
