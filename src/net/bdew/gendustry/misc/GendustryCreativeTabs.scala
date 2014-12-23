/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.misc

import net.bdew.gendustry.items.{GeneSample, GeneTemplate}
import net.bdew.gendustry.machines.apiary.BlockApiary
import net.bdew.lib.CreativeTabContainer
import net.minecraft.item.Item

object GendustryCreativeTabs extends CreativeTabContainer {
  val main = new Tab("bdew.gendustry", Item.getItemFromBlock(BlockApiary))
  val samples = new Tab("bdew.samples", GeneSample)
  val templates = new Tab("bdew.templates", GeneTemplate)
}
