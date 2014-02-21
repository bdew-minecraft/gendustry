/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.power

import cofh.api.energy.IEnergyHandler
import net.minecraftforge.common.ForgeDirection
import net.bdew.gendustry.config.Tuning
import cpw.mods.fml.common.Optional
import net.bdew.gendustry.compat.PowerProxy
import net.bdew.lib.power.TilePoweredBase

@Optional.Interface(modid = PowerProxy.TE_MOD_ID, iface = "cofh.api.energy.IEnergyHandler")
trait TilePoweredRF extends TilePoweredBase with IEnergyHandler {
  private lazy val ratio = Tuning.getSection("Power").getFloat("RF_MJ_Ratio")

  def receiveEnergy(from: ForgeDirection, maxReceive: Int, simulate: Boolean) =
    (power.inject(maxReceive / ratio, simulate) * ratio).floor.toInt

  def extractEnergy(from: ForgeDirection, maxExtract: Int, simulate: Boolean) = 0
  def canInterface(from: ForgeDirection) = PowerProxy.RFEnabled
  def getEnergyStored(from: ForgeDirection) = (power.stored * ratio).floor.toInt
  def getMaxEnergyStored(from: ForgeDirection) = (power.capacity * ratio).floor.toInt
}
