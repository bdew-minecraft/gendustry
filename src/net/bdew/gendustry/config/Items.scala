/*
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.config

import cpw.mods.fml.common.registry.GameRegistry
import forestry.api.core.ItemInterface
import net.bdew.gendustry.mutagen.ItemMutagenBucket
import net.bdew.gendustry.mutagen.ItemMutagenCan
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.common.Configuration
import net.minecraftforge.fluids.FluidContainerRegistry
import net.bdew.gendustry.items.{IndustrialGrafter, GeneTemplate, GeneSample, SimpleItem}
import net.bdew.gendustry.machines.apiary.upgrades.ItemApiaryUpgrade

object Items {
  var mutagenBucket: ItemMutagenBucket = null
  var mutagenCan: ItemMutagenCan = null
  var labware: SimpleItem = null
  var waste: SimpleItem = null
  var geneSample: GeneSample = null
  var geneSampleBlank: SimpleItem = null
  var geneTemplate: GeneTemplate = null
  var upgradeItem: ItemApiaryUpgrade = null
  var grafter: IndustrialGrafter = null

  def regSimpleItem(cfg: Configuration, name: String): SimpleItem =
    regItem(new SimpleItem(cfg.getItem(name, Ids.itemIds.next()).getInt, name), name)

  def regItem[T <: SimpleItem](item: T): T = regItem(item, item.name)

  def regItem[T <: Item](item: T, name: String, addStack: Boolean = true): T = {
    GameRegistry.registerItem(item, name)
    if (addStack)
      GameRegistry.registerCustomItemStack(name, new ItemStack(item))
    return item
  }

  def load(cfg: Configuration) {
    mutagenBucket = regItem(new ItemMutagenBucket(cfg.getItem("MutagenBucket", Ids.itemIds.next()).getInt),"MutagenBucket")
    mutagenCan = regItem(new ItemMutagenCan(cfg.getItem("MutagenCan", Ids.itemIds.next()).getInt), "MutagenCan")

    FluidContainerRegistry.registerFluidContainer(Blocks.mutagenFluid, new ItemStack(mutagenBucket), new ItemStack(Item.bucketEmpty))
    FluidContainerRegistry.registerFluidContainer(Blocks.mutagenFluid, new ItemStack(mutagenCan), ItemInterface.getItem("canEmpty"))

    geneSample = regItem(new GeneSample(cfg.getItem("GeneSample", Ids.itemIds.next()).getInt))
    geneTemplate = regItem(new GeneTemplate(cfg.getItem("GeneTemplate", Ids.itemIds.next()).getInt))

    upgradeItem = regItem(new ItemApiaryUpgrade(cfg.getItem("ApiaryUpgrade", Ids.itemIds.next()).getInt), "ApiaryUpgrade")

    grafter = regItem(new IndustrialGrafter(cfg.getItem("IndustrialGrafter", Ids.itemIds.next()).getInt), "IndustrialGrafter", false)
    GameRegistry.registerCustomItemStack("IndustrialGrafter", grafter.stackWithCharge(0))

    regSimpleItem(cfg, "MutagenTank")
    regSimpleItem(cfg, "BeeReceptacle")
    regSimpleItem(cfg, "PowerModule")
    regSimpleItem(cfg, "GeneticsProcessor")
    regSimpleItem(cfg, "UpgradeFrame")
    regSimpleItem(cfg, "ClimateModule")

    labware = regSimpleItem(cfg, "Labware")
    waste = regSimpleItem(cfg, "Waste")
    geneSampleBlank = regSimpleItem(cfg, "GeneSampleBlank")

  }
}