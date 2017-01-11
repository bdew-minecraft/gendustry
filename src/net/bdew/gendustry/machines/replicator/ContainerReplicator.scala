/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.machines.replicator

import net.bdew.lib.data.base.ContainerDataSlots
import net.bdew.lib.gui.{BaseContainer, SlotValidating}
import net.minecraft.entity.player.EntityPlayer

class ContainerReplicator(val te: TileReplicator, player: EntityPlayer) extends BaseContainer(te) with ContainerDataSlots {
  lazy val dataSource = te

  addSlotToContainer(new SlotValidating(te, te.slots.inTemplate, 98, 17))
  addSlotToContainer(new SlotValidating(te, te.slots.outIndividual, 142, 41))
  bindPlayerInventory(player.inventory, 8, 84, 142)
}