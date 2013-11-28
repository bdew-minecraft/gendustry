/*
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.machines

import net.bdew.lib.tile.TileExtended
import net.bdew.lib.data.base.{UpdateKind, TileDataSlots}
import net.bdew.lib.tile.inventory.{BreakableInventoryTile, SidedInventory, PersistentInventoryTile}
import net.bdew.gendustry.data.{DataSlotPower, ExposePower}
import buildcraft.api.power.PowerHandler.Type
import net.bdew.lib.data.DataSlotFloat
import net.minecraftforge.common.ForgeDirection

abstract class TileBaseProcessor extends TileExtended
with TileDataSlots
with PersistentInventoryTile
with BreakableInventoryTile
with SidedInventory
with ExposePower {
  def cfg: ProcessorMachine
  val power = DataSlotPower("power", this, Type.MACHINE).configure(cfg)
  val progress = DataSlotFloat("progress", this).setUpdate(UpdateKind.SAVE, UpdateKind.GUI)

  def getPowerDataslot(from: ForgeDirection): DataSlotPower = power

  override def tickServer() {
    if (power.getEnergyStored > cfg.activationEnergy) {

      if (!isWorking)
        if (tryStart())
          progress := 0

      if (isWorking) {

        if ((progress < 1) && (power.getEnergyStored > cfg.activationEnergy)) {
          val maxConsume = Math.min(Math.max(cfg.powerUseRate * power.getEnergyStored, cfg.activationEnergy), cfg.mjPerItem * (1 - progress))
          val consumed = power.useEnergy(0, maxConsume, true)
          progress += consumed / cfg.mjPerItem
        }

        if (progress >= 1) {
          if (tryFinish())
            progress := 0
        }
      }
    }
  }

  /**
   * Return true when an operation is in progress
   */
  def isWorking: Boolean

  /**
   * Try starting a new operation, return true if succesful
   */
  def tryStart(): Boolean

  /**
   * Perform output when operation is done, return true if succesful
   */
  def tryFinish(): Boolean
}
