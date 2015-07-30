/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.custom.hives

import cpw.mods.fml.common.registry.GameData
import net.minecraft.block.Block
import net.minecraft.world.World

trait BlockFilter {
  def matches(world: World, x: Int, y: Int, z: Int): Boolean
  def getDesctiption: String
}

case class BlockFilterList(valid: Set[(Block, Int)]) extends BlockFilter {
  override def matches(world: World, x: Int, y: Int, z: Int) =
    valid.exists { case (block, meta) => block == world.getBlock(x, y, z) && (meta == -1 || meta == world.getBlockMetadata(x, y, z)) }
  override def getDesctiption: String = {
    val names = for ((block, meta) <- valid.toList) yield {
      GameData.getBlockRegistry.getNameForObject(block) + (if (meta > 0) "@%d".format(meta) else "@*")
    }
    names.mkString(" OR ")
  }
}

object BlockFilterAir extends BlockFilter {
  override def matches(world: World, x: Int, y: Int, z: Int): Boolean = world.isAirBlock(x, y, z)
  override def getDesctiption: String = "[AIR]"
}

object BlockFilterReplaceable extends BlockFilter {
  override def matches(world: World, x: Int, y: Int, z: Int): Boolean = world.getBlock(x, y, z).getMaterial.isReplaceable
  override def getDesctiption: String = "[REPLACEABLE]"
}

object BlockFilterLeaves extends BlockFilter {
  override def matches(world: World, x: Int, y: Int, z: Int): Boolean = world.getBlock(x, y, z).isLeaves(world, x, y, z)
  override def getDesctiption: String = "[LEAVES]"
}