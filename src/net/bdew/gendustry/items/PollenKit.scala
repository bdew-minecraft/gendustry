/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.items

import forestry.api.arboriculture.{EnumGermlingType, ITreeRoot}
import forestry.api.genetics.IPollinatable
import net.bdew.gendustry.forestry.GeneticsHelper
import net.bdew.lib.PimpVanilla._
import net.bdew.lib.items.{BaseItem, ItemUtils}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.{BlockPos, EnumFacing}
import net.minecraft.world.World

object PollenKit extends BaseItem("PollenKit") {

  override def onItemUse(stack: ItemStack, player: EntityPlayer, world: World, pos: BlockPos, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean = {
    if (player.isSneaking) return false
    if (!world.isRemote) {
      if (player.inventory.getCurrentItem.getItem != this) return false
      (world.getTileSafe[IPollinatable](pos) map { te => te.getPollen }
        // If block is not IPollinatable, check for vanilla leafs conversion
        orElse GeneticsHelper.getErsatzPollen(world.getBlockState(pos))
        ) map { individual =>
        (individual.getGenome.getSpeciesRoot match {
          case trees: ITreeRoot => Option(trees.getMemberStack(individual, EnumGermlingType.POLLEN))
          case _ => None
        }) map { newStack =>
          // Generate pollen item and consume kit
          ItemUtils.dropItemToPlayer(world, player, newStack)
          player.inventory.decrStackSize(player.inventory.currentItem, 1)
        }
      }
    }
    true
  }

}
