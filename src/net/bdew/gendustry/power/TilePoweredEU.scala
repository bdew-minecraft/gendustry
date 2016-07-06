/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.power

import ic2.api.energy.event.{EnergyTileLoadEvent, EnergyTileUnloadEvent}
import ic2.api.energy.tile.{IEnergyEmitter, IEnergySink}
import net.bdew.gendustry.compat.PowerProxy
import net.bdew.gendustry.config.Tuning
import net.bdew.lib.Misc
import net.bdew.lib.power.TilePoweredBase
import net.bdew.lib.tile.TileTicking
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Optional

@Optional.Interface(modid = PowerProxy.IC2_MOD_ID, iface = "ic2.api.energy.tile.IEnergySink")
trait TilePoweredEU extends TilePoweredBase with IEnergySink with TileTicking {
  var sentLoaded = false
  private lazy val ratio = Tuning.getSection("Power").getFloat("EU_MJ_Ratio")
  lazy val sinkTier = Tuning.getSection("Power").getSection("EU").getInt("SinkTier")

  if (PowerProxy.haveIC2 && PowerProxy.EUEnabled)
    serverTick.listen(sendLoad)

  override def invalidate() {
    if (PowerProxy.haveIC2 && PowerProxy.EUEnabled)
      sendUnload()
    super.invalidate()
  }

  override def onChunkUnload() {
    if (PowerProxy.haveIC2 && PowerProxy.EUEnabled)
      sendUnload()
    super.onChunkUnload()
  }

  @Optional.Method(modid = PowerProxy.IC2_MOD_ID)
  def sendUnload() {
    if (PowerProxy.haveIC2 && sentLoaded) {
      MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this))
      sentLoaded = false
    }
  }

  @Optional.Method(modid = PowerProxy.IC2_MOD_ID)
  def sendLoad() {
    if (PowerProxy.haveIC2 && !sentLoaded) {
      MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this))
      sentLoaded = true
    }
  }

  override def getDemandedEnergy = Misc.clamp(power.capacity - power.stored, 0F, power.maxReceive) * ratio
  override def getSinkTier = sinkTier
  override def injectEnergy(directionFrom: EnumFacing, amount: Double, p3: Double) = {
    // IC2 is borked and is ignoring the return value, we need to store everything otherwise energy will be wasted
    // We go around power.inject so that all energy can be added
    power.stored += (amount / ratio).toFloat
    power.parent.dataSlotChanged(power)
    0
  }

  override def acceptsEnergyFrom(iEnergyEmitter: IEnergyEmitter, enumFacing: EnumFacing): Boolean = PowerProxy.EUEnabled
}
