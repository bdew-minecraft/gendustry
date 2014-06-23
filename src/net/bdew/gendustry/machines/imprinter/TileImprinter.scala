/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.machines.imprinter

import net.bdew.gendustry.config.Items
import net.minecraft.item.ItemStack
import forestry.api.genetics.AlleleManager
import net.minecraft.nbt.NBTTagCompound
import forestry.api.apiculture.{EnumBeeType, IBeeRoot, IBee}
import scala.util.Random
import net.bdew.lib.power.TileItemProcessor
import net.bdew.gendustry.power.TilePowered
import net.bdew.gendustry.items.GeneTemplate
import net.bdew.gendustry.apiimpl.TileWorker
import net.bdew.lib.covers.TileCoverable
import net.minecraftforge.common.util.ForgeDirection

class TileImprinter extends TileItemProcessor with TileWorker with TilePowered with TileCoverable {
  lazy val cfg = MachineImprinter
  val outputSlots = Seq(slots.outIndividual)

  object slots {
    val inTemplate = 0
    val inLabware = 1
    val inIndividual = 2
    val outIndividual = 3
  }

  def getSizeInventory = 4

  def canStart =
    getStackInSlot(slots.inTemplate) != null &&
      getStackInSlot(slots.inLabware) != null &&
      getStackInSlot(slots.inIndividual) != null

  def tryStart(): Boolean = {
    if (canStart) {

      val individual = AlleleManager.alleleRegistry.getIndividual(getStackInSlot(slots.inIndividual))
      val genome = individual.getGenome.getChromosomes
      val root = GeneTemplate.getSpecies(getStackInSlot(slots.inTemplate))

      if (root != individual.getGenome.getSpeciesRoot) return false

      if (individual.isInstanceOf[IBee]) {
        if (root.asInstanceOf[IBeeRoot].getType(getStackInSlot(slots.inIndividual)) != EnumBeeType.DRONE) {
          val random = new Random()
          if (individual.asInstanceOf[IBee].isNatural) {
            if (random.nextInt(100) < cfg.deathChanceNatural) {
              doStart(new ItemStack(Items.waste))
              return true
            }
          } else {
            if (random.nextInt(100) < cfg.deathChanceArtificial) {
              doStart(new ItemStack(Items.waste))
              return true
            }
          }
        }
      }

      val primary = genome.map(x => if (x == null) null else x.getPrimaryAllele)
      val secondary = genome.map(x => if (x == null) null else x.getSecondaryAllele)

      for (x <- GeneTemplate.getSamples(getStackInSlot(slots.inTemplate))) {
        primary(x.chromosome) = x.allele
        secondary(x.chromosome) = x.allele
      }

      val newStack = getStackInSlot(slots.inIndividual).copy()
      newStack.stackSize = 1

      val newTag = new NBTTagCompound()
      root.templateAsGenome(primary, secondary).writeToNBT(newTag)
      newStack.getTagCompound.setTag("Genome", newTag)

      doStart(newStack)

      return true

    } else return false
  }

  def doStart(s: ItemStack) {
    output := s
    decrStackSize(slots.inIndividual, 1)
    if (worldObj.rand.nextInt(100) < cfg.labwareConsumeChance)
      decrStackSize(slots.inLabware, 1)
  }

  override def isItemValidForSlot(slot: Int, stack: ItemStack): Boolean = {
    if (stack == null || stack.getItem == null) return false
    slot match {
      case slots.inTemplate =>
        return (stack.getItem == GeneTemplate) &&
          (inv(slots.inIndividual) == null || GeneTemplate.getSpecies(stack) == AlleleManager.alleleRegistry.getSpeciesRoot(inv(slots.inIndividual)))
      case slots.inLabware =>
        return stack.getItem == Items.labware
      case slots.inIndividual =>
        return (AlleleManager.alleleRegistry.getIndividual(stack) != null) &&
          (inv(slots.inTemplate) == null || GeneTemplate.getSpecies(inv(slots.inTemplate)) == AlleleManager.alleleRegistry.getSpeciesRoot(stack))
      case _ =>
        return false
    }
  }

  allowSided = true

  // can extract the template if input is empty and there's no operation in progress
  override def canExtractItem(slot: Int, item: ItemStack, side: Int) =
    slot == slots.outIndividual ||
      (slot == slots.inTemplate && inv(slots.inIndividual) == null && (output :== null))

  override def isValidCover(side: ForgeDirection, cover: ItemStack) = true
}