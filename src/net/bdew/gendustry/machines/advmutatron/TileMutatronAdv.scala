/*
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.machines.advmutatron

import net.bdew.gendustry.config.{Fluids, Items, Machines}
import net.minecraft.item.ItemStack
import net.minecraftforge.common.ForgeDirection
import net.minecraftforge.fluids._
import net.bdew.lib.data.{DataSlotInt, DataSlotTankRestricted}
import net.bdew.lib.tile.ExposeTank
import net.bdew.lib.power.TileItemProcessor
import net.bdew.gendustry.power.TilePowered
import net.bdew.gendustry.machines.mutatron.GeneticsHelper
import net.bdew.lib.data.base.UpdateKind

class TileMutatronAdv extends TileItemProcessor with TilePowered with ExposeTank {
  lazy val cfg = Machines.mutatronAdv
  val outputSlots = Seq(2)
  val selectorSlots = 4 to 9

  val tank = DataSlotTankRestricted("tank", this, cfg.tankSize, Fluids.mutagen.getID)
  val selectedMutation = DataSlotInt("selected", this, -1).setUpdate(UpdateKind.SAVE, UpdateKind.GUI)

  def getSizeInventory = 10

  def getTankFromDirection(dir: ForgeDirection): IFluidTank = tank

  override def onInventoryChanged() {
    updateSelectors()
    super.onInventoryChanged()
  }

  def updateSelectors() {
    if (worldObj != null && !worldObj.isRemote && !isWorking) {
      for (slot <- selectorSlots)
        inv(slot) = null
      selectedMutation := -1
      val valid = GeneticsHelper.getValidMutations(getStackInSlot(0), getStackInSlot(1))
      if (valid.size > 0) {
        for ((slot, mp) <- selectorSlots.zipWithIndex if valid.isDefinedAt(mp)) {
          inv(slot) = GeneticsHelper.getFinalMutationResult(valid(mp), getStackInSlot(0), false)
        }
      }
    }
  }

  def canStart =
    getStackInSlot(0) != null &&
      getStackInSlot(1) != null &&
      getStackInSlot(3) != null &&
      tank.getFluidAmount >= cfg.mutagenPerItem &&
      selectorSlots.contains(selectedMutation.cval) &&
      inv(selectedMutation.cval) != null

  def tryStart(): Boolean = {
    if (canStart) {
      output := GeneticsHelper.applyMutationDecayChance(getStackInSlot(selectedMutation.cval), getStackInSlot(0))
      tank.drain(cfg.mutagenPerItem, true)
      decrStackSize(0, 1)
      decrStackSize(1, 1)
      if (worldObj.rand.nextInt(100) < cfg.labwareConsumeChance)
        decrStackSize(3, 1)
      return true
    } else false
  }

  override def tryFinish() = {
    val v = super.tryFinish()
    updateSelectors()
    v
  }

  override def isItemValidForSlot(slot: Int, itemstack: ItemStack): Boolean = {
    slot match {
      case 0 =>
        return GeneticsHelper.isPotentialMutationPair(itemstack, getStackInSlot(1))
      case 1 =>
        return GeneticsHelper.isPotentialMutationPair(getStackInSlot(0), itemstack)
      case 3 =>
        return itemstack.getItem == Items.labware
      case _ =>
        return false
    }
  }

  allowSided = true
  override def canExtractItem(slot: Int, item: ItemStack, side: Int) = slot == 2

  override def canDrain(from: ForgeDirection, fluid: Fluid): Boolean = false
  override def drain(from: ForgeDirection, resource: FluidStack, doDrain: Boolean): FluidStack = null
  override def drain(from: ForgeDirection, maxDrain: Int, doDrain: Boolean): FluidStack = null
}