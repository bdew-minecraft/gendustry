/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.machines

import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.gendustry.Gendustry
import net.bdew.lib.Misc
import net.minecraft.block.Block
import net.minecraft.block.material.{MapColor, Material}
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.util.IIcon

object MachineMaterial extends Material(MapColor.ironColor)

class BaseMachineBlock(name: String) extends Block(MachineMaterial) {
  setBlockName(Gendustry.modId + "." + name)
  setHardness(2)

  var topIcon: IIcon = null
  var bottomIcon: IIcon = null

  override def getIcon(side: Int, meta: Int): IIcon = {
    side match {
      case 0 =>
        return bottomIcon
      case 1 =>
        return topIcon
      case _ =>
        return blockIcon
    }
  }

  @SideOnly(Side.CLIENT)
  override def registerBlockIcons(reg: IIconRegister) {
    bottomIcon = reg.registerIcon(Misc.iconName(Gendustry.modId, name, "bottom"))
    topIcon = reg.registerIcon(Misc.iconName(Gendustry.modId, name, "top"))
    blockIcon = reg.registerIcon(Misc.iconName(Gendustry.modId, name, "side"))
  }
}
