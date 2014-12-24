/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.items

import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.gendustry.Gendustry
import net.bdew.lib.Misc
import net.bdew.lib.covers.{ItemCover, TileCoverable}
import net.bdew.lib.items.{ItemUtils, SimpleItem}
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.inventory.{IInventory, ISidedInventory}
import net.minecraft.item.ItemStack
import net.minecraftforge.common.util.ForgeDirection

object ImportCover extends SimpleItem("ImportCover") with ItemCover {
  override def getCoverIcon(stack: ItemStack) = itemIcon
  override def getSpriteNumber = 0

  override def isValidTile(te: TileCoverable, stack: ItemStack) = te.isInstanceOf[ISidedInventory with IInventory]

  override def tickCover(te: TileCoverable, side: ForgeDirection, coverStack: ItemStack): Unit = {
    if (te.getWorldObj.getTotalWorldTime % 20 != 0) return
    val inv = te.asInstanceOf[ISidedInventory with IInventory]
    val insertSlots = inv.getAccessibleSlotsFromSide(side.ordinal())
    for {
      from <- Misc.getNeighbourTile(te, side, classOf[IInventory])
      slot <- ItemUtils.getAccessibleSlotsFromSide(from, side.getOpposite)
      stack <- Option(from.getStackInSlot(slot))
      if Misc.asInstanceOpt(from, classOf[ISidedInventory]).fold(true)(_.canExtractItem(slot, stack, side.getOpposite.ordinal()))
    } {
      from.setInventorySlotContents(slot, ItemUtils.addStackToSlots(stack, inv, insertSlots, true))
      from.markDirty()
    }
  }

  @SideOnly(Side.CLIENT)
  override def registerIcons(reg: IIconRegister) {
    itemIcon = reg.registerIcon(Gendustry.modId + ":covers/import")
  }
}
