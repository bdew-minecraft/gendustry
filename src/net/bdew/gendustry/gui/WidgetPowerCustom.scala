/*
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.gui

import net.bdew.lib.power.{DataSlotPower, WidgetPowerGauge}
import net.bdew.lib.gui.{TextureLocation, Rect, Point}
import scala.collection.mutable
import net.bdew.gendustry.config.Config

class WidgetPowerCustom(rect: Rect, texture: TextureLocation, dslot: DataSlotPower) extends WidgetPowerGauge(rect, texture, dslot) {
  override def handleTooltip(p: Point, tip: mutable.MutableList[String]) =
    tip += formater.format(dslot.stored * Config.powerShowMultiplier) + "/" + formater.format(dslot.capacity * Config.powerShowMultiplier) + " " + Config.powerShowUnits
}
