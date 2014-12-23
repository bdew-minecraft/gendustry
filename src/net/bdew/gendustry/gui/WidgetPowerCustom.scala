/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.gui

import net.bdew.gendustry.config.Config
import net.bdew.lib.DecFormat
import net.bdew.lib.gui.{Point, Rect, Texture}
import net.bdew.lib.power.{DataSlotPower, WidgetPowerGauge}

import scala.collection.mutable

class WidgetPowerCustom(rect: Rect, texture: Texture, dSlot: DataSlotPower) extends WidgetPowerGauge(rect, texture, dSlot) {
  override def handleTooltip(p: Point, tip: mutable.MutableList[String]) =
    tip += DecFormat.round(dSlot.stored * Config.powerShowMultiplier) + "/" + DecFormat.round(dSlot.capacity * Config.powerShowMultiplier) + " " + Config.powerShowUnits
}
