/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.waila

import net.bdew.lib.data.base.TileDataSlots
import java.text.DecimalFormat
import net.minecraft.item.ItemStack
import mcp.mobius.waila.api.{IWailaConfigHandler, IWailaDataAccessor}
import net.bdew.lib.power.DataSlotPower
import net.bdew.gendustry.config.Config
import net.bdew.lib.data.{DataSlotTank, DataSlotTankRestricted}
import net.minecraftforge.fluids.{FluidRegistry, FluidStack}

object WailaDataslotsDataProvider extends BaseDataProvider(classOf[TileDataSlots]) {
  val dec = new DecimalFormat("#,##0")

  override def getBodyStrings(target: TileDataSlots, stack: ItemStack, acc: IWailaDataAccessor, cfg: IWailaConfigHandler) = {
    val nbt = acc.getNBTData
    target.dataSlots flatMap {
      case (name, slot: DataSlotPower) =>
        val capacity = slot.capacity * Config.powerShowMultiplier
        val stored = nbt.getCompoundTag(name).getFloat("stored") * Config.powerShowMultiplier
        List("%s / %s %s".format(dec.format(stored), dec.format(capacity), Config.powerShowUnits))

      case (name, slot: DataSlotTankRestricted) =>
        val fstack = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag(name))
        if (fstack != null && fstack.getFluid != null) {
          List("%s / %s mB %s".format(dec.format(fstack.amount), dec.format(slot.size), fstack.getFluid.getLocalizedName))
        } else {
          val fluid = FluidRegistry.getFluid(slot.fluidID)
          List("0 / %s mB %s".format(dec.format(slot.size), fluid.getLocalizedName))
        }

      case (name, slot: DataSlotTank) =>
        val fstack = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag(name))
        if (fstack != null && fstack.getFluid != null) {
          List("[V] %s / %s mB %s".format(dec.format(fstack.amount), dec.format(slot.size), fstack.getFluid.getLocalizedName))
        } else {
          None
        }

      case _ => None
    }
  }
}
