/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.machines.replicator

import net.bdew.gendustry.config.{Fluids, Items, Machines}
import net.minecraft.item.ItemStack
import net.minecraftforge.common.ForgeDirection
import net.minecraftforge.fluids._
import net.bdew.lib.data.DataSlotTankRestricted
import net.bdew.lib.power.TileItemProcessor
import net.bdew.gendustry.power.TilePowered
import forestry.api.genetics.IAllele
import forestry.api.apiculture.{IBee, EnumBeeType, IBeeRoot}
import forestry.api.arboriculture.{EnumGermlingType, ITreeRoot}
import forestry.api.lepidopterology.{IButterflyRoot, EnumFlutterType}

class TileReplicator extends TileItemProcessor with TilePowered with IFluidHandler {
  lazy val cfg = Machines.replicator
  val outputSlots = Seq(slots.outIndividual)

  object slots {
    val inTemplate = 0
    val outIndividual = 1
  }

  val dnaTank = DataSlotTankRestricted("dnaTank", this, cfg.dnaTankSize, Fluids.dna.getID)
  val proteinTank = DataSlotTankRestricted("proteinTank", this, cfg.proteinTankSize, Fluids.protein.getID)

  def getSizeInventory = 2

  def canStart =
    getStackInSlot(slots.inTemplate) != null &&
      proteinTank.getFluidAmount >= cfg.proteinPerItem &&
      dnaTank.getFluidAmount >= cfg.dnaPerItem &&
      getStackInSlot(slots.outIndividual) == null

  def tryStart(): Boolean = {
    if (canStart) {
      val tpl = getStackInSlot(slots.inTemplate)
      val root = Items.geneTemplate.getSpecies(tpl)
      val samples = Items.geneTemplate.getSamples(tpl)
      val template = new Array[IAllele](samples.map(_.chromosome).max + 1)
      samples.foreach(x => template(x.chromosome) = x.allele)
      val individual = root.templateAsIndividual(template)
      individual.analyze()
      output := (root match {
        case bees: IBeeRoot =>
          individual.asInstanceOf[IBee].setIsNatural(cfg.makePristineBees)
          bees.getMemberStack(individual, EnumBeeType.QUEEN.ordinal())
        case trees: ITreeRoot =>
          trees.getMemberStack(individual, EnumGermlingType.SAPLING.ordinal())
        case butterflies: IButterflyRoot =>
          butterflies.getMemberStack(individual, EnumFlutterType.BUTTERFLY.ordinal())
      })
      return true
    } else return false
  }

  override def isItemValidForSlot(slot: Int, itemstack: ItemStack) =
    slot == slots.inTemplate && itemstack.getItem == Items.geneTemplate && Items.geneTemplate.isComplete(itemstack)

  allowSided = true
  override def canExtractItem(slot: Int, item: ItemStack, side: Int) = slot == slots.outIndividual

  def fill(from: ForgeDirection, resource: FluidStack, doFill: Boolean) =
    if (resource.getFluid == Fluids.dna)
      dnaTank.fill(resource, doFill)
    else if (resource.getFluid == Fluids.protein)
      proteinTank.fill(resource, doFill)
    else 0

  def drain(from: ForgeDirection, resource: FluidStack, doDrain: Boolean) = null
  def drain(from: ForgeDirection, maxDrain: Int, doDrain: Boolean) = null
  def canFill(from: ForgeDirection, fluid: Fluid) = fluid == Fluids.dna || fluid == Fluids.protein
  def canDrain(from: ForgeDirection, fluid: Fluid) = false
  def getTankInfo(from: ForgeDirection) = Array(dnaTank.getInfo, proteinTank.getInfo)
}