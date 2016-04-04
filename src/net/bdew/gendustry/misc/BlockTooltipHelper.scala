/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.misc

import net.bdew.gendustry.config.Config
import net.bdew.lib.PimpVanilla._
import net.bdew.lib.{DecFormat, Misc}
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fluids.FluidTank

object BlockTooltipHelper {
  def getPowerTooltip(tag: NBTTagCompound, name: String) = {
    if (tag.hasKey(name))
      Some(DecFormat.round(tag.getCompoundTag(name).getFloat("stored") * Config.powerShowMultiplier) + " " + Config.powerShowUnits)
    else None
  }

  def getItemsTooltip(tag: NBTTagCompound) = {
    val sz = tag.getList[NBTTagCompound]("Items").size
    if (sz > 0)
      Some(Misc.toLocalF("gendustry.label.items", sz))
    else None
  }

  def getTankTooltip(tag: NBTTagCompound, name: String) = {
    if (tag.hasKey(name)) {
      val tank = new FluidTank(Int.MaxValue).readFromNBT(tag.getCompoundTag(name))
      if (tank.getFluid != null && tank.getFluid.getFluid != null && tank.getFluid.amount > 0) {
        Some("%s mB %s".format(DecFormat.round(tank.getFluidAmount), tank.getFluid.getLocalizedName))
      } else None
    } else None
  }

  def getInventory(tag: NBTTagCompound) = {
    if (tag.hasKey("Items")) {
      (for {
        itemTag <- tag.getList[NBTTagCompound]("Items")
        item <- itemTag.toItemStack
      } yield itemTag.getByte("Slot").toInt -> item).toMap
    } else Map.empty[Int, ItemStack]
  }
}
