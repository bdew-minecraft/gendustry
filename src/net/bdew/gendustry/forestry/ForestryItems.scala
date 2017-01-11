/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.forestry

import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation

object ForestryItems {
  lazy val honeydew = Item.REGISTRY.getObject(new ResourceLocation("forestry", "honeydew"))
  lazy val canEmpty = Item.REGISTRY.getObject(new ResourceLocation("forestry", "can"))
}

