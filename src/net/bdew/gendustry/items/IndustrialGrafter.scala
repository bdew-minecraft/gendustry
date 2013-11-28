/*
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.items

import cofh.api.energy.IEnergyContainerItem
import net.minecraft.item.{ItemStack, EnumToolMaterial, ItemTool}
import forestry.api.arboriculture.IToolGrafter
import net.minecraft.world.World
import net.minecraft.entity.player.EntityPlayer
import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.config.Tuning
import net.minecraft.nbt.NBTTagCompound
import net.bdew.lib.Misc
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.entity.EntityLivingBase
import java.util
import net.minecraft.creativetab.CreativeTabs

class IndustrialGrafter(id: Int) extends ItemTool(id, 0, EnumToolMaterial.IRON, Array.empty[Block]) with IEnergyContainerItem with IToolGrafter {
  val cfg = Tuning.getSection("Items").getSection("IndustrialGrafter")
  val rfPerCharge = cfg.getInt("RfPerCharge")
  val maxCharge = cfg.getInt("Charges") * rfPerCharge

  setUnlocalizedName(Gendustry.modId + ".grafter")
  setMaxStackSize(1)
  setMaxDamage(101)

  efficiencyOnProperMaterial = 32

  def getCharge(stack: ItemStack): Int = {
    if (!stack.hasTagCompound) setCharge(stack, 0)
    return Misc.clamp(stack.getTagCompound.getInteger("charge"), 0, maxCharge)
  }

  def useCharge(stack: ItemStack, uses: Int = 1) {
    setCharge(stack, Misc.clamp(getCharge(stack) - uses * rfPerCharge, 0, maxCharge))
  }

  def setCharge(stack: ItemStack, charge: Int) {
    if (!stack.hasTagCompound) stack.setTagCompound(new NBTTagCompound())
    stack.getTagCompound.setInteger("charge", charge)
    updateDamage(stack)
  }

  def updateDamage(stack: ItemStack) {
    setDamage(stack, Misc.clamp((100 * (1 - getCharge(stack).toFloat / maxCharge)).round.toInt, 1, 100))
  }

  def stackWithCharge(charge: Int): ItemStack = {
    val n = new ItemStack(this)
    setCharge(n, charge)
    return n
  }

  override def setDamage(stack: ItemStack, damage: Int) = super.setDamage(stack, Misc.clamp(damage, 1, 100))

  override def getStrVsBlock(stack: ItemStack, block: Block, meta: Int): Float = getStrVsBlock(stack, block)
  override def getStrVsBlock(stack: ItemStack, block: Block): Float = {
    if (getCharge(stack) < rfPerCharge) return 0.1F
    if (block.blockMaterial == Material.leaves) return efficiencyOnProperMaterial
    return 0.1F
  }

  override def onBlockDestroyed(stack: ItemStack, world: World, blockId: Int, x: Int, y: Int, z: Int, player: EntityLivingBase): Boolean = {
    if (Block.blocksList(blockId).blockMaterial == Material.leaves) {
      val aoe = 2
      if (!world.isRemote && player.isInstanceOf[EntityPlayer]) {
        for (dx <- -1 * aoe to aoe;
             dy <- -1 * aoe to aoe;
             dz <- -1 * aoe to aoe
             if dy + y > 0 && dy + y < world.getHeight) {
          val bl = Block.blocksList(world.getBlockId(x + dx, y + dy, z + dz))
          if (bl != null && bl.blockMaterial == Material.leaves && getCharge(stack) > rfPerCharge) {
            bl.removeBlockByPlayer(world, player.asInstanceOf[EntityPlayer], x + dx, y + dy, z + dz)
            useCharge(stack)
          }
        }
      }
      useCharge(stack)
      return true
    }
    return false
  }

  override def addInformation(stack: ItemStack, player: EntityPlayer, l: util.List[_], par4: Boolean) = {
    import scala.collection.JavaConverters._
    val tip = l.asInstanceOf[util.List[String]].asScala

    tip += Misc.toLocalF("gendustry.label.charges", getCharge(stack) / rfPerCharge)
  }

  override def getSubItems(par1: Int, tabs: CreativeTabs, l: util.List[_]) {
    import scala.collection.JavaConverters._
    val items = l.asInstanceOf[util.List[ItemStack]].asScala
    items += new ItemStack(this)
    items += stackWithCharge(maxCharge)
  }

  override def getItemEnchantability: Int = 0
  override def getIsRepairable(par1ItemStack: ItemStack, par2ItemStack: ItemStack): Boolean = false
  override def isBookEnchantable(itemstack1: ItemStack, itemstack2: ItemStack): Boolean = false

  def receiveEnergy(container: ItemStack, maxReceive: Int, simulate: Boolean): Int = {
    val charge = getCharge(container)
    val canCharge = Misc.clamp(maxCharge - charge, 0, maxReceive)
    if (!simulate) setCharge(container, charge + canCharge)
    return canCharge
  }

  def extractEnergy(container: ItemStack, maxExtract: Int, simulate: Boolean): Int = 0
  def getEnergyStored(container: ItemStack): Int = getCharge(container)
  def getMaxEnergyStored(container: ItemStack): Int = maxCharge

  def getSaplingModifier(stack: ItemStack, world: World, player: EntityPlayer, x: Int, y: Int, z: Int): Float = if (getCharge(stack) > rfPerCharge) 100 else 0
}
