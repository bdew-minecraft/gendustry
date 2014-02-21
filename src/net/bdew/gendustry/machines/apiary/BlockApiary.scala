/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.machines.apiary

import net.bdew.gendustry.Gendustry
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IconRegister
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.Icon
import net.minecraft.world.{IBlockAccess, World}
import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import net.bdew.lib.block.HasTE
import net.bdew.lib.tile.inventory.BreakableInventoryBlock
import net.bdew.gendustry.config.Machines
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.bdew.gendustry.gui.BlockGuiWrenchable

class BlockApiary(id: Int) extends Block(id, Material.rock) with HasTE[TileApiary] with BlockGuiWrenchable with BreakableInventoryBlock {
  private var icons: Array[Icon] = null
  val TEClass = classOf[TileApiary]
  lazy val guiId: Int = Machines.apiary.guiId

  setUnlocalizedName(Gendustry.modId + ".apiary")
  setHardness(5)

  override def getIcon(side: Int, meta: Int): Icon = if (side < 2) icons(0) else icons(1)

  @SideOnly(Side.CLIENT)
  override def registerIcons(reg: IconRegister) {
    icons = new Array[Icon](2)
    icons(0) = reg.registerIcon(Gendustry.modId + ":apiary/top")
    icons(1) = reg.registerIcon(Gendustry.modId + ":apiary/side")
  }

  override def getLightValue(world: IBlockAccess, x: Int, y: Int, z: Int): Int = {
    val block: Block = Block.blocksList(world.getBlockId(x, y, z))
    if (block != null && block != this)
      return block.getLightValue(world, x, y, z)
    else if (world.getBlockTileEntity(x, y, z) != null && getTE(world, x, y, z).hasLight)
      return 15
    else
      return 0
  }

  override def onBlockPlacedBy(world: World, x: Int, y: Int, z: Int, player: EntityLivingBase, stack: ItemStack) {
    if (player.isInstanceOf[EntityPlayerMP])
      getTE(world, x, y, z).owner := player.asInstanceOf[EntityPlayerMP].username
  }
}