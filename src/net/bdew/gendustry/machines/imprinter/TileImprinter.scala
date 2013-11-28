/*
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.machines.imprinter

import net.bdew.gendustry.config.{Items, Machines}
import net.minecraft.item.ItemStack
import forestry.api.genetics.AlleleManager
import net.bdew.gendustry.machines.TileItemProcessor
import net.minecraft.nbt.NBTTagCompound
import forestry.api.apiculture.{EnumBeeType, IBeeRoot, IBee}
import scala.util.Random

class TileImprinter extends TileItemProcessor {
  lazy val cfg = Machines.imprinter
  val outputSlots = Seq(3)
  def getSizeInventory = 4

  def tryStart(): Boolean = {
    if (getStackInSlot(0) != null && getStackInSlot(1) != null && getStackInSlot(2) != null) {

      val individual = AlleleManager.alleleRegistry.getIndividual(getStackInSlot(2))
      val genome = individual.getGenome.getChromosomes
      val root = Items.geneTemplate.getSpecies(getStackInSlot(0))

      if (root != individual.getGenome.getSpeciesRoot) return false

      if (individual.isInstanceOf[IBee]) {
        if (root.asInstanceOf[IBeeRoot].getType(getStackInSlot(2)) != EnumBeeType.DRONE) {
          val random = new Random()
          if (individual.asInstanceOf[IBee].isNatural) {
            if (random.nextInt(100) < Machines.imprinter.deathChanceNatural) {
              doStart(new ItemStack(Items.waste))
              return true
            }
          } else {
            if (random.nextInt(100) < Machines.imprinter.deathChanceArtificial) {
              doStart(new ItemStack(Items.waste))
              return true
            }
          }
        }
      }

      val primary = genome.map(x => if (x == null) null else x.getPrimaryAllele)
      val secondary = genome.map(x => if (x == null) null else x.getSecondaryAllele)

      for (x <- Items.geneTemplate.getSamples(getStackInSlot(0))) {
        primary(x.chromosome) = x.allele
        secondary(x.chromosome) = x.allele
      }

      val newStack = getStackInSlot(2).copy()
      newStack.stackSize = 1

      val newTag = new NBTTagCompound()
      root.templateAsGenome(primary, secondary).writeToNBT(newTag)
      newStack.getTagCompound.setCompoundTag("Genome", newTag)

      doStart(newStack)

      return true

    } else return false
  }

  def doStart(s: ItemStack) {
    output := s
    decrStackSize(2, 1)
    if (worldObj.rand.nextInt(100) < cfg.labwareConsumeChance)
      decrStackSize(1, 1)
  }

  override def isItemValidForSlot(slot: Int, stack: ItemStack): Boolean = {
    if (stack == null || stack.getItem == null) return false
    slot match {
      case 0 =>
        return (stack.getItem == Items.geneTemplate) &&
          (inv(2) == null || Items.geneTemplate.getSpecies(stack) == AlleleManager.alleleRegistry.getSpeciesRoot(inv(2)))
      case 1 =>
        return stack.getItem == Items.labware
      case 2 =>
        return (AlleleManager.alleleRegistry.getIndividual(stack) != null) &&
          (inv(0) == null || Items.geneTemplate.getSpecies(inv(0)) == AlleleManager.alleleRegistry.getSpeciesRoot(stack))
      case _ =>
        return false
    }
  }

  allowSided = true

  // can extract the template if input is empty and there's no operation in progress
  override def canExtractItem(slot: Int, item: ItemStack, side: Int) = slot == 3 || (slot == 0 && inv(2) == null && (output :== null))
}