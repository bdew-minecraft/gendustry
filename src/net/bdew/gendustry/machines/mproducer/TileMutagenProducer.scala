/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.machines.mproducer

import net.bdew.gendustry.apiimpl.TileWorker
import net.bdew.gendustry.config.{Fluids, Items}
import net.bdew.gendustry.fluids.MutagenSources
import net.bdew.gendustry.machines.FluidPusher
import net.bdew.gendustry.power.TilePowered
import net.bdew.lib.block.TileKeepData
import net.bdew.lib.capabilities.legacy.OldFluidHandlerEmulator
import net.bdew.lib.capabilities.{Capabilities, CapabilityProvider}
import net.bdew.lib.covers.TileCoverable
import net.bdew.lib.data._
import net.bdew.lib.data.base.UpdateKind
import net.bdew.lib.power.TileBaseProcessor
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraftforge.fluids._

class TileMutagenProducer extends TileBaseProcessor with TileWorker with TilePowered with TileCoverable with TileKeepData with FluidPusher with CapabilityProvider with OldFluidHandlerEmulator {
  lazy val cfg = MachineMutagenProducer

  val tank = DataSlotTankRestricted("tank", this, cfg.tankSize, Fluids.mutagen, canFillExternal = false).setUpdate(UpdateKind.GUI, UpdateKind.SAVE)
  val output = DataSlotInt("output", this).setUpdate(UpdateKind.SAVE)

  addCapability(Capabilities.CAP_FLUID_HANDLER, tank)

  def getSizeInventory = 1

  def getTankFromDirection(dir: EnumFacing): IFluidTank = tank

  def isWorking = output > 0
  def tryStart(): Boolean = {
    if (getStackInSlot(0) != null) {
      output := MutagenSources.getValue(getStackInSlot(0))
      decrStackSize(0, 1)
      return true
    } else return false
  }

  def tryFinish(): Boolean = {
    if (tank.fillInternal(output, false) == output.value) {
      tank.fillInternal(output, true)
      output := -1
      return true
    } else return false
  }

  allowSided = true
  override def isItemValidForSlot(slot: Int, stack: ItemStack): Boolean = MutagenSources.getValue(stack) > 0
  override def canExtractItem(slot: Int, stack: ItemStack, side: EnumFacing): Boolean = false

  override def isValidCover(side: EnumFacing, cover: ItemStack) = cover.getItem == Items.coverImport
}