/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.gui

import buildcraft.api.tools.IToolWrench
import cofh.api.block.IDismantleable
import cpw.mods.fml.common.Optional
import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.compat.PowerProxy
import net.bdew.lib.items.ItemUtils
import net.bdew.lib.tile.inventory.BreakableInventoryTile
import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.common.util.ForgeDirection
import net.minecraftforge.fluids.{FluidContainerRegistry, IFluidHandler}

@Optional.Interface(modid = "CoFHAPI|block", iface = "cofh.api.block.IDismantleable")
trait BlockGuiWrenchable extends Block with IDismantleable {
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
        val fStack = fluidHandler.drain(side, Int.MaxValue, false)
        if (fStack != null) {
          val filled = FluidContainerRegistry.fillFluidContainer(fStack, activeItem)
          if (filled != null) {
            fluidHandler.drain(side, FluidContainerRegistry.getFluidForFilledItem(filled), true)
            player.inventory.decrStackSize(player.inventory.currentItem, 1)
            ItemUtils.dropItemToPlayer(world, player, filled)
            return true
          }
        }
      } else if (FluidContainerRegistry.isFilledContainer(activeItem)) {
        val fStack = FluidContainerRegistry.getFluidForFilledItem(activeItem)
        if (fStack != null && (fluidHandler.fill(side, fStack, false) == fStack.amount)) {
          fluidHandler.fill(side, fStack, true)
          val cont = activeItem.getItem.getContainerItem(activeItem)
          player.inventory.decrStackSize(player.inventory.currentItem, 1)
          if (cont != null) ItemUtils.dropItemToPlayer(world, player, cont)
          return true
        }
      }
    }
    return false
  }

  override def onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, side: Int, xOffs: Float, yOffs: Float, zOffs: Float): Boolean = {
    // If the click can be handled by something else - ignore it
    if (super.onBlockActivated(world, x, y, z, player, side, xOffs, yOffs, zOffs)) return true
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
