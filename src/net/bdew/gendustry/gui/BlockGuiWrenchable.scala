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
import net.minecraftforge.fluids.{IFluidHandler, FluidContainerRegistry}
import net.minecraftforge.common.util.ForgeDirection

//import cofh.api.block.IDismantleable

import net.minecraft.item.ItemStack
import net.bdew.lib.tile.inventory.BreakableInventoryTile
import net.bdew.lib.items.ItemUtils

//FIXME Reenable when TE support is readded
//@Optional.Interface(modid = PowerProxy.TE_MOD_ID, iface = "cofh.api.block.IDismantleable")
trait BlockGuiWrenchable extends Block /*with IDismantleable*/ {
  val guiId: Int

  def dismantleBlock(player: EntityPlayer, world: World, x: Int, y: Int, z: Int, returnBlock: Boolean): ItemStack = {
    val item = new ItemStack(this)
    val te = world.getTileEntity(x, y, z)

    if (te != null && te.isInstanceOf[BreakableInventoryTile])
      te.asInstanceOf[BreakableInventoryTile].dropItems()

    world.setBlockToAir(x, y, z)

    if (!returnBlock)
      ItemUtils.throwItemAt(world, x, y, z, item)

    return item
  }

  def canDismantle(player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Boolean = true

  def tryFluidInteract(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, side: ForgeDirection): Boolean = {
    val tileEntity = world.getTileEntity(x, y, z)
    val activeItem = player.getCurrentEquippedItem
    if (activeItem != null && tileEntity != null && tileEntity.isInstanceOf[IFluidHandler]) {
      val fluidHandler = tileEntity.asInstanceOf[IFluidHandler]
      if (FluidContainerRegistry.isEmptyContainer(activeItem)) {
        val fstack = fluidHandler.drain(side, Int.MaxValue, false)
        if (fstack != null) {
          val filled = FluidContainerRegistry.fillFluidContainer(fstack, activeItem)
          if (filled != null) {
            fluidHandler.drain(side, FluidContainerRegistry.getFluidForFilledItem(filled), true)
            player.inventory.decrStackSize(player.inventory.currentItem, 1)
            ItemUtils.dropItemToPlayer(world, player, filled)
            return true
          }
        }
      } else if (FluidContainerRegistry.isFilledContainer(activeItem)) {
        val fstack = FluidContainerRegistry.getFluidForFilledItem(activeItem)
        if (fstack != null && (fluidHandler.fill(side, fstack, false) == fstack.amount)) {
          fluidHandler.fill(side, fstack, true)
          val cont = activeItem.getItem.getContainerItem(activeItem)
          player.inventory.decrStackSize(player.inventory.currentItem, 1)
          if (cont != null) ItemUtils.dropItemToPlayer(world, player, cont)
          return true
        }
      }
    }
    return false
  }

  override def onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, side: Int, xoffs: Float, yoffs: Float, zoffs: Float): Boolean = {
    // If the click can be handled by something else - ignore it
    if (super.onBlockActivated(world, x, y, z, player, side, xoffs, yoffs, zoffs)) return true
    if (player.isSneaking) {
      val equipped = if (player.getCurrentEquippedItem != null) player.getCurrentEquippedItem.getItem else null
      if (equipped.isInstanceOf[IToolWrench] && equipped.asInstanceOf[IToolWrench].canWrench(player, x, y, z)) {
        if (!world.isRemote) world.func_147480_a(x, y, z, true) //destroyBlock
        return true
      }
      return false
    } else if (tryFluidInteract(world, x, y, z, player, ForgeDirection.values()(side))) {
      return true
    } else {
      if (!world.isRemote) player.openGui(Gendustry.instance, guiId, world, x, y, z)
      return true
    }
  }
}
