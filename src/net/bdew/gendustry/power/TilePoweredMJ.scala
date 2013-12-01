/*
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.power

import buildcraft.api.power.{PowerHandler, IPowerReceptor}
import net.bdew.gendustry.machines.PoweredMachine
import net.minecraftforge.common.ForgeDirection

trait TilePoweredMJ extends TilePoweredBase with IPowerReceptor {
  val powerHandler = new PowerHandler(this, PowerHandler.Type.MACHINE)

  def doWork(workProvider: PowerHandler) {}
  def getPowerReceiver(side: ForgeDirection) = powerHandler.getPowerReceiver
  def getWorld = worldObj

  serverTick.listen(() => {
    if (power.stored < power.capacity && powerHandler.getEnergyStored > 0) {
      // this is needed because perdition is stupid and can get applied after getEnergyStored and ninja-reduce the value >.>
      val canSend = powerHandler.useEnergy(0, power.capacity - power.stored, false)
      val transferred = power.inject(canSend, false)
      powerHandler.useEnergy(transferred, transferred, true)
    }
  })

  persistSave.listen(t => powerHandler.writeToNBT(t, "bcPower"))
  persistLoad.listen(t => if (t.hasKey("bcPower")) powerHandler.readFromNBT(t, "bcPower"))

  override def configurePower(cfg: PoweredMachine) {
    super.configurePower(cfg)
    powerHandler.configure(cfg.minReceivedEnergy, cfg.maxReceivedEnergy, cfg.activationEnergy, cfg.maxReceivedEnergy * 10)
    powerHandler.configurePowerPerdition(cfg.powerLoss, cfg.powerLossInterval)
  }
}
