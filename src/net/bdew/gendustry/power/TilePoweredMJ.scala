/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.power

import buildcraft.api.power.{IPowerReceptor, PowerHandler}
import cpw.mods.fml.common.Optional
import net.bdew.gendustry.compat.PowerProxy
import net.bdew.gendustry.config.Tuning
import net.bdew.lib.machine.PoweredMachine
import net.bdew.lib.power.TilePoweredBase
import net.minecraftforge.common.util.ForgeDirection

@Optional.Interface(modid = PowerProxy.BC_MOD_ID, iface = "buildcraft.api.power.IPowerReceptor")
trait TilePoweredMJ extends TilePoweredBase with IPowerReceptor {
  lazy val powerHandler = new PowerHandler(this, PowerHandler.Type.MACHINE)

  def doWork(workProvider: PowerHandler) {}

  def getPowerReceiver(side: ForgeDirection) = if (PowerProxy.MJEnabled) powerHandler.getPowerReceiver else null

  def getWorld = getWorldObj

  if (PowerProxy.haveBC) {
    serverTick.listen(() => {
      if (power.stored < power.capacity && powerHandler.getEnergyStored > 0) {
        // this is needed because perdition is stupid and can get applied after getEnergyStored and ninja-reduce the value >.>
        val canSend = powerHandler.useEnergy(0, power.capacity - power.stored, false)
        val transferred = power.inject(canSend.toFloat, false)
        powerHandler.useEnergy(transferred, transferred, true)
      }
    })

    persistSave.listen(t => powerHandler.writeToNBT(t, "bcPower"))
    persistLoad.listen(t => if (t.hasKey("bcPower")) powerHandler.readFromNBT(t, "bcPower"))
  }

  override def configurePower(cfg: PoweredMachine) {
    super.configurePower(cfg)
    if (PowerProxy.haveBC) {
      val tune = Tuning.getSection("Power").getSection("BC")
      val minReceivedEnergy = cfg.maxReceivedEnergy / tune.getFloat("MinReceivedEnergyDivisor")
      val powerLoss = tune.getInt("PowerLoss")
      val powerLossInterval = tune.getInt("PowerLossInterval")
      powerHandler.configure(minReceivedEnergy, cfg.maxReceivedEnergy, cfg.activationEnergy, cfg.maxReceivedEnergy * 10)
      powerHandler.configurePowerPerdition(powerLoss, powerLossInterval)
    }
  }
}
