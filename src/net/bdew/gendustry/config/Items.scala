/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.config

import net.bdew.gendustry.items.{IndustrialScoop, IndustrialGrafter, GeneTemplate, GeneSample}
import net.bdew.gendustry.machines.apiary.upgrades.ItemApiaryUpgrade
import net.bdew.lib.config.ItemManager
import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.custom.{CustomHoneyDrop, CustomHoneyComb}

object Items extends ItemManager {
  val labware = regSimpleItem("Labware")
  val waste = regSimpleItem("Waste")
  val geneSampleBlank = regSimpleItem("GeneSampleBlank")

  regItem(GeneSample)
  regItem(GeneTemplate)

  regItem(ItemApiaryUpgrade)

  regItem(IndustrialGrafter)
  regItem(IndustrialScoop)

  regItem(CustomHoneyComb)
  regItem(CustomHoneyDrop)

  regSimpleItem("MutagenTank")
  regSimpleItem("BeeReceptacle")
  regSimpleItem("PowerModule")
  regSimpleItem("GeneticsProcessor")
  regSimpleItem("EnvProcessor")
  regSimpleItem("UpgradeFrame")
  regSimpleItem("ClimateModule")

  Gendustry.logInfo("Items loaded")
}