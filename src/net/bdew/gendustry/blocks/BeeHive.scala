/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.blocks

import java.util

import forestry.api.apiculture.{BeeManager, EnumBeeType, IHiveDrop}
import net.bdew.gendustry.custom.hives.HiveDescription
import net.bdew.lib.block.BaseBlock
import net.minecraft.block.material.{MapColor, Material}
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

import scala.util.Random

object MaterialBeehive extends Material(MapColor.STONE) {
  setRequiresTool()
  setImmovableMobility()

}

case class BeeHive(hiveId: String, modelLocation: ResourceLocation, color: Int, lightLevel: Int, hive: HiveDescription)
  extends BaseBlock("BeeHive" + hiveId, MaterialBeehive) {

  setHardness(1.0f)
  setHarvestLevel("scoop", 0)
  lightValue = lightLevel

  private def makeBee(drop: IHiveDrop, kind: EnumBeeType, ignoble: Boolean, world: IBlockAccess, pos: BlockPos) = {
    val bee = drop.getBeeType(world, pos)
    if (ignoble) bee.setIsNatural(false)
    BeeManager.beeRoot.getMemberStack(bee, kind)
  }

  override def getDrops(world: IBlockAccess, pos: BlockPos, state: IBlockState, fortune: Int): util.ArrayList[ItemStack] = {
    val ret = new util.ArrayList[ItemStack]()

    if (hive.drops.isEmpty) return ret

    val dropList = Random.shuffle(hive.drops)

    // Select random princess drop
    dropList.find(drop => Random.nextDouble <= drop.getChance(world, pos, fortune)) orElse {
      // if none are selected by the rng - ensure a princess is always dropped by selecting the one with highest chance
      dropList.sortBy(-_.getChance(world, pos, fortune)).headOption
    } map { drop =>
      ret.add(makeBee(drop, EnumBeeType.PRINCESS, Random.nextDouble < drop.getIgnobleChance(world, pos, fortune), world, pos))
    }

    // Add random drone
    dropList.filter(drop => Random.nextDouble < drop.getChance(world, pos, fortune)) map { drop =>
      ret.add(makeBee(drop, EnumBeeType.DRONE, false, world, pos))
    }

    // And additional drops
    dropList.filter(drop => Random.nextDouble <= drop.getChance(world, pos, fortune)) map { drop =>
      ret.addAll(drop.getExtraItems(world, pos, fortune))
    }

    ret
  }


}
