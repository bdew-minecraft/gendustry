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
import net.bdew.lib.block.HasTE
import net.bdew.lib.covers.BlockCoverable
import net.bdew.lib.tile.inventory.BreakableInventoryBlock
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.util.IIcon
import net.minecraft.world.{IBlockAccess, World}

object BlockApiary extends Block(Material.rock) with HasTE[TileApiary] with BlockCoverable[TileApiary] with BlockGuiWrenchable with BreakableInventoryBlock {
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

  override def onBlockPlacedBy(world: World, x: Int, y: Int, z: Int, player: EntityLivingBase, stack: ItemStack) {
    if (player.isInstanceOf[EntityPlayerMP])
      getTE(world, x, y, z).owner := player.asInstanceOf[EntityPlayerMP].getGameProfile
  }
}