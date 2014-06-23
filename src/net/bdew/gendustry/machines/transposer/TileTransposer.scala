/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.machines.transposer

import net.bdew.gendustry.config.Items
import net.minecraft.item.ItemStack
import net.bdew.lib.power.TileItemProcessor
import net.bdew.gendustry.power.TilePowered
import net.bdew.lib.items.IStack
import net.bdew.gendustry.items.{GeneTemplate, GeneSample}
import net.bdew.gendustry.apiimpl.TileWorker
import net.bdew.lib.covers.TileCoverable
import net.minecraftforge.common.util.ForgeDirection

class TileTransposer extends TileItemProcessor with TileWorker with TilePowered with TileCoverable {
  lazy val cfg = MachineTransposer

  val outputSlots = Seq(slots.outCopy)

  object slots {
    val inBlank = 0
    val inLabware = 1
    val inTemplate = 2
    val outCopy = 3
  }

  def getSizeInventory = 4

  def canStart =
    getStackInSlot(slots.inBlank) != null &&
      getStackInSlot(slots.inLabware) != null &&
      getStackInSlot(slots.inTemplate) != null

  def tryStart(): Boolean = {
    if (canStart) {
      val tpl = getStackInSlot(slots.inTemplate)
      if (tpl.getItem == GeneSample) {
        output := tpl.copy()
      } else if (tpl.getItem == GeneTemplate && GeneTemplate.getSamples(tpl) != null) {
        output := getStackInSlot(slots.inBlank).copy()
        GeneTemplate.getSamples(tpl).foreach(GeneTemplate.addSample(output, _))
      } else return false

      decrStackSize(slots.inBlank, 1)
      if (worldObj.rand.nextInt(100) < cfg.labwareConsumeChance)
        decrStackSize(slots.inLabware, 1)

      return true
    } else return false
  }

  def isValidInputs(blank: ItemStack, template: ItemStack) = (blank, template) match {
    case (IStack(Items.geneSampleBlank), null) => true
    case (IStack(Items.geneSampleBlank), IStack(GeneSample)) => true
    case (null, IStack(GeneSample)) => true
    case (IStack(GeneTemplate), null) => true
    case (null, IStack(GeneTemplate)) => GeneTemplate.getSpecies(template) != null
    case (IStack(GeneTemplate), IStack(GeneTemplate)) =>
      val bsp = GeneTemplate.getSpecies(blank)
      val tsp = GeneTemplate.getSpecies(template)
      tsp != null && (bsp == null || bsp == tsp)
    case _ => false
  }

  override def isItemValidForSlot(slot: Int, itemstack: ItemStack): Boolean = {
    if (itemstack == null || itemstack.getItem == null) return false
    slot match {
      case slots.inLabware => itemstack.getItem == Items.labware
      case slots.inBlank => isValidInputs(itemstack, getStackInSlot(slots.inTemplate))
      case slots.inTemplate => isValidInputs(getStackInSlot(slots.inBlank), itemstack)
      case _ => return false
    }
  }

  allowSided = true
  override def canExtractItem(slot: Int, item: ItemStack, side: Int) =
    slot == slots.outCopy || (slot == slots.inTemplate && inv(slots.inBlank) == null && (output :== null))

  override def isValidCover(side: ForgeDirection, cover: ItemStack) = true
}