/*
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.power

import net.bdew.gendustry.compat.PowerProxy
import net.minecraftforge.common.{ForgeDirection, MinecraftForge}
import ic2.api.energy.event.{EnergyTileUnloadEvent, EnergyTileLoadEvent}
import ic2.api.energy.tile.IEnergySink
import net.bdew.gendustry.config.Tuning
import net.minecraft.tileentity.TileEntity
import cpw.mods.fml.common.Optional
import net.bdew.gendustry.Gendustry

@Optional.Interface(modid = PowerProxy.IC2_MOD_ID, iface = "ic2.api.energy.tile.IEnergySink")
trait TilePoweredEU extends TilePoweredBase with IEnergySink {
  var sentLoaded = false
  lazy val ratio = Tuning.getSection("Power").getFloat("IC2_MJ_Ratio")
  lazy val maxSafe = Tuning.getSection("Power").getSection("IC2").getInt("MaxSafeInput")

  if (PowerProxy.haveIC2) {
    serverTick.listen(() => {
      if (!sentLoaded) {
        MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this))
      }
    })
  }

  override def onChunkUnload() = {
    if (PowerProxy.haveIC2)
      MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this))
    super.onChunkUnload()
  }

  def acceptsEnergyFrom(emitter: TileEntity, direction: ForgeDirection) = true
  def getMaxSafeInput = maxSafe
  def demandedEnergyUnits() = (power.capacity - power.stored) * ratio
  def injectEnergyUnits(directionFrom: ForgeDirection, amount: Double) = {
    val res =  power.inject((amount / ratio).toFloat, false) * ratio
    Gendustry.logInfo("Recieved EU: %f", res)
    res
  }
}
