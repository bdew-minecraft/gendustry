/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.items

import java.util

import forestry.api.arboriculture.IToolGrafter
import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.config.Tuning
import net.bdew.gendustry.power.ItemPowered
import net.bdew.lib.Misc
import net.bdew.lib.PimpVanilla._
import net.bdew.lib.items.BaseTool
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

object IndustrialGrafter extends BaseTool("IndustrialGrafter", Item.ToolMaterial.IRON) with ItemPowered with IToolGrafter {
  lazy val cfg = Tuning.getSection("Items").getSection("IndustrialGrafter")
  lazy val mjPerCharge = cfg.getInt("MjPerCharge")
  lazy val maxCharge = cfg.getInt("Charges") * mjPerCharge
  lazy val aoe = cfg.getInt("AOE")
  lazy val saplingModifier = cfg.getFloat("SaplingModifier")

  setUnlocalizedName(Gendustry.modId + ".grafter")
  setMaxStackSize(1)
  setMaxDamage(101)

  efficiencyOnProperMaterial = 32

  override def getStrVsBlock(stack: ItemStack, state: IBlockState): Float =
    if (!hasCharges(stack))
      0.1F
    else if (state.getBlock.getMaterial(state) == Material.LEAVES)
      efficiencyOnProperMaterial
    else
      0.1F

  override def onBlockDestroyed(stack: ItemStack, world: World, state: IBlockState, pos: BlockPos, player: EntityLivingBase): Boolean = {
    if (state.getBlock.getMaterial(state) == Material.LEAVES) {
      if (!world.isRemote && player.isInstanceOf[EntityPlayer] && !player.isSneaking) {
        for (dx <- -1 * aoe to aoe;
             dy <- -1 * aoe to aoe;
             dz <- -1 * aoe to aoe
             if dy + pos.getY > 0 && dy + pos.getY < world.getHeight && (dx != 0 || dy != 0 || dz != 0)) {
          val target = pos.add(dx, dy, dz)
          val targetState = world.getBlockState(target)
          val targetBlock = targetState.getBlock
          if (targetBlock != null && targetBlock.getMaterial(targetState) == Material.LEAVES && hasCharges(stack)) {
            targetBlock.onBlockHarvested(world, target, targetState, player.asInstanceOf[EntityPlayer])
            if (targetBlock.removedByPlayer(targetState, world, target, player.asInstanceOf[EntityPlayer], false)) {
              useCharge(stack, 1, player)
              targetBlock.harvestBlock(world, player.asInstanceOf[EntityPlayer], target, targetState, world.getTileEntity(target), stack)
            }
          }
        }
      }
      useCharge(stack, 1, player)
      return true
    } else
      return false
  }

  override def hitEntity(stack: ItemStack, target: EntityLivingBase, player: EntityLivingBase): Boolean = false

  override def addInformation(stack: ItemStack, player: EntityPlayer, tooltip: util.List[String], advanced: Boolean): Unit = {
    tooltip.add(Misc.toLocalF("gendustry.label.charges", getCharge(stack) / mjPerCharge))
  }

  override def getSubItems(item: Item, tabs: CreativeTabs, l: util.List[ItemStack]) {
    import scala.collection.JavaConverters._
    val items = l.asInstanceOf[util.List[ItemStack]].asScala
    items += new ItemStack(this)
    items += stackWithCharge(maxCharge)
  }

  override def getItemEnchantability: Int = 0
  override def getIsRepairable(stack1: ItemStack, stack2: ItemStack): Boolean = false
  override def isBookEnchantable(stack1: ItemStack, stack2: ItemStack): Boolean = false

  def getSaplingModifier(stack: ItemStack, world: World, player: EntityPlayer, pos: BlockPos): Float = if (hasCharges(stack)) saplingModifier else 0
}
