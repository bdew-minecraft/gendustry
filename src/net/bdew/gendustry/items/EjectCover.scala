/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.items

import net.bdew.lib.items.{ItemUtils, SimpleItem}
import net.bdew.lib.covers.{TileCoverable, ItemCover}
import net.minecraftforge.common.ForgeDirection
import net.minecraft.inventory.{IInventory, ISidedInventory}
import net.minecraft.client.renderer.texture.IconRegister
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.gendustry.Gendustry
import net.bdew.lib.Misc

class EjectCover(id: Int) extends SimpleItem(id, "EjectCover") with ItemCover {
  override def getCoverIcon = itemIcon
  override def getSpriteNumber = 0

  override def isValidTile(te: TileCoverable) = te.isInstanceOf[ISidedInventory with IInventory]

  override def tickCover(te: TileCoverable, side: ForgeDirection): Unit = {
    if (te.worldObj.getTotalWorldTime % 20 != 0) return
    val inv = te.asInstanceOf[ISidedInventory with IInventory]
    for {
      target <- Misc.getNeighbourTile(te, side, classOf[IInventory])
      slot <- inv.getAccessibleSlotsFromSide(side.ordinal())
      stack <- Option(inv.getStackInSlot(slot))
      if inv.canExtractItem(slot, stack, side.ordinal())
    } {
      val slots = ItemUtils.getAccessibleSlotsFromSide(target, side.getOpposite)
      inv.setInventorySlotContents(slot, ItemUtils.addStackToSlots(stack, target, slots, true))
      inv.onInventoryChanged()
    }
  }

  @SideOnly(Side.CLIENT)
  override def registerIcons(reg: IconRegister) {
    itemIcon = reg.registerIcon(Gendustry.modId + ":covers/eject")
  }
}
