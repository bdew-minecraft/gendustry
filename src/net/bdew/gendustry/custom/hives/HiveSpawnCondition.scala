/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.custom.hives

import net.minecraft.world.World

trait HiveSpawnCondition {
  def isValidLocation(world: World, x: Int, y: Int, z: Int): Boolean
  def getDescription: String
}

case class ConditionUnder(blocks: BlockFilter) extends HiveSpawnCondition {
  override def isValidLocation(world: World, x: Int, y: Int, z: Int): Boolean =
    y < world.getHeight && blocks.matches(world, x, y + 1, z)
  override def getDescription: String = "Under " + blocks.getDesctiption
}

case class ConditionAbove(blocks: BlockFilter) extends HiveSpawnCondition {
  override def isValidLocation(world: World, x: Int, y: Int, z: Int): Boolean =
    y >= 1 && blocks.matches(world, x, y - 1, z)
  override def getDescription: String = "Above " + blocks.getDesctiption
}

case class ConditionNextTo(blocks: BlockFilter) extends HiveSpawnCondition {
  override def isValidLocation(world: World, x: Int, y: Int, z: Int): Boolean =
    blocks.matches(world, x + 1, y, z) || blocks.matches(world, x - 1, y, z) || blocks.matches(world, x, y, z + 1) || blocks.matches(world, x, y, z - 1)
  override def getDescription: String = "Next To " + blocks.getDesctiption
}

case class ConditionReplace(blocks: BlockFilter) extends HiveSpawnCondition {
  override def isValidLocation(world: World, x: Int, y: Int, z: Int): Boolean =
    blocks.matches(world, x, y, z)
  override def getDescription: String = "Replacing " + blocks.getDesctiption
}