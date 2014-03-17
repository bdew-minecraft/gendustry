/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.config

import cpw.mods.fml.common.registry.GameRegistry
import net.bdew.gendustry.items.{IndustrialGrafter, GeneTemplate, GeneSample}
import net.bdew.gendustry.machines.apiary.upgrades.ItemApiaryUpgrade
import net.bdew.lib.config.ItemManager
import net.bdew.gendustry.Gendustry

object Items extends ItemManager(Config.IDs) {
  val labware = regSimpleItem("Labware")
  val waste = regSimpleItem("Waste")
  val geneSampleBlank = regSimpleItem("GeneSampleBlank")

  val geneSample = regItem(new GeneSample(ids.getItemId("GeneSample")))
  val geneTemplate = regItem(new GeneTemplate(ids.getItemId("GeneTemplate")))

  val upgradeItem = regItemCls(classOf[ItemApiaryUpgrade], "ApiaryUpgrade")

  val grafter = regItemCls(classOf[IndustrialGrafter], "IndustrialGrafter", false)
  GameRegistry.registerCustomItemStack("IndustrialGrafter", grafter.stackWithCharge(0))

  regSimpleItem("MutagenTank")
  regSimpleItem("BeeReceptacle")
  regSimpleItem("PowerModule")
  regSimpleItem("GeneticsProcessor")
  regSimpleItem("EnvProcessor")
  regSimpleItem("UpgradeFrame")
  regSimpleItem("ClimateModule")

  Gendustry.logInfo("Items loaded")
}