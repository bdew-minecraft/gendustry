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

abstract class RecipeComponent(val rect: Rect) {
  def getTooltip: List[String]
  def mouseClick(button: Int): Boolean
  def render(offset: Point)
}
