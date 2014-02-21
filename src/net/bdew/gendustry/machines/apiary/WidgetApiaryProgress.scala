/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.machines.apiary

import net.bdew.lib.gui.{Point, Rect}
import net.bdew.lib.gui.widgets.Widget
import net.bdew.lib.data.DataSlotFloat
import net.bdew.gendustry.gui.Textures
import scala.collection.mutable
import net.bdew.lib.Misc
import net.bdew.gendustry.Gendustry
import net.minecraft.client.Minecraft

class WidgetApiaryProgress(val rect: Rect, breeding: DataSlotFloat, progress: DataSlotFloat) extends Widget {
  val texture = Textures.whiteProgress(rect.w)

  override def handleTooltip(p: Point, tip: mutable.MutableList[String]) {
    if (breeding.cval > 0) {
      tip += "%s - %.0f%%".format(Misc.toLocal(Gendustry.modId + ".label.breeding"), breeding.cval * 100)
    } else if (progress.cval > 0) {
      tip += "%s - %.0f%%".format(Misc.toLocal(Gendustry.modId + ".label.working"), progress.cval * 100)
    }
  }

  override def draw(mouse: Point) {
    if (breeding.cval > 0) {
      parent.drawTexture(Rect(rect.x, rect.y, (breeding.cval * rect.w).round, rect.h), texture)
    } else if (progress.cval > 0) {
      parent.drawTexture(Rect(rect.x, rect.y, (progress.cval * rect.w).round, rect.h), texture)
    }
  }
}
