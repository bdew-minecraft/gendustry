/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.custom

import forestry.api.apiculture.{EnumBeeChromosome, FlowerManager}
import forestry.api.genetics._
import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.config.loader._
import net.bdew.lib.Misc
import net.minecraft.block.Block
import net.minecraftforge.oredict.OreDictionary

object CustomFlowerAlleles {
  var definitions = List.empty[CSFlowerAllele]
  lazy val flowerRegistry = FlowerManager.flowerRegistry

  def addDefinition(definition: CSFlowerAllele) = definitions +:= definition

  def registerAlleles(): Unit = {
    for (CSFlowerAllele(id, definition) <- definitions) {
      val dominant = {
        val entries = Misc.filterType(definition, classOf[FADDominant])
        if (entries.isEmpty) {
          Gendustry.logWarn("Flower allele %s has no Dominant/Recessive flag, assuming dominant", id)
          true
        } else {
          if (entries.size > 1) {
            Gendustry.logWarn("Flower allele %s has multiple Dominant/Recessive flags, only the first will be used", id)
          }
          entries.head.dominant
        }

      }

      val flowerType = "gendustry." + id

      val flowerProvider = CustomFlowerProvider(flowerType, id)

      Gendustry.logDebug("Registering custom flower allele %s", id)

      AlleleManager.alleleFactory.createFlowers(Gendustry.modId, "flowers", id, flowerProvider, dominant, EnumBeeChromosome.FLOWER_PROVIDER)

      for {
        entry <- Misc.filterType(definition, classOf[FADAccepts])
        stackRef <- entry.accepts
        item <- TuningLoader.loader.getAllConcreteStacks(stackRef)
        block <- Option(Block.getBlockFromItem(item.getItem))
      } {
        Gendustry.logDebug("Registering custom acceptable flower for allele %s: %s", id, item)
        if (item.getItemDamage == OreDictionary.WILDCARD_VALUE) {
          flowerRegistry.registerAcceptableFlower(block, flowerType)
        } else {
          flowerRegistry.registerAcceptableFlower(block, item.getItemDamage, flowerType)
        }
      }

      for {
        FADSpread(stackRef, weight) <- Misc.filterType(definition, classOf[FADSpread])
      } {
        val item = TuningLoader.loader.getConcreteStackNoWildcard(stackRef)
        val block = Block.getBlockFromItem(item.getItem)
        Gendustry.logDebug("Registering custom spread flower for allele %s: %s (weight %.03f)", id, item, weight)
        if (block == null)
          Gendustry.logWarn("Definition %s in flower allele %s doesn't refer to a block, it will be ignored", stackRef, id)
        else if (item.getItemDamage == OreDictionary.WILDCARD_VALUE)
          flowerRegistry.registerPlantableFlower(block, 0, weight, flowerType)
        else
          flowerRegistry.registerPlantableFlower(block, item.getItemDamage, weight, flowerType)
      }
    }
    definitions = List.empty
  }
}