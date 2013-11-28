/*
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.test

import net.minecraft.tileentity.TileEntity
import net.minecraftforge.common.ForgeDirection
import buildcraft.api.power.PowerHandler.Type
import buildcraft.api.power.{IPowerEmitter, IPowerReceptor}

class PowerEmitterTile extends TileEntity with IPowerEmitter {
  def canEmitPowerFrom(side: ForgeDirection): Boolean = true
  override def updateEntity() {
    if (worldObj.isRemote) return
    for (dir <- ForgeDirection.VALID_DIRECTIONS) {
      val te = worldObj.getBlockTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ)
      if (te != null && te.isInstanceOf[IPowerReceptor]) {
        val pr = te.asInstanceOf[IPowerReceptor].getPowerReceiver(dir.getOpposite)
        if (pr != null) {
          val power = pr.getMaxEnergyReceived
          if (power > 0)
            pr.receiveEnergy(Type.ENGINE, power, dir.getOpposite)
        }
      }
    }
  }
}
