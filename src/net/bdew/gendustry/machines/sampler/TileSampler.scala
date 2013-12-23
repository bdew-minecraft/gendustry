/*
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.machines.sampler

import net.bdew.gendustry.config.{Items, Machines}
import net.minecraft.item.ItemStack
import forestry.api.genetics.AlleleManager
import scala.util.Random
import net.bdew.gendustry.forestry.GeneSampleInfo
import net.bdew.lib.power.TileItemProcessor
import net.bdew.gendustry.power.TilePowered
import net.bdew.gendustry.compat.ExtraBeesProxy

class TileSampler extends TileItemProcessor with TilePowered {
  lazy val cfg = Machines.sampler
  val outputSlots = Seq(3)

  def getSizeInventory = 4

  def selectRandomAllele(stack: ItemStack): ItemStack = {
    if (cfg.convertEBSerums && ExtraBeesProxy.isSerum(stack)) {
      val sample = ExtraBeesProxy.getSerumSample(stack)
      if (sample != null) return Items.geneSample.newStack(sample)
    }

    val root = AlleleManager.alleleRegistry.getSpeciesRoot(stack)
    if (root == null) return new ItemStack(Items.waste)
    val member = root.getMember(stack)
    val genome = member.getGenome
    val chromosomes = genome.getChromosomes.zipWithIndex.filter(_._1 != null)
    val alleles = chromosomes.map({
      case (x, n) => Seq(n -> x.getPrimaryAllele, n -> x.getSecondaryAllele)
    }).flatten

    val rand = new Random()
    val (chr, allele) = alleles(rand.nextInt(alleles.length))
    return Items.geneSample.newStack(GeneSampleInfo(root, chr, allele))
  }

  def tryStart(): Boolean = {
    if (getStackInSlot(0) != null && getStackInSlot(1) != null && getStackInSlot(2) != null) {

      output := selectRandomAllele(getStackInSlot(2))

      decrStackSize(0, 1)
      decrStackSize(2, 1)
      if (worldObj.rand.nextInt(100) < cfg.labwareConsumeChance)
        decrStackSize(1, 1)

      return true
    } else return false
  }

  override def isItemValidForSlot(slot: Int, itemstack: ItemStack): Boolean = {
    if (itemstack == null || itemstack.getItem == null) return false
    slot match {
      case 0 =>
        return itemstack.getItem == Items.geneSampleBlank
      case 1 =>
        return itemstack.getItem == Items.labware
      case 2 =>
        return AlleleManager.alleleRegistry.getIndividual(itemstack) != null || (cfg.convertEBSerums && ExtraBeesProxy.isSerum(itemstack))
      case _ =>
        return false
    }
  }

  allowSided = true
  override def canExtractItem(slot: Int, item: ItemStack, side: Int) = slot == 3
}