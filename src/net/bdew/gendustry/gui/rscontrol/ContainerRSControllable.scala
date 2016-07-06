/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.gui.rscontrol

import net.bdew.lib.data.base.ContainerDataSlots
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.ClickType
import net.minecraft.item.ItemStack

trait ContainerRSControllable extends ContainerDataSlots {
  val dataSource: TileRSControllable

  /**
   * This implementation is piggybacking on vanilla Packet102WindowClick
   * This is the fake slot number, and shouldn't be used by anything else
   */
  final val RSMODE_SLOT_NUM = 1000

  override def slotClick(slotNum: Int, button: Int, clickType: ClickType, player: EntityPlayer): ItemStack =
    if (slotNum == RSMODE_SLOT_NUM) {
      dataSource.rsmode := RSMode(button)
      null
    } else {
      super.slotClick(slotNum, button, clickType, player)
    }
}
