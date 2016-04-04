/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.items.covers

import net.bdew.gendustry.compat.itempush.ItemPush
import net.bdew.lib.covers.{ItemCover, TileCoverable}
import net.bdew.lib.items.BaseItem
import net.minecraft.inventory.{IInventory, ISidedInventory}
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing

object EjectCover extends BaseItem("EjectCover") with ItemCover {
  override def isCoverTicking: Boolean = true

  override def isValidTile(te: TileCoverable, stack: ItemStack) = te.isInstanceOf[ISidedInventory with IInventory]

  override def tickCover(te: TileCoverable, side: EnumFacing, coverStack: ItemStack): Unit = {
    if (te.getWorld.getTotalWorldTime % 20 != 0) return
    val inv = te.asInstanceOf[ISidedInventory with IInventory]

    for {
      slot <- inv.getSlotsForFace(side)
      stack <- Option(inv.getStackInSlot(slot))
      if inv.canExtractItem(slot, stack, side)
    } {
      val stackLeft = ItemPush.pushStack(te, side, stack.copy())
      if (stackLeft == null || stackLeft.stackSize < stack.stackSize) {
        inv.setInventorySlotContents(slot, stackLeft)
        inv.markDirty()
      }
    }
  }
}
