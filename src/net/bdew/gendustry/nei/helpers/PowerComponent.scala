/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.nei.helpers

import net.bdew.gendustry.config.Config
import net.bdew.gendustry.gui.Textures
import net.bdew.gendustry.nei.NEIDrawTarget
import net.bdew.lib.DecFormat
import net.bdew.lib.gui.{Point, Rect}

class PowerComponent(rect: Rect, power: Float, capacity: Float) extends RecipeComponent(rect) {
  def getTooltip = List("%s %s".format(DecFormat.round(power * Config.powerShowMultiplier), Config.powerShowUnits))
  def mouseClick(button: Int) = false
  def render(offset: Point) {
    NEIDrawTarget.drawTextureInterpolate(rect - offset, Textures.powerFill, 0, 1 - (power / capacity), 1, 1)
  }
}
