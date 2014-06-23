/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.compat.itempush

import net.minecraft.tileentity.TileEntity
import net.minecraft.item.ItemStack
import net.bdew.lib.Misc
import cofh.api.transport.IItemDuct
import net.minecraftforge.common.util.ForgeDirection

object CofhConduitPushProxy extends ItemPushProxy {
  override def pushStack(from: TileEntity, dir: ForgeDirection, stack: ItemStack) =
    (for (conduit <- Misc.getNeighbourTile(from, dir, classOf[IItemDuct])) yield
      conduit.insertItem(dir.getOpposite, stack)
      ).getOrElse(stack)
}
