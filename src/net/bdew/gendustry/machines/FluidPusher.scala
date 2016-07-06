/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.machines

import net.bdew.lib.data.DataSlotTankBase
import net.bdew.lib.data.base.TileDataSlotsTicking
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraftforge.fluids.IFluidHandler

trait FluidPusher extends TileDataSlotsTicking {
  val tank: DataSlotTankBase

  private def sendFluid() {
    if (tank.getFluidAmount > 0) {
      for (dir <- EnumFacing.values()) {
        val te: TileEntity = getWorld.getTileEntity(getPos.offset(dir))
        if (te != null && te.isInstanceOf[IFluidHandler]) {
          val pumped = te.asInstanceOf[IFluidHandler].fill(dir.getOpposite, tank.getFluid.copy(), true)
          if (pumped > 0) {
            tank.drain(pumped, true)
            if (tank.getFluidAmount <= 0) return
          }
        }
      }
    }
  }

  serverTick.listen(sendFluid)
}
