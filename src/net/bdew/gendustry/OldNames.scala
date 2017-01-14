/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry

import java.util.Locale

import net.bdew.gendustry.config.Items
import net.bdew.gendustry.custom.{CustomHoneyComb, CustomHoneyDrop}
import net.bdew.gendustry.items._
import net.bdew.gendustry.items.covers.{EjectCover, ErrorSensorCover, ImportCover}
import net.bdew.gendustry.machines.advmutatron.BlockMutatronAdv
import net.bdew.gendustry.machines.apiary.BlockApiary
import net.bdew.gendustry.machines.apiary.upgrades.ItemApiaryUpgrade
import net.bdew.gendustry.machines.extractor.BlockExtractor
import net.bdew.gendustry.machines.imprinter.BlockImprinter
import net.bdew.gendustry.machines.liquifier.BlockLiquifier
import net.bdew.gendustry.machines.mproducer.BlockMutagenProducer
import net.bdew.gendustry.machines.mutatron.BlockMutatron
import net.bdew.gendustry.machines.replicator.BlockReplicator
import net.bdew.gendustry.machines.sampler.BlockSampler
import net.bdew.gendustry.machines.transposer.BlockTransposer
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraftforge.fml.common.event.FMLMissingMappingsEvent.MissingMapping
import net.minecraftforge.fml.common.registry.GameRegistry.Type
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry

object OldNames {
  val map: Map[String, IForgeRegistryEntry[_]] = Map(
    "Labware" -> Items.labware,
    "Waste" -> Items.waste,
    "GeneSampleBlank" -> Items.geneSampleBlank,
    "GeneSample" -> GeneSample,
    "GeneTemplate" -> GeneTemplate,
    "apiary.upgrade" -> ItemApiaryUpgrade,
    "IndustrialGrafter" -> IndustrialGrafter,
    "IndustrialScoop" -> IndustrialScoop,
    "HoneyComb" -> CustomHoneyComb,
    "HoneyDrop" -> CustomHoneyDrop,
    "MutagenTank" -> Items.mutagenTank,
    "BeeReceptacle" -> Items.beeReceptacle,
    "PowerModule" -> Items.powerModule,
    "GeneticsProcessor" -> Items.geneticsProcessor,
    "EnvProcessor" -> Items.environmentProcessor,
    "UpgradeFrame" -> Items.upgradeFrame,
    "ClimateModule" -> Items.climateModule,
    "PollenKit" -> PollenKit,
    "HiveSpawnDebugger" -> HiveSpawnDebugger,
    "EjectCover" -> EjectCover,
    "ImportCover" -> ImportCover,
    "ErrorSensorCover" -> ErrorSensorCover,

    "MutagenProducer" -> BlockMutagenProducer,
    "Mutatron" -> BlockMutatron,
    "IndustrialApiary" -> BlockApiary,
    "Imprinter" -> BlockImprinter,
    "Sampler" -> BlockSampler,
    "MutatronAdv" -> BlockMutatronAdv,
    "Liquifier" -> BlockLiquifier,
    "Extractor" -> BlockExtractor,
    "Transposer" -> BlockTransposer,
    "Replicator" -> BlockReplicator
  )

  val lowerMap: Map[String, IForgeRegistryEntry[_]] = map.map(x => "gendustry:" + x._1.toLowerCase(Locale.US) -> x._2).toMap

  def checkRemap(mapping: MissingMapping): Unit = {
    lowerMap.get(mapping.name) match {
      case Some(x: Block) if mapping.`type` == Type.BLOCK => mapping.remap(x)
      case Some(x: Block) if mapping.`type` == Type.ITEM => mapping.remap(Item.getItemFromBlock(x))
      case Some(x: Item) if mapping.`type` == Type.ITEM => mapping.remap(x)
      case _ => //nothing
    }
  }
}
