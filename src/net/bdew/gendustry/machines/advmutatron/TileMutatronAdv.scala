/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.machines.advmutatron

import net.bdew.gendustry.api.blocks.IAdvancedMutatron
import net.bdew.gendustry.apiimpl.TileWorker
import net.bdew.gendustry.compat.FakeMutatronBeeHousing
import net.bdew.gendustry.config.{Fluids, Items}
import net.bdew.gendustry.forestry.GeneticsHelper
import net.bdew.gendustry.power.TilePowered
import net.bdew.lib.block.TileKeepData
import net.bdew.lib.covers.TileCoverable
import net.bdew.lib.data.base.UpdateKind
import net.bdew.lib.data.{DataSlotGameProfile, DataSlotInt, DataSlotTankRestricted}
import net.bdew.lib.power.TileItemProcessor
import net.bdew.lib.tile.ExposeTank
import net.minecraft.item.ItemStack
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.fluids._

class TileMutatronAdv extends TileItemProcessor with TileWorker with TilePowered with ExposeTank with IAdvancedMutatron with TileCoverable with TileKeepData {
  lazy val cfg = MachineMutatronAdv
  val outputSlots = Seq(slots.outIndividual)

  object slots {
    val inIndividual1 = 0
    val inIndividual2 = 1
    val inLabware = 3
    val outIndividual = 2
    val selectors = 4 to 9
  }

  val tank = DataSlotTankRestricted("tank", this, cfg.tankSize, Fluids.mutagen)
  val selectedMutation = DataSlotInt("selected", this, -1).setUpdate(UpdateKind.SAVE, UpdateKind.GUI)
  val lastPlayer = DataSlotGameProfile("player", this).setUpdate(UpdateKind.SAVE)

  def getSizeInventory = 10

  def getTankFromDirection(dir: ForgeDirection): IFluidTank = tank

  override def getParent1 = getStackInSlot(slots.inIndividual1)
  override def getParent2 = getStackInSlot(slots.inIndividual2)
  override def getOwner = lastPlayer.value

  lazy val fakeBeeHousing = new FakeMutatronBeeHousing(this)

  override def markDirty() {
    updateSelectors()
    super.markDirty()
  }

  def updateSelectors() {
    if (worldObj != null && !worldObj.isRemote && !isWorking) {
      for (slot <- slots.selectors)
        inv(slot) = null
      selectedMutation := -1
      val valid = GeneticsHelper.getValidMutations(getStackInSlot(slots.inIndividual1), getStackInSlot(slots.inIndividual2), fakeBeeHousing)
      if (valid.nonEmpty) {
        for ((slot, mp) <- slots.selectors.zipWithIndex if valid.isDefinedAt(mp)) {
          inv(slot) = GeneticsHelper.getFinalMutationResult(valid(mp), getStackInSlot(slots.inIndividual1), false)
        }
      }
    }
  }

  override def setMutation(mutation: Int) {
    if (!isWorking && slots.selectors.contains(mutation) && inv(mutation) != null)
      selectedMutation := mutation
  }

  override def getPossibleMutations = {
    import scala.collection.JavaConverters._
    (slots.selectors map (x => Integer.valueOf(x) -> inv(x)) filterNot (_._2 != null)).toMap.asJava
  }

  def canStart =
    getStackInSlot(slots.inIndividual1) != null &&
      getStackInSlot(slots.inIndividual2) != null &&
      getStackInSlot(slots.inLabware) != null &&
      tank.getFluidAmount >= cfg.mutagenPerItem &&
      slots.selectors.contains(selectedMutation.value) &&
      inv(selectedMutation.value) != null

  def tryStart(): Boolean = {
    if (canStart) {
      output := GeneticsHelper.applyMutationDecayChance(getStackInSlot(selectedMutation.value), getStackInSlot(0))
      tank.drain(cfg.mutagenPerItem, true)
      if (lastPlayer.value != null)
        GeneticsHelper.addMutationToTracker(inv(0), inv(1), output, lastPlayer, worldObj)
      decrStackSize(slots.inIndividual1, 1)
      decrStackSize(slots.inIndividual2, 1)
      if (worldObj.rand.nextInt(100) < cfg.labwareConsumeChance)
        decrStackSize(slots.inLabware, 1)
      return true
    } else false
  }

  override def tryFinish() = {
    val v = super.tryFinish()
    updateSelectors()
    v
  }

  override def isItemValidForSlot(slot: Int, stack: ItemStack): Boolean = {
    slot match {
      case 0 =>
        return GeneticsHelper.isPotentialMutationPair(stack, getStackInSlot(1), fakeBeeHousing)
      case 1 =>
        return GeneticsHelper.isPotentialMutationPair(getStackInSlot(0), stack, fakeBeeHousing)
      case 3 =>
        return stack.getItem == Items.labware
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