/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.machines.transposer

import net.bdew.lib.gui.{BaseContainer, SlotValidating}
import net.minecraft.entity.player.EntityPlayer
import net.bdew.lib.data.base.ContainerDataSlots

class ContainerTransposer(val te: TileTransposer, player: EntityPlayer) extends BaseContainer(te) with ContainerDataSlots {
  lazy val dataSource = te

  addSlotToContainer(new SlotValidating(te, te.slots.inTemplate, 74, 28))
  addSlotToContainer(new SlotValidating(te, te.slots.inLabware, 98, 28))
  addSlotToContainer(new SlotValidating(te, te.slots.inBlank, 41, 49))
  addSlotToContainer(new SlotValidating(te, te.slots.outCopy, 137, 49))
  bindPlayerInventory(player.inventory, 8, 84, 142)

  def canInteractWith(entityplayer: EntityPlayer): Boolean = {
    return te.isUseableByPlayer(entityplayer)
  }
}