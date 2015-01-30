/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.machines.apiary

import net.bdew.lib.gui.widgets.Widget
import net.bdew.lib.gui.{Point, Rect}

import scala.collection.mutable

class WidgetError(x: Int, y: Int, apiary: TileApiary) extends Widget {
  val rect: Rect = new Rect(x, y, 16, 16)
  override def draw(mouse: Point) {
    parent.drawTexture(rect, ErrorCodes.getIcon(apiary.errorState.value))
  }

  override def handleTooltip(p: Point, tip: mutable.MutableList[String]) {
    tip += ErrorCodes.getDescription(apiary.errorState.value)
    tip ++= apiary.getStats
  }
}
