/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.gui

import net.minecraft.world.World
import net.minecraft.entity.player.EntityPlayer
import buildcraft.api.tools.IToolWrench
import net.bdew.gendustry.Gendustry
import net.minecraft.block.Block
import cpw.mods.fml.common.Optional
import net.bdew.gendustry.compat.PowerProxy
import cofh.api.block.IDismantleable
import net.minecraft.item.ItemStack
import net.bdew.lib.tile.inventory.BreakableInventoryTile
import net.bdew.lib.items.ItemUtils

@Optional.Interface(modid = PowerProxy.TE_MOD_ID, iface = "cofh.api.block.IDismantleable")
trait BlockGuiWrenchable extends Block with IDismantleable {
  val guiId: Int

  def dismantleBlock(player: EntityPlayer, world: World, x: Int, y: Int, z: Int, returnBlock: Boolean): ItemStack = {
    val item = new ItemStack(this)
    val te = world.getBlockTileEntity(x, y, z)

    if (te != null && te.isInstanceOf[BreakableInventoryTile])
      te.asInstanceOf[BreakableInventoryTile].dropItems()

    world.setBlockToAir(x, y, z)

    if (!returnBlock)
      ItemUtils.throwItemAt(world, x, y, z, item)

    return item
  }

  def canDismantle(player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Boolean = true

  override def onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, side: Int, xoffs: Float, yoffs: Float, zoffs: Float): Boolean = {
    // If the click can be handled by something else - ignore it
    if (super.onBlockActivated(world, x, y, z, player, side, xoffs, yoffs, zoffs)) return true

    if (player.isSneaking) {
      val equipped = if (player.getCurrentEquippedItem != null) player.getCurrentEquippedItem.getItem else null
      if (equipped.isInstanceOf[IToolWrench] && equipped.asInstanceOf[IToolWrench].canWrench(player, x, y, z)) {
        if (!world.isRemote) world.destroyBlock(x, y, z, true)
        return true
      }
      return false
    } else {
      if (!world.isRemote) player.openGui(Gendustry.instance, guiId, world, x, y, z)
      return true
    }
  }
}
