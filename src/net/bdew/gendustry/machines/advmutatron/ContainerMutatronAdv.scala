/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.machines.advmutatron

import net.bdew.lib.data.base.ContainerDataSlots
import net.bdew.lib.gui.{BaseContainer, SlotValidating}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.ClickType
import net.minecraft.item.ItemStack

class ContainerMutatronAdv(val te: TileMutatronAdv, player: EntityPlayer) extends BaseContainer(te) with ContainerDataSlots {
  lazy val dataSource = te

  addSlotToContainer(new SlotValidating(te, 0, 60, 30))
  addSlotToContainer(new SlotValidating(te, 1, 60, 53))
  addSlotToContainer(new SlotValidating(te, 2, 142, 41))
  addSlotToContainer(new SlotValidating(te, 3, 98, 17))

  for (i <- te.slots.selectors)
    addSlotToContainer(new SlotSelector(te, i, i * 18 - 10, 85))

  bindPlayerInventory(player.inventory, 8, 106, 164)

  te.lastPlayer := player.getGameProfile

  override def slotClick(slotNum: Int, button: Int, clickType: ClickType, player: EntityPlayer): ItemStack = {
    te.lastPlayer := player.getGameProfile
    // This is a hacky workaround!
    // When a player changes the contents of a slot, playerInventoryBeingManipulated is set to true,
    // preventing updates to OTHER slots from being detected and sent back
    // Here i ensure changes are sent back before returning so NetServerHandler.handleWindowClick doesn't
    // get the opportunity to mess things up
    val r = super.slotClick(slotNum, button, clickType, player)
    detectAndSendChanges()
    return r
  }
}