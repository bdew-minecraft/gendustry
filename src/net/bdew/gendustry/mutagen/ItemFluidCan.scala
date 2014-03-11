/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.mutagen

import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import net.bdew.gendustry.Gendustry
import net.minecraft.client.renderer.texture.IconRegister
import net.minecraft.item.Item
import net.minecraftforge.fluids.Fluid

class ItemFluidCan(id: Int, fluid: Fluid) extends Item(id) {
  setUnlocalizedName(Gendustry.modId + "." + fluid.getName.toLowerCase + ".can")

  @SideOnly(Side.CLIENT)
  override def registerIcons(reg: IconRegister) {
    itemIcon = reg.registerIcon(Gendustry.modId + ":can/" + fluid.getName.toLowerCase)
  }
}