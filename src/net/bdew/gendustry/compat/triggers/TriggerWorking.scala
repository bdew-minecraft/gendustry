/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.compat.triggers

import net.bdew.lib.power.TileBaseProcessor
import net.minecraftforge.common.util.ForgeDirection

object TriggerWorking extends BaseTrigger("working", "x", classOf[TileBaseProcessor]) {
  def getState(side: ForgeDirection, tile: TileBaseProcessor) = tile.isWorking
}
