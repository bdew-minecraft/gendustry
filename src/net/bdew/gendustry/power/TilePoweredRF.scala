/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.power

import cofh.api.energy.IEnergyReceiver
import net.bdew.gendustry.compat.PowerProxy
import net.bdew.gendustry.config.Tuning
import net.bdew.lib.power.TilePoweredBase
import net.minecraft.util.EnumFacing
import net.minecraftforge.fml.common.Optional

@Optional.Interface(modid = PowerProxy.TE_MOD_ID, iface = "cofh.api.energy.IEnergyReceiver")
trait TilePoweredRF extends TilePoweredBase with IEnergyReceiver {
  private lazy val ratio = Tuning.getSection("Power").getFloat("RF_MJ_Ratio")

  override def receiveEnergy(from: EnumFacing, maxReceive: Int, simulate: Boolean) =
    if (PowerProxy.RFEnabled)
      (power.inject(maxReceive / ratio, simulate) * ratio).floor.toInt
    else 0

  override def canConnectEnergy(from: EnumFacing) = PowerProxy.RFEnabled
  override def getEnergyStored(from: EnumFacing) = (power.stored * ratio).floor.toInt
  override def getMaxEnergyStored(from: EnumFacing) = (power.capacity * ratio).floor.toInt
}
