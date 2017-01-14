/*
 * Copyright (c) bdew, 2013 - 2017
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
import net.bdew.gendustry.items.covers.{EjectCover, ErrorSensorCover, ImportCover}
import net.bdew.gendustry.machines.apiary.upgrades.ItemApiaryUpgrade
import net.bdew.gendustry.misc.GendustryCreativeTabs
import net.bdew.lib.config.ItemManager

object Items extends ItemManager(GendustryCreativeTabs.main) {
  val labware = regSimpleItem("labware")
  val waste = regSimpleItem("waste")
  val geneSampleBlank = regSimpleItem("gene_sample_blank")

  regItem(GeneSample).setCreativeTab(GendustryCreativeTabs.samples)
  regItem(GeneTemplate).setCreativeTab(GendustryCreativeTabs.templates)

  regItem(ItemApiaryUpgrade)

  regItem(IndustrialGrafter)
  regItem(IndustrialScoop)

  regItem(CustomHoneyComb)
  regItem(CustomHoneyDrop)

  val mutagenTank = regSimpleItem("mutagen_tank")
  val beeReceptacle = regSimpleItem("bee_receptacle")
  val powerModule = regSimpleItem("power_module")
  val geneticsProcessor = regSimpleItem("genetics_processor")
  val environmentProcessor = regSimpleItem("env_processor")
  val upgradeFrame = regSimpleItem("upgrade_frame")
  val climateModule = regSimpleItem("climate_module")

  if (ForestryHelper.haveRoot("Trees")) {
    regItem(PollenKit)
  }

  if (ForestryHelper.haveRoot("Bees")) {
    regItem(HiveSpawnDebugger)
  }

  val coverEject = regItem(EjectCover)
  val coverImport = regItem(ImportCover)
  regItem(ErrorSensorCover)

  Gendustry.logInfo("Items loaded")
}
