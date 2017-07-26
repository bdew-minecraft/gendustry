/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.power

import cofh.redstoneflux.api.IEnergyContainerItem
import net.bdew.gendustry.compat.PowerProxy
import net.bdew.gendustry.config.Tuning
import net.bdew.lib.Misc
import net.bdew.lib.power.ItemPoweredBase
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.Optional

@Optional.Interface(modid = PowerProxy.TE_MOD_ID, iface = "cofh.redstoneflux.api.IEnergyContainerItem")
trait ItemPoweredRF extends ItemPoweredBase with IEnergyContainerItem {
  private lazy val ratio = Tuning.getSection("Power").getFloat("RF_MJ_Ratio")

  def receiveEnergy(container: ItemStack, maxReceive: Int, simulate: Boolean): Int = {
    if (!PowerProxy.RFEnabled) return 0
    val charge = getCharge(container)
    val canCharge = Misc.clamp(maxCharge.toFloat - charge, 0F, maxReceive.toFloat / ratio).floor.toInt
    if (!simulate) setCharge(container, charge + canCharge)
    return (canCharge * ratio).round
  }

  def extractEnergy(container: ItemStack, maxExtract: Int, simulate: Boolean): Int = 0
  def getEnergyStored(container: ItemStack): Int = (getCharge(container) * ratio).round
  def getMaxEnergyStored(container: ItemStack): Int = (maxCharge * ratio).round
}
