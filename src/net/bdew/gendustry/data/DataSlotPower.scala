/*
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.data

import net.minecraft.nbt.NBTTagCompound
import buildcraft.api.power.{IPowerReceptor, PowerHandler}
import net.bdew.lib.data.base.{TileDataSlots, UpdateKind, DataSlot}
import net.bdew.gendustry.machines.PoweredMachine

case class DataSlotPower(name: String, parent: TileDataSlots, kind: PowerHandler.Type) extends DataSlot {
  updateKind = Set(UpdateKind.GUI, UpdateKind.SAVE)
  var oldVal = 0F

  require(parent.isInstanceOf[IPowerReceptor])
  val handler = new PowerHandler(parent.asInstanceOf[IPowerReceptor], kind)

  def configure(cfg: PoweredMachine): DataSlotPower = {
    handler.configure(cfg.minReceivedEnergy, cfg.maxReceivedEnergy, cfg.activationEnergy, cfg.maxStoredEnergy)
    handler.configurePowerPerdition(cfg.powerLoss, cfg.powerLossInterval)
    return this
  }

  parent.serverTick.listen(checkUpdate)

  updateKind = Set(UpdateKind.GUI, UpdateKind.SAVE)

  def checkUpdate() {
    handler.update()
    if (getEnergyStored != oldVal) {
      oldVal = getEnergyStored
      parent.dataSlotChanged(this)
    }
  }

  def useEnergy(min: Float, max: Float, doUse: Boolean): Float = handler.useEnergy(min, max, doUse)
  def getMinEnergyReceived: Float = handler.getMinEnergyReceived
  def getMaxEnergyReceived: Float = handler.getMaxEnergyReceived
  def getMaxEnergyStored: Float = handler.getMaxEnergyStored
  def getActivationEnergy: Float = handler.getActivationEnergy
  def getEnergyStored: Float = handler.getEnergyStored

  def save(t: NBTTagCompound, kind: UpdateKind.Value) {
    handler.writeToNBT(t, name)
    if (kind == UpdateKind.GUI)
      t.getCompoundTag(name).setFloat("maxStored", getMaxEnergyStored)
  }

  def load(t: NBTTagCompound, kind: UpdateKind.Value) {
    handler.readFromNBT(t, name)
    if (kind == UpdateKind.GUI)
      handler.configure(getMaxEnergyReceived, getMaxEnergyReceived, getActivationEnergy, t.getCompoundTag(name).getFloat("maxStored"))
  }
}


