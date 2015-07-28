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

import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.gendustry.custom.hives.HiveDescription
import net.bdew.lib.block.SimpleBlock
import net.minecraft.block.material.{MapColor, Material}
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.item.ItemStack
import net.minecraft.util.IIcon
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.common.util.ForgeDirection

import scala.util.Random

object MaterialBeehive extends Material(MapColor.stoneColor) {
  setRequiresTool()
  setImmovableMobility()

}

case class BeeHive(hiveId: String, sideIconName: String, topIconName: String, bottomIconName: String, color: Int, lightLevel: Int, hive: HiveDescription)
  extends SimpleBlock("BeeHive" + hiveId, MaterialBeehive) {

  setHardness(1.0f)
  setHarvestLevel("scoop", 0)
  lightValue = lightLevel

  override def getDrops(world: World, x: Int, y: Int, z: Int, metadata: Int, fortune: Int): util.ArrayList[ItemStack] = {
    val ret = new util.ArrayList[ItemStack]()
    val rng = new Random(world.rand)

    if (hive.drops.isEmpty) return ret

    val dropList = rng.shuffle(hive.drops)

    // Select random princess drop
    dropList.find(drop => rng.nextInt(100) <= drop.getChance(world, x, y, z)) orElse {
      // if none are selected by RNG - ensure a princess is always dropped by selecting the one with highest chance
      dropList.sortBy(-_.getChance(world, x, y, z)).headOption
    } map { drop =>
      ret.add(drop.getPrincess(world, x, y, z, fortune))
    }

    // Add random drone
    dropList.find(drop => rng.nextInt(100) <= drop.getChance(world, x, y, z)) map { drop =>
      ret.addAll(drop.getDrones(world, x, y, z, fortune))
    }

    // And additional drops
    dropList.find(drop => rng.nextInt(100) <= drop.getChance(world, x, y, z)) map { drop =>
      ret.addAll(drop.getAdditional(world, x, y, z, fortune))
    }

    ret
  }

  var topIcon: IIcon = null
  var bottomIcon: IIcon = null

  override def getIcon(side: Int, meta: Int): IIcon =
    if (side == ForgeDirection.UP.ordinal())
      topIcon
    else if (side == ForgeDirection.DOWN.ordinal())
      bottomIcon
    else
      blockIcon

  @SideOnly(Side.CLIENT)
  override def colorMultiplier(w: IBlockAccess, x: Int, y: Int, z: Int): Int = color

  @SideOnly(Side.CLIENT)
  override def getRenderColor(meta: Int): Int = color

  @SideOnly(Side.CLIENT)
  override def registerBlockIcons(reg: IIconRegister): Unit = {
    topIcon = reg.registerIcon(topIconName)
    bottomIcon = reg.registerIcon(bottomIconName)
    blockIcon = reg.registerIcon(sideIconName)
  }

}
