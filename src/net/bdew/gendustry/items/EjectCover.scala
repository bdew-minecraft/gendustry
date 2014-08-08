/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.items

import net.bdew.lib.items.SimpleItem
import net.bdew.lib.covers.{TileCoverable, ItemCover}
import net.minecraft.inventory.{IInventory, ISidedInventory}
import net.minecraft.client.renderer.texture.IIconRegister
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.compat.itempush.ItemPush
import net.minecraft.item.ItemStack
import net.minecraftforge.common.util.ForgeDirection

object EjectCover extends SimpleItem("EjectCover") with ItemCover {
  override def getCoverIcon(stack: ItemStack) = itemIcon
  override def getSpriteNumber = 0

  override def isValidTile(te: TileCoverable, stack: ItemStack) = te.isInstanceOf[ISidedInventory with IInventory]

  override def tickCover(te: TileCoverable, side: ForgeDirection, coverStack: ItemStack): Unit = {
    if (te.getWorldObj.getTotalWorldTime % 20 != 0) return
    val inv = te.asInstanceOf[ISidedInventory with IInventory]

    for {
      slot <- inv.getAccessibleSlotsFromSide(side.ordinal())
      stack <- Option(inv.getStackInSlot(slot))
      if inv.canExtractItem(slot, stack, side.ordinal())
    } {
      val stackLeft = ItemPush.pushStack(te, side, stack.copy())
      if (stackLeft == null || stackLeft.stackSize < stack.stackSize) {
        inv.setInventorySlotContents(slot, stackLeft)
        inv.markDirty()
      }
    }
  }

  @SideOnly(Side.CLIENT)
  override def registerIcons(reg: IIconRegister) {
    itemIcon = reg.registerIcon(Gendustry.modId + ":covers/eject")
  }
}
