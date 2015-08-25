/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.config

import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.compat.ForestryHelper
import net.bdew.gendustry.custom.{CustomHoneyComb, CustomHoneyDrop}
import net.bdew.gendustry.items._
import net.bdew.gendustry.items.covers.{EjectCover, ImportCover}
import net.bdew.gendustry.machines.apiary.upgrades.ItemApiaryUpgrade
import net.bdew.gendustry.misc.GendustryCreativeTabs
import net.bdew.lib.config.ItemManager

object Items extends ItemManager(GendustryCreativeTabs.main) {
  val labware = regSimpleItem("Labware")
  val waste = regSimpleItem("Waste")
  val geneSampleBlank = regSimpleItem("GeneSampleBlank")

  regItem(GeneSample).setCreativeTab(GendustryCreativeTabs.samples)
  regItem(GeneTemplate).setCreativeTab(GendustryCreativeTabs.templates)

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

  if (ForestryHelper.haveRoot("Trees")) {
    regItem(PollenKit)
  }

  if (ForestryHelper.haveRoot("Bees")) {
    regItem(HiveSpawnDebugger)
  }

  val coverEject = regItem(EjectCover)
  val coverImport = regItem(ImportCover)

  Gendustry.logInfo("Items loaded")
}