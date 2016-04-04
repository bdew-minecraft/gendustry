/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.fluids

import java.util.Locale

import net.bdew.gendustry.Gendustry
import net.minecraft.item.Item
import net.minecraftforge.fluids.Fluid

class ItemFluidCan(fluid: Fluid) extends Item {
  setUnlocalizedName(Gendustry.modId + "." + fluid.getName.toLowerCase(Locale.US) + ".can")
}