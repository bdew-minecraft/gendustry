/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.items

import forestry.api.arboriculture.EnumGermlingType
import forestry.api.genetics.IPollinatable
import net.bdew.gendustry.compat.ForestryHelper
import net.bdew.gendustry.forestry.GeneticsHelper
import net.bdew.lib.block.BlockRef
import net.bdew.lib.items.{ItemUtils, SimpleItem}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.World

object PollenKit extends SimpleItem("PollenKit") {
  override def onItemUse(stack: ItemStack, player: EntityPlayer, world: World, x: Int, y: Int, z: Int, side: Int, xOff: Float, yOff: Float, zOff: Float): Boolean = {
    if (player.isSneaking) return false
    if (!world.isRemote) {
      if (player.inventory.getCurrentItem.getItem != this) return false
      val blockRef = BlockRef(x, y, z)
      (blockRef.getTile[IPollinatable](world) map { te => te.getPollen }
        // If block is not IPollinatable, check for vanilla leafs conversion
        orElse (blockRef.block(world) flatMap { bl => GeneticsHelper.getErsatzPollen(bl, blockRef.meta(world)) })
        ) map { individual =>
        // Generate pollen item and consume kit
        val newStack = ForestryHelper.getRoot("Trees").getMemberStack(individual, EnumGermlingType.POLLEN.ordinal())
        if (newStack != null) {
          ItemUtils.dropItemToPlayer(world, player, newStack)
          player.inventory.decrStackSize(player.inventory.currentItem, 1)
        }
      }
    }
    true
  }

}
