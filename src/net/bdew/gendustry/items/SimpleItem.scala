/*
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.items

import net.minecraft.item.Item
import net.bdew.gendustry.Gendustry
import net.minecraft.client.renderer.texture.IconRegister
import cpw.mods.fml.relauncher.{Side, SideOnly}

class SimpleItem(id: Int, val name: String) extends Item(id) {
  setUnlocalizedName(Gendustry.modId + "." + name)

  @SideOnly(Side.CLIENT)
  override def registerIcons(reg: IconRegister) {
    itemIcon = reg.registerIcon(Gendustry.modId + ":" + name.toLowerCase)
  }
}
