/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.machines.mutatron

import net.bdew.gendustry.config.{Fluids, Items}
import net.minecraft.item.ItemStack
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.fluids._
import net.bdew.lib.data.{DataSlotString, DataSlotTankRestricted}
import net.bdew.lib.tile.ExposeTank
import net.bdew.lib.power.TileItemProcessor
import net.bdew.gendustry.power.TilePowered
import net.bdew.lib.data.base.UpdateKind
import net.bdew.gendustry.forestry.GeneticsHelper
import net.bdew.gendustry.apiimpl.TileWorker
import net.bdew.lib.covers.TileCoverable

class TileMutatron extends TileItemProcessor with TileWorker with TilePowered with ExposeTank with TileCoverable {
  lazy val cfg = MachineMutatron
  val outputSlots = Seq(slots.outIndividual)

  object slots {
    val inIndividual1 = 0
    val inIndividual2 = 1
    val inLabware = 3
    val outIndividual = 2
  }

  val tank = DataSlotTankRestricted("tank", this, cfg.tankSize, Fluids.mutagen.getID)
  val lastPlayer = DataSlotString("player", this).setUpdate(UpdateKind.SAVE)

  def getSizeInventory = 4

  def getTankFromDirection(dir: ForgeDirection): IFluidTank = tank

  def canStart =
    getStackInSlot(slots.inIndividual1) != null &&
      getStackInSlot(slots.inIndividual2) != null &&
      getStackInSlot(slots.inLabware) != null &&
      tank.getFluidAmount >= cfg.mutagenPerItem

  def tryStart(): Boolean = {
    if (canStart) {
      output := GeneticsHelper.getMutationResult(getStackInSlot(slots.inIndividual1), getStackInSlot(slots.inIndividual2))
      tank.drain(cfg.mutagenPerItem, true)
      if (lastPlayer.cval != null && lastPlayer.cval > "")
        GeneticsHelper.addMutationToTracker(inv(slots.inIndividual1), inv(slots.inIndividual2), output, lastPlayer, worldObj)
      decrStackSize(slots.inIndividual1, 1)
      decrStackSize(slots.inIndividual2, 1)
      if (worldObj.rand.nextInt(100) < cfg.labwareConsumeChance)
        decrStackSize(slots.inLabware, 1)
      return true
    } else return false
  }

  override def isItemValidForSlot(slot: Int, itemstack: ItemStack): Boolean = {
    slot match {
      case slots.inIndividual1 =>
        return GeneticsHelper.isPotentialMutationPair(itemstack, getStackInSlot(slots.inIndividual2))
      case slots.inIndividual2 =>
        return GeneticsHelper.isPotentialMutationPair(getStackInSlot(slots.inIndividual1), itemstack)
      case slots.inLabware =>
        return itemstack.getItem == Items.labware
      case _ =>
        return false
    }
  }

  allowSided = true
  override def canExtractItem(slot: Int, item: ItemStack, side: Int) = slot == slots.outIndividual

  override def canDrain(from: ForgeDirection, fluid: Fluid): Boolean = false
  override def drain(from: ForgeDirection, resource: FluidStack, doDrain: Boolean): FluidStack = null
  override def drain(from: ForgeDirection, maxDrain: Int, doDrain: Boolean): FluidStack = null

  override def isValidCover(side: ForgeDirection, cover: ItemStack) = true
}