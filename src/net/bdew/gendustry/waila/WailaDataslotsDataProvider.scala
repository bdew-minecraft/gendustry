/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.waila

import mcp.mobius.waila.api.{IWailaConfigHandler, IWailaDataAccessor}
import net.bdew.gendustry.config.Config
import net.bdew.lib.data.base.{TileDataSlots, UpdateKind}
import net.bdew.lib.data.{DataSlotTank, DataSlotTankRestricted}
import net.bdew.lib.power.DataSlotPower
import net.bdew.lib.{DecFormat, Misc}
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fluids.FluidStack

object WailaDataSlotsDataProvider extends BaseDataProvider(classOf[TileDataSlots]) {
  override def getNBTTag(player: EntityPlayerMP, te: TileDataSlots, tag: NBTTagCompound, world: World, pos: BlockPos) = {
    tag.setTag("gendustry_dataslots", Misc.applyMutator(new NBTTagCompound) {
      te.doSave(UpdateKind.GUI, _)
    })
    tag
  }

  override def getBodyStrings(target: TileDataSlots, stack: ItemStack, acc: IWailaDataAccessor, cfg: IWailaConfigHandler) = {
    if (acc.getNBTData.hasKey("gendustry_dataslots")) {
      target.doLoad(UpdateKind.GUI, acc.getNBTData.getCompoundTag("gendustry_dataslots"))
      target.dataSlots.values flatMap {
        case slot: DataSlotPower =>
          Some("%s / %s %s".format(DecFormat.round(slot.stored * Config.powerShowMultiplier), DecFormat.round(slot.capacity * Config.powerShowMultiplier), Config.powerShowUnits))

        case slot: DataSlotTankRestricted =>
          if (slot.getFluid != null && slot.getFluid.getFluid != null) {
            Some("%s / %s mB %s".format(DecFormat.round(slot.getFluidAmount), DecFormat.round(slot.size), slot.getFluid.getLocalizedName))
          } else {
            Some("0 / %s mB %s".format(DecFormat.round(slot.size), new FluidStack(slot.filterFluid, 0).getLocalizedName))
          }

        case slot: DataSlotTank =>
          if (slot.getFluid != null && slot.getFluid.getFluid != null) {
            Some("%s / %s mB %s".format(DecFormat.round(slot.getFluidAmount), DecFormat.round(slot.size), slot.getFluid.getLocalizedName))
          } else {
            None
          }

        case _ => None
      }
    } else List.empty
  }
}
