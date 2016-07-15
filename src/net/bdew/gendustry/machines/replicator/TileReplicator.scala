/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.machines.replicator

import net.bdew.gendustry.apiimpl.TileWorker
import net.bdew.gendustry.config.Fluids
import net.bdew.gendustry.forestry.GeneticsHelper
import net.bdew.gendustry.items.GeneTemplate
import net.bdew.gendustry.power.TilePowered
import net.bdew.lib.block.TileKeepData
import net.bdew.lib.capabilities.helpers.FluidMultiHandler
import net.bdew.lib.capabilities.legacy.OldFluidHandlerEmulator
import net.bdew.lib.capabilities.{Capabilities, CapabilityProvider}
import net.bdew.lib.covers.TileCoverable
import net.bdew.lib.data.DataSlotTankRestricted
import net.bdew.lib.power.TileItemProcessor
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing

class TileReplicator extends TileItemProcessor with TileWorker with TilePowered with TileCoverable with TileKeepData with CapabilityProvider with OldFluidHandlerEmulator {
  lazy val cfg = MachineReplicator
  val outputSlots = Seq(slots.outIndividual)

  object slots {
    val inTemplate = 0
    val outIndividual = 1
  }

  val dnaTank = DataSlotTankRestricted("dnaTank", this, cfg.dnaTankSize, Fluids.dna, canDrainExternal = false)
  val proteinTank = DataSlotTankRestricted("proteinTank", this, cfg.proteinTankSize, Fluids.protein, canDrainExternal = false)

  addCapability(Capabilities.CAP_FLUID_HANDLER, new FluidMultiHandler(List(dnaTank, proteinTank)))

  def getSizeInventory = 2

  def canStart =
    getStackInSlot(slots.inTemplate) != null &&
      proteinTank.getFluidAmount >= cfg.proteinPerItem &&
      dnaTank.getFluidAmount >= cfg.dnaPerItem &&
      getStackInSlot(slots.outIndividual) == null

  def tryStart(): Boolean = {
    if (canStart) {
      output := Some(GeneticsHelper.individualFromTemplate(getStackInSlot(slots.inTemplate), cfg.makePristineBees))
      dnaTank.drain(cfg.dnaPerItem, true)
      proteinTank.drain(cfg.proteinPerItem, true)
      return true
    } else return false
  }

  override def isItemValidForSlot(slot: Int, stack: ItemStack) =
    slot == slots.inTemplate && stack.getItem == GeneTemplate && GeneTemplate.isComplete(stack)

  allowSided = true
  override def canExtractItem(slot: Int, item: ItemStack, side: EnumFacing) = slot == slots.outIndividual

  override def isValidCover(side: EnumFacing, cover: ItemStack) = true
}