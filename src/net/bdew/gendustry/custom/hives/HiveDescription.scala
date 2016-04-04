/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.custom.hives

import forestry.api.apiculture.IHiveDrop
import forestry.api.apiculture.hives.{IHiveDescription, IHiveGen}
import forestry.api.core.{EnumHumidity, EnumTemperature}
import net.bdew.gendustry.Gendustry
import net.bdew.lib.PimpVanilla._
import net.minecraft.util.BlockPos
import net.minecraft.world.World
import net.minecraft.world.biome.BiomeGenBase
import net.minecraftforge.fml.common.registry.GameRegistry

case class HiveDescription(id: String, chance: Float, yMin: Int, yMax: Int,
                           validBiome: Set[BiomeGenBase],
                           validTemperature: Set[EnumTemperature],
                           validHumidity: Set[EnumHumidity],
                           conditions: List[HiveSpawnCondition],
                           drops: List[IHiveDrop],
                           spawnDebug: Boolean
                          ) extends IHiveDescription {
  override lazy val getBlock = GameRegistry.findBlock(Gendustry.modId, "BeeHive" + id)
  override val getMeta = 0

  override val getHiveGen = new IHiveGen {
    override def isValidLocation(world: World, pos: BlockPos): Boolean =
      !conditions.exists(!_.isValidLocation(world, pos))
    override def canReplace(world: World, pos: BlockPos): Boolean = true
    override def getYForHive(world: World, x: Int, z: Int): Int =
      (new BlockPos(x, yMin, z) to new BlockPos(x, yMax, z)) find (p => isValidLocation(world, p) && canReplace(world, p)) map (_.getY) getOrElse -1
  }

  override val getGenChance = chance

  override def isGoodBiome(biome: BiomeGenBase): Boolean = validBiome.contains(biome)
  override def isGoodHumidity(humidity: EnumHumidity): Boolean = validHumidity.contains(humidity)
  override def isGoodTemperature(temperature: EnumTemperature): Boolean = validTemperature.contains(temperature)

  override def postGen(world: World, pos: BlockPos): Unit = {
    if (spawnDebug) Gendustry.logInfo("Spawning hive %s at %d:%d,%d,%d", id, world.provider.getDimensionId, pos)
  }
}