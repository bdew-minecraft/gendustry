/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.compat.triggers

import net.bdew.lib.power.TileBaseProcessor
import net.minecraftforge.common.ForgeDirection

object TriggerWorking extends BaseTrigger("working", "x", classOf[TileBaseProcessor]) {
  def getState(side: ForgeDirection, tile: TileBaseProcessor) = tile.isWorking
}
