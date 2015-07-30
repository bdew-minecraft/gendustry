/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.items

import cpw.mods.fml.relauncher.{Side, SideOnly}
import forestry.api.core.{EnumHumidity, EnumTemperature}
import net.bdew.gendustry.custom.CustomHives
import net.bdew.gendustry.custom.hives.{BlockFilterAir, ConditionReplace, HiveDescription}
import net.bdew.lib.Misc
import net.bdew.lib.block.BlockRef
import net.bdew.lib.items.SimpleItem
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.world.World

object HiveSpawnDebugger extends SimpleItem("HiveSpawnDebugger") {

  class CheckResult(val isSuccess: Boolean)

  case class CheckResultSuccess() extends CheckResult(true)

  case class CheckResultFailed(msg: String) extends CheckResult(false)

  def checkSpawnLocation(hive: HiveDescription, world: World, x: Int, y: Int, z: Int): CheckResult = {
    val biome = world.getBiomeGenForCoords(x, z)
    if (!hive.isGoodBiome(biome)) CheckResultFailed("Wrong Biome")
    else if (!hive.isGoodHumidity(EnumHumidity.getFromValue(biome.rainfall))) CheckResultFailed("Wrong Humidity")
    else if (!hive.isGoodTemperature(EnumTemperature.getFromValue(biome.temperature))) CheckResultFailed("Wrong Temperature")
    else if (y > hive.yMax || y < hive.yMin) CheckResultFailed("Incorrect Y level")
    else {
      val failed = hive.conditions.find(!_.isValidLocation(world, x, y, z))
      if (failed.isDefined)
        CheckResultFailed("Condition failed: " + failed.get.getDescription)
      else
        CheckResultSuccess()
    }

  }

  override def onItemUse(stack: ItemStack, player: EntityPlayer, world: World, x: Int, y: Int, z: Int, side: Int, xOff: Float, yOff: Float, zOff: Float): Boolean = {
    if (!world.isRemote) {
      import net.bdew.lib.helpers.ChatHelper._
      player.addChatMessage(" ==== Checking Spawn At (%d,%d,%d) ===".format(x, y, z))
      for ((id, hive) <- CustomHives.hives) {
        val pos = if (hive.conditions.contains(ConditionReplace(BlockFilterAir))) {
          // Hive spawns in air, check block next to one clicked
          BlockRef(x, y, z).neighbour(Misc.forgeDirection(side))
        } else {
          // Hive spawns replaces block, check clicked block
          BlockRef(x, y, z)
        }
        val msg =
          checkSpawnLocation(hive, world, pos.x, pos.y, pos.z) match {
            case CheckResultFailed(error) =>
              error.setColor(Color.RED)
            case CheckResultSuccess() =>
              ("OK at " + pos).setColor(Color.GREEN)
          }

        player.addChatMessage(L(" * %s - %s", C(id).setColor(Color.YELLOW), msg))
      }
    }
    true
  }

  @SideOnly(Side.CLIENT) override
  def registerIcons(reg: IIconRegister): Unit = {}

  @SideOnly(Side.CLIENT)
  override def getIconFromDamage(par1: Int) = Items.stick.getIconFromDamage(0)
}
