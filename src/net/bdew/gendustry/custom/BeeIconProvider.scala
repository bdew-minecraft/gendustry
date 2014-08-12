/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.custom

import cpw.mods.fml.relauncher.{Side, SideOnly}
import forestry.api.apiculture.EnumBeeType
import forestry.api.core.IIconProvider
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.util.IIcon

object BeeIconProvider extends IIconProvider {
  var icons: Array[Array[IIcon]] = null
  val iconType = "default"

  @SideOnly(Side.CLIENT)
  override def registerIcons(register: IIconRegister) {
    if (icons != null)
      return

    icons = Array.fill(EnumBeeType.values().length, 3)(null)

    EnumBeeType.values().filter(_ != EnumBeeType.NONE).foreach(beeType => {
      icons(beeType.ordinal())(0) = register.registerIcon("forestry:bees/%s/%s.outline".format(iconType, beeType).toLowerCase)
      if (beeType == EnumBeeType.LARVAE)
        icons(beeType.ordinal())(1) = register.registerIcon("forestry:bees/%s/%s.body".format(iconType, beeType).toLowerCase)
      else
        icons(beeType.ordinal())(1) = register.registerIcon("forestry:bees/%s/body1".format(iconType).toLowerCase)
      icons(beeType.ordinal())(2) = register.registerIcon("forestry:bees/%s/%s.body2".format(iconType, beeType).toLowerCase)
    })
  }

  override def getIcon(texUID: Short) = null
}
