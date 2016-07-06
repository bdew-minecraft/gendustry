/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.items.covers

import net.bdew.lib.Misc
import net.bdew.lib.covers.{ItemCover, TileCoverable}
import net.bdew.lib.items.{BaseItem, ItemUtils}
import net.minecraft.inventory.{IInventory, ISidedInventory}
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing

object ImportCover extends BaseItem("ImportCover") with ItemCover {
  override def isCoverTicking: Boolean = true

  override def isValidTile(te: TileCoverable, stack: ItemStack) = te.isInstanceOf[ISidedInventory with IInventory]

  override def tickCover(te: TileCoverable, side: EnumFacing, coverStack: ItemStack): Unit = {
    if (te.getWorld.getTotalWorldTime % 20 != 0) return
    val inv = te.asInstanceOf[ISidedInventory with IInventory]
    val insertSlots = inv.getSlotsForFace(side)
    for {
      from <- Misc.getNeighbourTile(te, side, classOf[IInventory])
      slot <- ItemUtils.getAccessibleSlotsFromSide(from, side.getOpposite)
      stack <- Option(from.getStackInSlot(slot))
      if Misc.asInstanceOpt(from, classOf[ISidedInventory]).fold(true)(_.canExtractItem(slot, stack, side.getOpposite))
    } {
      from.setInventorySlotContents(slot, ItemUtils.addStackToSlots(stack, inv, insertSlots, true))
      from.markDirty()
    }
  }
}
