/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.config.loader

import net.bdew.lib.recipes.{Condition, ConfigStatement, StackRef}

case class CSHiveDefinition(id: String, definition: List[HiveDefStatement]) extends ConfigStatement

trait HiveDefStatement

case class HDSpawnIf(condition: Condition) extends HiveDefStatement

case class HDSpawnChance(chance: Float) extends HiveDefStatement

case class HDYRange(min: Int, max: Int) extends HiveDefStatement

case class HDBiomes(biomes: List[String]) extends HiveDefStatement

case class HDTemperature(temperatures: List[String]) extends HiveDefStatement

case class HDHumidity(humidityLevels: List[String]) extends HiveDefStatement

case class HDSideTexture(loc: String) extends HiveDefStatement

case class HDTopTexture(loc: String) extends HiveDefStatement

case class HDBottomTexture(loc: String) extends HiveDefStatement

case class HDColor(color: Int) extends HiveDefStatement

case class HDLight(level: Int) extends HiveDefStatement

case class HDSpawnDebug(debug: Boolean) extends HiveDefStatement

case class HDDrops(drops: List[HiveDropEntry]) extends HiveDefStatement

trait HiveDefCondition extends HiveDefStatement

case class HDLocationUnder(blocks: BlockFilterDef) extends HiveDefCondition

case class HDLocationAbove(blocks: BlockFilterDef) extends HiveDefCondition

case class HDLocationNextTo(blocks: BlockFilterDef) extends HiveDefCondition

case class HDReplace(blocks: BlockFilterDef) extends HiveDefCondition

trait BlockFilterDef

object BlockFilterDefAir extends BlockFilterDef

object BlockFilterDefLeaves extends BlockFilterDef

object BlockFilterDefReplaceable extends BlockFilterDef

case class BlockFilterRef(blocks: List[StackRef]) extends BlockFilterDef

case class HiveDropEntry(chance: Int, uid: String, ignobleShare: Float, additional: List[StackRef])