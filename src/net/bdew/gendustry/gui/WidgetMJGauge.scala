/*
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.gui

import net.bdew.lib.gui.{Point, TextureLocation, Rect}
import scala.collection.mutable
import java.text.DecimalFormat
import net.bdew.gendustry.data.DataSlotPower
import net.minecraft.client.Minecraft
import net.bdew.lib.gui.widgets.Widget

class WidgetMJGauge(val rect: Rect, texture: TextureLocation, dslot: DataSlotPower) extends Widget {
  val formater = new DecimalFormat("#,###")

  override def draw() {
    Minecraft.getMinecraft.renderEngine.bindTexture(texture.resource)
    val fill = (dslot.getEnergyStored / dslot.getMaxEnergyStored * rect.h).round
    parent.drawTexturedModalRect(rect.x, rect.y + rect.h - fill, texture.x, texture.y + rect.h - fill, rect.w, fill)
  }

  override def handleTooltip(p: Point, tip: mutable.MutableList[String]) = tip += formater.format(dslot.getEnergyStored) + "/" + formater.format(dslot.getMaxEnergyStored) + " MJ"
}
