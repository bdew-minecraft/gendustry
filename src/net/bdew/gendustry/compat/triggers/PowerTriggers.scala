/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.compat.triggers

import net.bdew.gendustry.power.TilePowered
import buildcraft.api.gates.ActionManager
import net.minecraftforge.common.util.ForgeDirection

object TriggerPower0 extends BaseTrigger("power0", "z1", classOf[TilePowered]) {
  def getState(side: ForgeDirection, tile: TilePowered) = tile.power.stored == 0
}

object TriggerPower25 extends BaseTrigger("power25", "z2", classOf[TilePowered]) {
  def getState(side: ForgeDirection, tile: TilePowered) = tile.power.stored / tile.power.capacity >= 0.25
}

object TriggerPower50 extends BaseTrigger("power50", "z3", classOf[TilePowered]) {
  def getState(side: ForgeDirection, tile: TilePowered) = tile.power.stored / tile.power.capacity >= 0.5
}

object TriggerPower75 extends BaseTrigger("power75", "z4", classOf[TilePowered]) {
  def getState(side: ForgeDirection, tile: TilePowered) = tile.power.stored / tile.power.capacity >= 0.75
}

object TriggerPower100 extends BaseTrigger("power100", "z5", classOf[TilePowered]) {
  def getState(side: ForgeDirection, tile: TilePowered) = tile.power.stored / tile.power.capacity >= 1
}

object PowerTriggers {
  val triggers = Seq(TriggerPower0, TriggerPower25, TriggerPower50, TriggerPower75, TriggerPower100)
  def register() {
    triggers.foreach(ActionManager.registerTrigger(_))
  }
}