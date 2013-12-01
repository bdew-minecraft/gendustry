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
    val transfer = power.inject(powerHandler.getEnergyStored, false)
    powerHandler.useEnergy(transfer, transfer, true)
  })

  override def configurePower(cfg: PoweredMachine) {
    super.configurePower(cfg)
    powerHandler.configure(cfg.minReceivedEnergy, cfg.maxReceivedEnergy, cfg.activationEnergy, cfg.maxStoredEnergy)
    powerHandler.configurePowerPerdition(cfg.powerLoss, cfg.powerLossInterval)
  }
}
