/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.items

import java.util

import forestry.api.core.IToolScoop
import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.config.Tuning
import net.bdew.gendustry.power.ItemPowered
import net.bdew.lib.Misc
import net.bdew.lib.items.{BaseItemMixin, BaseTool, ItemUtils}
import net.minecraft.block.state.IBlockState
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{EnumActionResult, EnumFacing, EnumHand, NonNullList}
import net.minecraft.world.World
import net.minecraftforge.common.ForgeHooks

object IndustrialScoop extends BaseTool("industrial_scoop", Item.ToolMaterial.IRON) with BaseItemMixin with ItemPowered with IToolScoop {
  lazy val cfg = Tuning.getSection("Items").getSection("IndustrialScoop")
  lazy val mjPerCharge = cfg.getInt("MjPerCharge")
  lazy val maxCharge = cfg.getInt("Charges") * mjPerCharge
  lazy val silktouchCharges = cfg.getInt("SilktouchCharges")

  setUnlocalizedName(Gendustry.modId + ".scoop")
  setMaxStackSize(1)
  setMaxDamage(101)

  efficiencyOnProperMaterial = 32

  setHarvestLevel("scoop", 3)

  override def getStrVsBlock(stack: ItemStack, state: IBlockState): Float =
    if (!hasCharges(stack))
      0.1F
    else
      super.getStrVsBlock(stack, state)

  override def onItemUse(player: EntityPlayer, world: World, pos: BlockPos, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult = {
    val stack = player.getHeldItem(hand)
    if (!player.isSneaking) return EnumActionResult.PASS
    if (world.isRemote) return EnumActionResult.SUCCESS
    val state = world.getBlockState(pos)
    if (state.getBlock.getHarvestTool(state) == "scoop" && getCharge(stack) >= silktouchCharges && getCharge(stack) >= silktouchCharges) {
      useCharge(stack, silktouchCharges, player)
      ItemUtils.dropItemToPlayer(world, player, new ItemStack(state.getBlock, 1, state.getBlock.damageDropped(state)))
      world.setBlockToAir(pos)
      EnumActionResult.SUCCESS
    } else EnumActionResult.FAIL
  }

  override def onBlockDestroyed(stack: ItemStack, world: World, state: IBlockState, pos: BlockPos, player: EntityLivingBase): Boolean = {
    if (ForgeHooks.isToolEffective(world, pos, stack)) {
      useCharge(stack, 1, player)
      true
    } else false
  }

  override def hitEntity(stack: ItemStack, target: EntityLivingBase, player: EntityLivingBase): Boolean = false

  override def addInformation(stack: ItemStack, world: World, tooltip: util.List[String], flags: ITooltipFlag): Unit = {
    tooltip.add(Misc.toLocalF("gendustry.label.charges", getCharge(stack) / mjPerCharge))
  }

  override def getSubItems(tab: CreativeTabs, subItems: NonNullList[ItemStack]): Unit = {
    subItems.add(new ItemStack(this))
    subItems.add(stackWithCharge(maxCharge))
  }

  override def getItemEnchantability: Int = 0
  override def getIsRepairable(stack1: ItemStack, stack2: ItemStack): Boolean = false
  override def isBookEnchantable(stack1: ItemStack, stack2: ItemStack): Boolean = false
}
