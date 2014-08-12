/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.gui.rscontrol

import net.bdew.lib.data.base.ContainerDataSlots
import net.minecraft.entity.player.EntityPlayer

trait ContainerRSControllable extends ContainerDataSlots {
  val dataSource: TileRSContollable

  /**
   * This implementation is piggybacking on vanilla Packet102WindowClick
   * This is the fake slot number, and shouldn't be used by anything else
   */
  final val RSMODE_SLOT_NUM = 1000

  override def slotClick(slotnum: Int, button: Int, modifiers: Int, player: EntityPlayer) =
    if (slotnum == RSMODE_SLOT_NUM) {
      dataSource.rsmode := RSMode(button)
      null
    } else {
      super.slotClick(slotnum, button, modifiers, player)
    }
}
