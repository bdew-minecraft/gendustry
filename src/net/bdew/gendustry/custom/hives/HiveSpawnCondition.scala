/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.custom.hives

import net.minecraft.util.{BlockPos, EnumFacing}
import net.minecraft.world.World

trait HiveSpawnCondition {
  def isValidLocation(world: World, pos: BlockPos): Boolean
  def getDescription: String
}

class ConditionNeighbour(blocks: BlockFilter, name: String, offsets: List[EnumFacing]) extends HiveSpawnCondition {
  override def isValidLocation(world: World, pos: BlockPos): Boolean =
    offsets.exists(f => blocks.matches(world, pos.offset(f)))
  override def getDescription: String = name + " " + blocks.getDesctiption
}

case class ConditionUnder(blocks: BlockFilter) extends ConditionNeighbour(blocks, "Below", List(EnumFacing.UP))

case class ConditionAbove(blocks: BlockFilter) extends ConditionNeighbour(blocks, "Above", List(EnumFacing.DOWN))

case class ConditionNextTo(blocks: BlockFilter) extends ConditionNeighbour(blocks, "Next To", EnumFacing.HORIZONTALS.toList)

case class ConditionNear(blocks: BlockFilter) extends ConditionNeighbour(blocks, "Near", EnumFacing.values().toList)

case class ConditionReplace(blocks: BlockFilter) extends HiveSpawnCondition {
  override def isValidLocation(world: World, pos: BlockPos): Boolean =
    blocks.matches(world, pos)
  override def getDescription: String = "Replacing " + blocks.getDesctiption
}