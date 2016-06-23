/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.custom.hives

import net.minecraft.block.Block
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.registry.GameData

trait BlockFilter {
  def matches(world: World, pos: BlockPos): Boolean
  def getDescription: String
}

case class BlockFilterList(valid: Set[(Block, Int)]) extends BlockFilter {
  override def matches(world: World, pos: BlockPos) =
    valid.exists { case (block, meta) =>
      val state = world.getBlockState(pos)
      block == state.getBlock && (meta == -1 || meta == state.getBlock.getMetaFromState(state))
    }
  override def getDescription: String = {
    val names = for ((block, meta) <- valid.toList) yield {
      GameData.getBlockRegistry.getNameForObject(block) + (if (meta > 0) "@%d".format(meta) else "@*")
    }
    names.mkString(" OR ")
  }
}

object BlockFilterAir extends BlockFilter {
  override def matches(world: World, pos: BlockPos): Boolean = world.isAirBlock(pos)
  override def getDescription: String = "[AIR]"
}

object BlockFilterReplaceable extends BlockFilter {
  override def matches(world: World, pos: BlockPos): Boolean = {
    val state = world.getBlockState(pos)
    state.getBlock.getMaterial(state).isReplaceable
  }
  override def getDescription: String = "[REPLACEABLE]"
}

object BlockFilterLeaves extends BlockFilter {
  override def matches(world: World, pos: BlockPos): Boolean = {
    val state = world.getBlockState(pos)
    state.getBlock.isLeaves(state, world, pos)
  }
  override def getDescription: String = "[LEAVES]"
}