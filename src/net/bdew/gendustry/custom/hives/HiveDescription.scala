/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.custom.hives

import java.util.Random

import forestry.api.apiculture.IHiveDrop
import forestry.api.apiculture.hives.{IHiveDescription, IHiveGen}
import forestry.api.core.{EnumHumidity, EnumTemperature}
import net.bdew.gendustry.Gendustry
import net.bdew.lib.PimpVanilla._
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.biome.Biome

case class HiveDescription(id: String, chance: Float, yMin: Int, yMax: Int,
                           validBiome: Set[Biome],
                           validTemperature: Set[EnumTemperature],
                           validHumidity: Set[EnumHumidity],
                           conditions: List[HiveSpawnCondition],
                           drops: List[IHiveDrop],
                           spawnDebug: Boolean
                          ) extends IHiveDescription {

  override def getBlockState: IBlockState =
    Block.REGISTRY.getObject(new ResourceLocation(Gendustry.modId, "BeeHive" + id)).getDefaultState

  override val getHiveGen = new IHiveGen {
    override def isValidLocation(world: World, pos: BlockPos): Boolean =
      !conditions.exists(!_.isValidLocation(world, pos))
    override def canReplace(blockState: IBlockState, world: World, pos: BlockPos): Boolean = true
    override def getPosForHive(world: World, x: Int, z: Int): BlockPos =
      ((new BlockPos(x, yMin, z) to new BlockPos(x, yMax, z)) find (p => isValidLocation(world, p) && canReplace(world.getBlockState(p), world, p))).orNull
  }

  override val getGenChance = chance

  override def isGoodBiome(biome: Biome): Boolean = validBiome.contains(biome)
  override def isGoodHumidity(humidity: EnumHumidity): Boolean = validHumidity.contains(humidity)
  override def isGoodTemperature(temperature: EnumTemperature): Boolean = validTemperature.contains(temperature)

  override def postGen(world: World, rand: Random, pos: BlockPos): Unit = {
    if (spawnDebug) Gendustry.logInfo("Spawning hive %s at %d:%s", id, world.provider.getDimension, pos)
  }
}