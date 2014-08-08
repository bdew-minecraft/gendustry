/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.machines.extractor

import net.minecraft.entity.player.EntityPlayer
import net.bdew.lib.gui.{BaseContainer, SlotValidating}
import net.bdew.lib.data.base.ContainerDataSlots

class ContainerExtractor(val te: TileExtractor, player: EntityPlayer) extends BaseContainer(te) with ContainerDataSlots {
  lazy val dataSource = te

  addSlotToContainer(new SlotValidating(te, te.slots.inIndividual, 44, 41))
  addSlotToContainer(new SlotValidating(te, te.slots.inLabware, 94, 19))
  bindPlayerInventory(player.inventory, 8, 84, 142)

  def canInteractWith(entityplayer: EntityPlayer): Boolean = te.isUseableByPlayer(entityplayer)
}