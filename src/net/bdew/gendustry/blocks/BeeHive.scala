/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.blocks

import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.gendustry.custom.hives.HiveDescription
import net.bdew.lib.block.SimpleBlock
import net.minecraft.block.material.{MapColor, Material}
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.util.IIcon
import net.minecraft.world.IBlockAccess
import net.minecraftforge.common.util.ForgeDirection

object MaterialBeehive extends Material(MapColor.stoneColor) {
  setRequiresTool()
  setImmovableMobility()

}

case class BeeHive(hiveId: String, sideIconName: String, topIconName: String, bottomIconName: String, color: Int, lightLevel: Int)
  extends SimpleBlock("BeeHive" + hiveId, MaterialBeehive) {
  var hive: Option[HiveDescription] = None

  setHardness(1.0f)
  setHarvestLevel("scoop", 0)
  lightValue = lightLevel

  var topIcon: IIcon = null
  var bottomIcon: IIcon = null

  override def getIcon(side: Int, meta: Int): IIcon =
    if (side == ForgeDirection.UP.ordinal())
      topIcon
    else if (side == ForgeDirection.DOWN.ordinal())
      bottomIcon
    else
      blockIcon

  @SideOnly(Side.CLIENT)
  override def colorMultiplier(w: IBlockAccess, x: Int, y: Int, z: Int): Int = color

  @SideOnly(Side.CLIENT)
  override def getRenderColor(meta: Int): Int = color

  @SideOnly(Side.CLIENT)
  override def registerBlockIcons(reg: IIconRegister): Unit = {
    topIcon = reg.registerIcon(topIconName)
    bottomIcon = reg.registerIcon(bottomIconName)
    blockIcon = reg.registerIcon(sideIconName)
  }

}
