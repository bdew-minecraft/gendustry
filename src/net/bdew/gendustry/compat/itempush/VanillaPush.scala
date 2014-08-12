/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.compat.itempush

import net.bdew.lib.Misc
import net.bdew.lib.items.ItemUtils
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.common.util.ForgeDirection

object VanillaPush extends ItemPushProxy {
  override def pushStack(from: TileEntity, dir: ForgeDirection, stack: ItemStack) =
    (for (target <- Misc.getNeighbourTile(from, dir, classOf[IInventory]) if stack != null) yield {
      val slots = ItemUtils.getAccessibleSlotsFromSide(target, dir.getOpposite)
      ItemUtils.addStackToSlots(stack, target, slots, true)
    }) getOrElse stack
}
