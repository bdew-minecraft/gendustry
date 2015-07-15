/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.machines.apiary

import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.gui.BlockGuiWrenchable
import net.bdew.gendustry.misc.BlockTooltipHelper
import net.bdew.lib.block.{BlockKeepData, BlockTooltip, HasTE}
import net.bdew.lib.covers.BlockCoverable
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.player.{EntityPlayer, EntityPlayerMP}
import net.minecraft.item.ItemStack
import net.minecraft.util.IIcon
import net.minecraft.world.{IBlockAccess, World}

object BlockApiary extends Block(Material.rock) with HasTE[TileApiary] with BlockCoverable[TileApiary] with BlockGuiWrenchable with BlockTooltip with BlockKeepData {
  private var icons: Array[IIcon] = null
  val TEClass = classOf[TileApiary]
  lazy val guiId: Int = MachineApiary.guiId

  setBlockName(Gendustry.modId + ".apiary")
  setHardness(5)

  override def getIcon(side: Int, meta: Int): IIcon = if (side < 2) icons(0) else icons(1)

  @SideOnly(Side.CLIENT)
  override def registerBlockIcons(reg: IIconRegister) {
    icons = new Array[IIcon](2)
    icons(0) = reg.registerIcon(Gendustry.modId + ":apiary/top")
    icons(1) = reg.registerIcon(Gendustry.modId + ":apiary/side")
  }

  override def getLightValue(world: IBlockAccess, x: Int, y: Int, z: Int): Int = {
    val block = world.getBlock(x, y, z)
    if (block != null && block != this)
      return block.getLightValue(world, x, y, z)
    else if (world.getTileEntity(x, y, z) != null && getTE(world, x, y, z).hasLight)
      return 15
    else
      return 0
  }

  override def getTooltip(stack: ItemStack, player: EntityPlayer, advanced: Boolean): List[String] = {
    if (stack.hasTagCompound && stack.getTagCompound.hasKey("data")) {
      val data = stack.getTagCompound.getCompoundTag("data")
      val inv = BlockTooltipHelper.getInventory(data)

      List.empty ++
        (inv.get(0) map (_.getDisplayName)) ++
        (inv.get(1) map (_.getDisplayName)) ++
        BlockTooltipHelper.getPowerTooltip(data, "power") ++
        BlockTooltipHelper.getItemsTooltip(data)

    } else List.empty
  }

  override def restoreTileEntity(world: World, x: Int, y: Int, z: Int, is: ItemStack, player: EntityPlayer): Unit = {
    super.restoreTileEntity(world, x, y, z, is, player)
    if (player.isInstanceOf[EntityPlayerMP])
      getTE(world, x, y, z).owner := player.asInstanceOf[EntityPlayerMP].getGameProfile
  }

  override def canConnectRedstone(world: IBlockAccess, x: Int, y: Int, z: Int, side: Int) = true
}