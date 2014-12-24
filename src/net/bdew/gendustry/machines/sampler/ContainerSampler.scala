/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.machines.sampler

import net.bdew.lib.data.base.ContainerDataSlots
import net.bdew.lib.gui.{BaseContainer, SlotValidating}
import net.minecraft.entity.player.EntityPlayer

class ContainerSampler(val te: TileSampler, player: EntityPlayer) extends BaseContainer(te) with ContainerDataSlots {
  lazy val dataSource = te

  addSlotToContainer(new SlotValidating(te, 0, 74, 28))
  addSlotToContainer(new SlotValidating(te, 1, 98, 28))
  addSlotToContainer(new SlotValidating(te, 2, 41, 49))
  addSlotToContainer(new SlotValidating(te, 3, 137, 49))
  bindPlayerInventory(player.inventory, 8, 84, 142)
}