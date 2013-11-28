/*
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.data

import buildcraft.api.power.{PowerHandler, IPowerReceptor}
import net.minecraft.world.World
import net.minecraftforge.common.ForgeDirection
import net.bdew.lib.tile.TileExtended

trait ExposePower extends TileExtended with IPowerReceptor {
  def getPowerDataslot(from: ForgeDirection): DataSlotPower
  def doWork(workProvider: PowerHandler) {}
  def getPowerReceiver(side: ForgeDirection) = getPowerDataslot(side).handler.getPowerReceiver
  def getWorld: World = worldObj
}
