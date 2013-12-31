/*
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.machines.advmutatron

import net.bdew.lib.gui.widgets.Widget
import net.bdew.lib.gui.{Point, Rect}
import net.bdew.lib.data.DataSlotInt
import net.bdew.gendustry.gui.Textures

class WidgetSelector(origin: Point, dslot: DataSlotInt, slotOffset: Int) extends Widget {
  val rect = new Rect(origin, 0, 0)
  val texture = Textures.slotSelect
  override def draw() = {
    if (dslot > 0) {
      bindTexture(texture.resource)
      val pos = rect.origin +((dslot.cval + slotOffset) * 18, 0)
      parent.drawTexturedModalRect(pos.x, pos.y, texture.x, texture.y, 18, 18)
    }
  }
}
