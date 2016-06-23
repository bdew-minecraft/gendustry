/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.gui

import net.bdew.gendustry.Gendustry
import net.bdew.lib.items.ItemUtils
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{EnumFacing, EnumHand}
import net.minecraft.world.World
import net.minecraftforge.fluids.{FluidContainerRegistry, IFluidHandler}

//@Optional.Interface(modid = "CoFHAPI|block", iface = "cofh.api.block.IDismantleable")
trait BlockGuiWrenchable extends Block /*with IDismantleable*/ {
  val guiId: Int
  // todo: Restore when cofh stuff is updated
  //  override def dismantleBlock(player: EntityPlayer, world: World,pos: BlockPos, returnDrops: Boolean): util.ArrayList[ItemStack] = {
  //    val item =
  //
  //      if (this.isInstanceOf[BlockKeepData]) {
  //        this.asInstanceOf[BlockKeepData].getSavedBlock(world, pos, world.getBlockMetadata(pos))
  //      } else {
  //        val te = world.getTileEntity(pos)
  //
  //        if (te != null && te.isInstanceOf[BreakableInventoryTile])
  //          te.asInstanceOf[BreakableInventoryTile].dropItems()
  //
  //        new ItemStack(this)
  //      }
  //
  //    world.setBlockToAir(pos)
  //
  //    val ret = new util.ArrayList[ItemStack]()
  //
  //    if (returnDrops)
  //      ret.add(item)
  //    else
  //      ItemUtils.throwItemAt(world, pos, item)
  //
  //    ret
  //  }
  //
  //  override def canDismantle(player: EntityPlayer, world: World,pos: BlockPos): Boolean = true

  def tryFluidInteract(world: World, pos: BlockPos, player: EntityPlayer, side: EnumFacing): Boolean = {
    val tileEntity = world.getTileEntity(pos)
    val activeItem = player.getActiveItemStack
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

  override def onBlockActivated(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, hand: EnumHand, heldItem: ItemStack, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean = {
    // If the click can be handled by something else - ignore it
    if (super.onBlockActivated(world, pos, state, player, hand, heldItem, side, hitX, hitY, hitZ)) return true
    if (player.isSneaking) {
      val equipped = if (player.getActiveItemStack != null) player.getActiveItemStack.getItem else null
      // Todo: Re-enable when BC is available
      //      if (equipped.isInstanceOf[IToolWrench] && equipped.asInstanceOf[IToolWrench].canWrench(player, pos)) {
      //        if (!world.isRemote) world.destroyBlock(pos, true)
      //        return true
      //      }
      return false
    } else if (tryFluidInteract(world, pos, player, side)) {
      return true
    } else {
      if (!world.isRemote) player.openGui(Gendustry.instance, guiId, world, pos.getX, pos.getY, pos.getZ)
      return true
    }
  }
}
