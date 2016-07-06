/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.machines.apiary

import net.bdew.gendustry.gui.Textures
import net.bdew.lib.data.DataSlotFloat
import net.bdew.lib.gui.widgets.Widget
import net.bdew.lib.gui.{Point, Rect}

import scala.collection.mutable

class WidgetApiaryProgress(val rect: Rect, progress: DataSlotFloat) extends Widget {
  val texture = Textures.whiteProgress(rect.w)

  override def handleTooltip(p: Point, tip: mutable.MutableList[String]) {
    tip += "%.0f%%".format(progress.value * 100)
  }

  override def draw(mouse: Point) {
    if (progress.value > 0) {
      parent.drawTextureInterpolate(rect, texture, 0, 0, progress.value, 1)
    }
  }
}
