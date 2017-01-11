/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.gui

import net.bdew.gendustry.Gendustry
import net.bdew.lib.capabilities.helpers.FluidHelper
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{EnumFacing, EnumHand}
import net.minecraft.world.World

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
    for {
      tankHandler <- FluidHelper.getFluidHandler(world, pos, side)
      itemHandler <- FluidHelper.getFluidHandler(player.getHeldItemMainhand)
    } {
      if (itemHandler.getTankProperties.exists(x => x.canFill && x.getContents == null)) {
        if (FluidHelper.pushFluid(tankHandler, itemHandler) != null) return true
      } else if (itemHandler.getTankProperties.exists(x => x.canDrain && x.getContents != null && x.getContents.amount > 0)) {
        if (FluidHelper.pushFluid(itemHandler, tankHandler) != null) return true
      }
    }
    false
  }

  override def onBlockActivated(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean = {
    // If the click can be handled by something else - ignore it
    if (super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ)) return true
    if (player.isSneaking) {
      val equipped = if (player.getActiveItemStack != null) player.getActiveItemStack.getItem else null
      // Todo: Re-enable when BC is available
      //      if (equipped.isInstanceOf[IToolWrench] && equipped.asInstanceOf[IToolWrench].canWrench(player, pos)) {
      //        if (!world.isRemote) world.destroyBlock(pos, true)
      //        return true
      //      }
      return false
    } else if (tryFluidInteract(world, pos, player, facing)) {
      return true
    } else {
      if (!world.isRemote) player.openGui(Gendustry.instance, guiId, world, pos.getX, pos.getY, pos.getZ)
      return true
    }
  }
}
