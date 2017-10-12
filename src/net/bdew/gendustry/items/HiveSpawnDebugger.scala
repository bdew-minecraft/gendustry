/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.items

import forestry.api.core.{EnumHumidity, EnumTemperature}
import net.bdew.gendustry.custom.CustomHives
import net.bdew.gendustry.custom.hives.{BlockFilterAir, ConditionReplace, HiveDescription}
import net.bdew.lib.items.BaseItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{EnumActionResult, EnumFacing, EnumHand}
import net.minecraft.world.World

object HiveSpawnDebugger extends BaseItem("hive_spawn_debugger") {

  class CheckResult(val isSuccess: Boolean)

  case class CheckResultSuccess() extends CheckResult(true)

  case class CheckResultFailed(msg: String) extends CheckResult(false)

  def checkSpawnLocation(hive: HiveDescription, world: World, pos: BlockPos): CheckResult = {
    val biome = world.getBiome(pos)
    if (!hive.isGoodBiome(biome)) CheckResultFailed("Wrong Biome")
    else if (!hive.isGoodHumidity(EnumHumidity.getFromValue(biome.getRainfall))) CheckResultFailed("Wrong Humidity")
    else if (!hive.isGoodTemperature(EnumTemperature.getFromValue(biome.getTemperature(pos)))) CheckResultFailed("Wrong Temperature")
    else if (pos.getY > hive.yMax || pos.getY < hive.yMin) CheckResultFailed("Incorrect Y level")
    else {
      val failed = hive.conditions.find(!_.isValidLocation(world, pos))
      if (failed.isDefined)
        CheckResultFailed("Condition failed: " + failed.get.getDescription)
      else
        CheckResultSuccess()
    }
  }

  override def onItemUse(player: EntityPlayer, world: World, pos: BlockPos, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult = {
    if (!world.isRemote) {
      import net.bdew.lib.helpers.ChatHelper._
      player.sendMessage(" ==== Checking Spawn At (%s) ===".format(pos))
      for ((id, hive) <- CustomHives.hives) {
        val actualPos = if (hive.conditions.contains(ConditionReplace(BlockFilterAir))) {
          // Hive spawns in air, check block next to one clicked
          pos.offset(facing)
        } else {
          // Hive spawns replaces block, check clicked block
          pos
        }
        val msg =
          checkSpawnLocation(hive, world, actualPos) match {
            case CheckResultFailed(error) =>
              error.setColor(Color.RED)
            case CheckResultSuccess() =>
              ("OK at " + actualPos).setColor(Color.GREEN)
          }

        player.sendMessage(L(" * %s - %s", C(id).setColor(Color.YELLOW), msg))
      }
    }
    EnumActionResult.SUCCESS
  }
}
