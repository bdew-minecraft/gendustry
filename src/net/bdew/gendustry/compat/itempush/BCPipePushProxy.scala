/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.compat.itempush

import buildcraft.api.transport.IPipeTile
import net.bdew.lib.Misc
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.common.util.ForgeDirection

object BCPipePushProxy extends ItemPushProxy {
  override def pushStack(from: TileEntity, dir: ForgeDirection, stack: ItemStack) =
    (for (pipe <- Misc.getNeighbourTile(from, dir, classOf[IPipeTile])) yield {
      if (pipe.getPipeType == IPipeTile.PipeType.ITEM) {
        val used = pipe.injectItem(stack, true, dir.getOpposite)
        if (used >= stack.stackSize)
          null
        else
          stack.splitStack(stack.stackSize - used)
      } else stack
    }).getOrElse(stack)
}
