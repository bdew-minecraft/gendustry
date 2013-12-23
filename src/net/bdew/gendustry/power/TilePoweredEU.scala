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
import net.bdew.lib.power.TilePoweredBase
import net.bdew.lib.Misc

@Optional.Interface(modid = PowerProxy.IC2_MOD_ID, iface = "ic2.api.energy.tile.IEnergySink")
trait TilePoweredEU extends TilePoweredBase with IEnergySink {
  var sentLoaded = false
  private lazy val ratio = Tuning.getSection("Power").getFloat("EU_MJ_Ratio")
  lazy val maxSafe = Tuning.getSection("Power").getSection("IC2").getInt("MaxSafeInput")

  if (PowerProxy.haveIC2) {
    serverTick.listen(() => {
      if (!sentLoaded) {
        MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this))
        sentLoaded = true
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
  def demandedEnergyUnits() = Misc.clamp(power.capacity - power.stored, 0F, power.maxReceive) * ratio
  def injectEnergyUnits(directionFrom: ForgeDirection, amount: Double) = {
    // IC2 is borked and is ignoring the return value, we need to store everything otherwise energy will be wasted
    // We go around power.inject so that all energy can be added
    power.stored += (amount / ratio).toFloat
    power.parent.dataSlotChanged(power)
    0
  }
}
