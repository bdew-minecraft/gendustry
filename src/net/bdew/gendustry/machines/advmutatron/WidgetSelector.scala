/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.machines.advmutatron

import net.bdew.gendustry.gui.Textures
import net.bdew.lib.data.DataSlotInt
import net.bdew.lib.gui.widgets.Widget
import net.bdew.lib.gui.{Point, Rect}

class WidgetSelector(origin: Point, dSlot: DataSlotInt, slotOffset: Int) extends Widget {
  val rect = new Rect(origin, 0, 0)
  val texture = Textures.slotSelect

  override def draw(mouse: Point) = {
    if (dSlot > 0)
      parent.drawTexture(Rect(rect.origin.x + (dSlot.value + slotOffset) * 18, rect.origin.y, 18, 18), texture)
  }
}
