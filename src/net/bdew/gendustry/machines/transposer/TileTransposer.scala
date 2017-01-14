/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.machines.transposer

import net.bdew.gendustry.apiimpl.TileWorker
import net.bdew.gendustry.config.Items
import net.bdew.gendustry.items.{GeneSample, GeneTemplate}
import net.bdew.gendustry.power.TilePowered
import net.bdew.lib.block.TileKeepData
import net.bdew.lib.covers.TileCoverable
import net.bdew.lib.items.IStack
import net.bdew.lib.power.TileItemProcessor
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing

class TileTransposer extends TileItemProcessor with TileWorker with TilePowered with TileCoverable with TileKeepData {
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
    !getStackInSlot(slots.inBlank).isEmpty &&
      !getStackInSlot(slots.inLabware).isEmpty &&
      !getStackInSlot(slots.inTemplate).isEmpty

  def tryStart(): Boolean = {
    if (canStart) {
      val tpl = getStackInSlot(slots.inTemplate)
      if (tpl.getItem == GeneSample) {
        output := Some(tpl.copy())
      } else if (tpl.getItem == GeneTemplate && GeneTemplate.getSamples(tpl) != null) {
        val tplItem = getStackInSlot(slots.inBlank).copy()
        GeneTemplate.getSamples(tpl).foreach(GeneTemplate.addSample(tplItem, _))
        output := Some(tplItem)
      } else return false

      decrStackSize(slots.inBlank, 1)
      if (world.rand.nextInt(100) < cfg.labwareConsumeChance)
        decrStackSize(slots.inLabware, 1)

      return true
    } else return false
  }

  def isValidInputs(blank: ItemStack, template: ItemStack) = (blank, template) match {
    case (IStack(Items.geneSampleBlank), x) if x.isEmpty => true
    case (IStack(Items.geneSampleBlank), IStack(GeneSample)) => true
    case (x, IStack(GeneSample)) if x.isEmpty => true
    case (IStack(GeneTemplate), x) if x.isEmpty => true
    case (x, IStack(GeneTemplate)) if x.isEmpty => GeneTemplate.getSpecies(template) != null
    case (IStack(GeneTemplate), IStack(GeneTemplate)) =>
      val bsp = GeneTemplate.getSpecies(blank)
      val tsp = GeneTemplate.getSpecies(template)
      tsp != null && (bsp == null || bsp == tsp)
    case _ => false
  }

  override def isItemValidForSlot(slot: Int, stack: ItemStack): Boolean = {
    if (stack.isEmpty) return false
    slot match {
      case slots.inLabware => stack.getItem == Items.labware
      case slots.inBlank => isValidInputs(stack, getStackInSlot(slots.inTemplate))
      case slots.inTemplate => isValidInputs(getStackInSlot(slots.inBlank), stack)
      case _ => return false
    }
  }

  allowSided = true
  override def canExtractItem(slot: Int, item: ItemStack, side: EnumFacing) =
    slot == slots.outCopy || (slot == slots.inTemplate && inv(slots.inBlank).isEmpty && output.isEmpty)

  override def isValidCover(side: EnumFacing, cover: ItemStack) = true
}