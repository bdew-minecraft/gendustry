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

import cpw.mods.fml.relauncher.{Side, SideOnly}
import forestry.api.core.IToolScoop
import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.config.Tuning
import net.bdew.gendustry.power.ItemPowered
import net.bdew.lib.Misc
import net.bdew.lib.items.{ItemUtils, NamedItem}
import net.minecraft.block.Block
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack, ItemTool}
import net.minecraft.world.World
import net.minecraftforge.common.ForgeHooks

object IndustrialScoop extends ItemTool(0, Item.ToolMaterial.IRON, new util.HashSet[Block]) with NamedItem with ItemPowered with IToolScoop {
  def name = "IndustrialScoop"
  lazy val cfg = Tuning.getSection("Items").getSection("IndustrialScoop")
  lazy val mjPerCharge = cfg.getInt("MjPerCharge")
  lazy val maxCharge = cfg.getInt("Charges") * mjPerCharge
  lazy val silktouchCharges = cfg.getInt("SilktouchCharges")

  setUnlocalizedName(Gendustry.modId + ".scoop")
  setMaxStackSize(1)
  setMaxDamage(101)

  efficiencyOnProperMaterial = 32

  setHarvestLevel("scoop", 3)

  override def getDigSpeed(stack: ItemStack, block: Block, meta: Int) =
    if (!hasCharges(stack))
      0.1F
    else super.getDigSpeed(stack, block, meta)

  override def onItemUse(stack: ItemStack, player: EntityPlayer, world: World, x: Int, y: Int, z: Int, side: Int, xOff: Float, yOff: Float, zOff: Float): Boolean = {
    if (!player.isSneaking) return false
    if (world.isRemote) return true
    val block = world.getBlock(x, y, z)
    if (block.getHarvestTool(world.getBlockMetadata(x, y, z)) == "scoop" && getCharge(stack) >= silktouchCharges && getCharge(stack) >= silktouchCharges) {
      useCharge(stack, silktouchCharges, player)
      ItemUtils.dropItemToPlayer(world, player, new ItemStack(block, 1, block.getDamageValue(world, x, y, z)))
      world.setBlockToAir(x, y, z)
      true
    } else false
  }

  override def onBlockDestroyed(stack: ItemStack, world: World, block: Block, x: Int, y: Int, z: Int, player: EntityLivingBase): Boolean = {
    if (ForgeHooks.isToolEffective(stack, block, world.getBlockMetadata(x, y, z))) {
      useCharge(stack, 1, player)
      return true
    }
    return false
  }

  override def hitEntity(stack: ItemStack, target: EntityLivingBase, player: EntityLivingBase): Boolean = false

  override def addInformation(stack: ItemStack, player: EntityPlayer, l: util.List[_], par4: Boolean) = {
    import scala.collection.JavaConverters._
    val tip = l.asInstanceOf[util.List[String]].asScala

    tip += Misc.toLocalF("gendustry.label.charges", getCharge(stack) / mjPerCharge)
  }

  override def getSubItems(item: Item, tabs: CreativeTabs, l: util.List[_]) {
    import scala.collection.JavaConverters._
    val items = l.asInstanceOf[util.List[ItemStack]].asScala
    items += new ItemStack(this)
    items += stackWithCharge(maxCharge)
  }

  override def getItemEnchantability: Int = 0
  override def getIsRepairable(stack1: ItemStack, stack2: ItemStack): Boolean = false
  override def isBookEnchantable(stack1: ItemStack, stack2: ItemStack): Boolean = false

  @SideOnly(Side.CLIENT)
  override def registerIcons(reg: IIconRegister) {
    itemIcon = reg.registerIcon(Gendustry.modId + ":scoop")
  }
}
