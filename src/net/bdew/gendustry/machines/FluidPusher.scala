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
