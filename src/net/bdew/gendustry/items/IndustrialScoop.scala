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

import forestry.api.core.IToolScoop
import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.config.Tuning
import net.bdew.gendustry.power.ItemPowered
import net.bdew.lib.Misc
import net.bdew.lib.items.{BaseItemMixin, ItemUtils}
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack, ItemTool}
import net.minecraft.util.{BlockPos, EnumFacing}
import net.minecraft.world.World
import net.minecraftforge.common.ForgeHooks

object IndustrialScoop extends ItemTool(0, Item.ToolMaterial.IRON, new util.HashSet[Block]) with BaseItemMixin with ItemPowered with IToolScoop {
  val name = "IndustrialScoop"
  lazy val cfg = Tuning.getSection("Items").getSection("IndustrialScoop")
  lazy val mjPerCharge = cfg.getInt("MjPerCharge")
  lazy val maxCharge = cfg.getInt("Charges") * mjPerCharge
  lazy val silktouchCharges = cfg.getInt("SilktouchCharges")

  setUnlocalizedName(Gendustry.modId + ".scoop")
  setMaxStackSize(1)
  setMaxDamage(101)

  efficiencyOnProperMaterial = 32

  setHarvestLevel("scoop", 3)

  override def getDigSpeed(stack: ItemStack, state: IBlockState): Float =
    if (!hasCharges(stack))
      0.1F
    else
      super.getDigSpeed(stack, state)

  override def onItemUse(stack: ItemStack, player: EntityPlayer, world: World, pos: BlockPos, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean = {
    if (!player.isSneaking) return false
    if (world.isRemote) return true
    val state = world.getBlockState(pos)
    if (state.getBlock.getHarvestTool(state) == "scoop" && getCharge(stack) >= silktouchCharges && getCharge(stack) >= silktouchCharges) {
      useCharge(stack, silktouchCharges, player)
      ItemUtils.dropItemToPlayer(world, player, new ItemStack(state.getBlock, 1, state.getBlock.getDamageValue(world, pos)))
      world.setBlockToAir(pos)
      true
    } else false
  }

  override def onBlockDestroyed(stack: ItemStack, world: World, block: Block, pos: BlockPos, player: EntityLivingBase): Boolean = {
    if (ForgeHooks.isToolEffective(world, pos, stack)) {
      useCharge(stack, 1, player)
      return true
    }
    return false
  }

  override def hitEntity(stack: ItemStack, target: EntityLivingBase, player: EntityLivingBase): Boolean = false

  override def addInformation(stack: ItemStack, playerIn: EntityPlayer, tooltip: util.List[String], advanced: Boolean): Unit = {
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
}
