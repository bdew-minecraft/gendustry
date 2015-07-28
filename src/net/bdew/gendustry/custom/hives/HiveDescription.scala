/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.custom.hives

import cpw.mods.fml.common.registry.GameRegistry
import forestry.api.apiculture.IHiveDrop
import forestry.api.apiculture.hives.{IHiveDescription, IHiveGen}
import forestry.api.core.{EnumHumidity, EnumTemperature}
import net.bdew.gendustry.Gendustry
import net.minecraft.world.World
import net.minecraft.world.biome.BiomeGenBase

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
    override def isValidLocation(world: World, x: Int, y: Int, z: Int): Boolean =
      !conditions.exists(!_.isValidLocation(world, x, y, z))
    override def canReplace(world: World, x: Int, y: Int, z: Int): Boolean = true
    override def getYForHive(world: World, x: Int, z: Int): Int =
      (yMin to yMax) find (y => isValidLocation(world, x, y, z) && canReplace(world, x, y, z)) getOrElse -1
  }

  override val getGenChance = chance

  override def isGoodBiome(biome: BiomeGenBase): Boolean = validBiome.contains(biome)
  override def isGoodHumidity(humidity: EnumHumidity): Boolean = validHumidity.contains(humidity)
  override def isGoodTemperature(temperature: EnumTemperature): Boolean = validTemperature.contains(temperature)

  override def postGen(world: World, x: Int, y: Int, z: Int): Unit = {
    if (spawnDebug) Gendustry.logInfo("Spawning hive %s at %d:%d,%d,%d", id, world.provider.dimensionId, x, y, z)
  }
}