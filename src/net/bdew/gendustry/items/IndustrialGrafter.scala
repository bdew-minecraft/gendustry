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
import forestry.api.arboriculture.IToolGrafter
import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.config.Tuning
import net.bdew.gendustry.power.ItemPowered
import net.bdew.lib.Misc
import net.bdew.lib.items.NamedItem
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack, ItemTool}
import net.minecraft.world.World

object IndustrialGrafter extends ItemTool(0, Item.ToolMaterial.IRON, new util.HashSet[Block]) with NamedItem with ItemPowered with IToolGrafter {
  def name = "IndustrialGrafter"
  lazy val cfg = Tuning.getSection("Items").getSection("IndustrialGrafter")
  lazy val mjPerCharge = cfg.getInt("MjPerCharge")
  lazy val maxCharge = cfg.getInt("Charges") * mjPerCharge
  lazy val aoe = cfg.getInt("AOE")
  lazy val saplingModifier = cfg.getFloat("SaplingModifier")

  setUnlocalizedName(Gendustry.modId + ".grafter")
  setMaxStackSize(1)
  setMaxDamage(101)

  efficiencyOnProperMaterial = 32

  override def getDigSpeed(stack: ItemStack, block: Block, meta: Int) =
    if (!hasCharges(stack))
      0.1F
    else if (block.getMaterial == Material.leaves)
      efficiencyOnProperMaterial
    else
      0.1F

  override def onBlockDestroyed(stack: ItemStack, world: World, block: Block, x: Int, y: Int, z: Int, player: EntityLivingBase): Boolean = {
    if (block.getMaterial == Material.leaves) {
      if (!world.isRemote && player.isInstanceOf[EntityPlayer] && !player.isSneaking) {
        for (dx <- -1 * aoe to aoe;
             dy <- -1 * aoe to aoe;
             dz <- -1 * aoe to aoe
             if dy + y > 0 && dy + y < world.getHeight && (dx != 0 || dy != 0 || dz != 0)) {
          val bl = world.getBlock(x + dx, y + dy, z + dz)
          if (bl != null && bl.getMaterial == Material.leaves && hasCharges(stack)) {
            val meta = world.getBlockMetadata(x + dx, y + dy, z + dz)
            bl.onBlockHarvested(world, x + dx, y + dy, z + dz, meta, player.asInstanceOf[EntityPlayer])
            if (bl.removedByPlayer(world, player.asInstanceOf[EntityPlayer], x + dx, y + dy, z + dz, false)) {
              useCharge(stack, 1, player)
              bl.harvestBlock(world, player.asInstanceOf[EntityPlayer], x + dx, y + dy, z + dz, meta)
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

  def getSaplingModifier(stack: ItemStack, world: World, player: EntityPlayer, x: Int, y: Int, z: Int): Float = if (hasCharges(stack)) saplingModifier else 0

  @SideOnly(Side.CLIENT)
  override def registerIcons(reg: IIconRegister) {
    itemIcon = reg.registerIcon(Gendustry.modId + ":grafter")
  }
}
