/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.custom

import java.util.Locale

import forestry.api.apiculture.IAlleleBeeSpecies
import forestry.api.apiculture.hives.HiveManager
import forestry.api.core.{EnumHumidity, EnumTemperature}
import forestry.api.genetics.AlleleManager
import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.blocks.BeeHive
import net.bdew.gendustry.config.Blocks
import net.bdew.gendustry.config.loader._
import net.bdew.gendustry.custom.hives._
import net.bdew.lib.Misc
import net.minecraft.block.Block
import net.minecraft.item.ItemBlock
import net.minecraft.world.biome.Biome
import net.minecraftforge.oredict.OreDictionary

object CustomHives {
  var definitions = List.empty[CSHiveDefinition]
  var hives = Map.empty[String, HiveDescription]

  def registerHiveDefinition(definition: CSHiveDefinition): Unit = {
    definitions :+= definition
  }

  def getSingleStatement[T <: HiveDefStatement](definition: CSHiveDefinition, cls: Class[T]) = {
    val l = Misc.filterType(definition.definition, cls)
    if (l.size > 1)
      Gendustry.logWarn("Multiple entries of type %s in BeeHive definition '%s' - all but the first one will be ignored", cls.getSimpleName, definition.id)
    l.headOption
  }

  def resolveFilter(f: BlockFilterDef) = f match {
    case BlockFilterDefAir => BlockFilterAir
    case BlockFilterDefLeaves => BlockFilterLeaves
    case BlockFilterDefReplaceable => BlockFilterReplaceable
    case BlockFilterRef(list) =>
      val blocks = for {
        ref <- list
        stack <- TuningLoader.loader.getAllConcreteStacks(ref)
      } yield {
        if (stack.isEmpty || !stack.getItem.isInstanceOf[ItemBlock]) {
          Gendustry.logWarn("Error resolving filter %s - stackref %s does not resolve to a block", f, ref)
          None
        } else {
          Some(Block.getBlockFromItem(stack.getItem) -> (if (stack.getItemDamage == OreDictionary.WILDCARD_VALUE) -1 else stack.getItemDamage))
        }
      }
      BlockFilterList(blocks.flatten.toSet)
  }

  def registerHives(): Unit = {
    import scala.collection.JavaConversions._

    val validBiomes = Biome.REGISTRY.map(x => x.getRegistryName.toString.toLowerCase(Locale.US) -> x).toMap


    for (definition <- definitions) {
      Gendustry.logDebug("Processing Beehive definition: %s", definition)

      val failedCondition = Misc.filterType(definition.definition, classOf[HDSpawnIf]) find (c => !TuningLoader.loader.resolveCondition(c.condition))

      if (failedCondition.isDefined) {
        Gendustry.logDebug("Condition unmet: %s - not registering", failedCondition.get.condition)
      } else {
        val spawnChance = getSingleStatement(definition, classOf[HDSpawnChance]) map (_.chance) getOrElse 1F

        val range = getSingleStatement(definition, classOf[HDYRange]) getOrElse HDYRange(0, 255)

        val biomeNames = (Misc.filterType(definition.definition, classOf[HDBiomes]) flatMap (_.biomes) map (_.toLowerCase(Locale.US))).toSet
        val biomes =
          if (biomeNames.isEmpty || biomeNames.contains("all"))
            validBiomes.values.toSet
          else
            validBiomes.filterKeys(biomeNames.contains).values.toSet

        val temperatureNames = (Misc.filterType(definition.definition, classOf[HDTemperature]) flatMap (_.temperatures) map (_.toLowerCase(Locale.US))).toSet
        val temperatures =
          if (temperatureNames.isEmpty || temperatureNames.contains("all"))
            EnumTemperature.values().toSet
          else
            EnumTemperature.values().filter(x => temperatureNames.contains(x.getName.toLowerCase(Locale.US))).toSet

        val humidityNames = (Misc.filterType(definition.definition, classOf[HDHumidity]) flatMap (_.humidityLevels) map (_.toLowerCase(Locale.US))).toSet
        val humidities =
          if (humidityNames.isEmpty || humidityNames.contains("all"))
            EnumHumidity.values().toSet
          else
            EnumHumidity.values().filter(x => humidityNames.contains(x.getName.toLowerCase(Locale.US))).toSet


        var conditions = (for (condition <- Misc.filterType(definition.definition, classOf[HiveDefCondition])) yield {
          condition match {
            case HDLocationUnder(f) => Some(ConditionUnder(resolveFilter(f)))
            case HDLocationAbove(f) => Some(ConditionAbove(resolveFilter(f)))
            case HDLocationNextTo(f) => Some(ConditionNextTo(resolveFilter(f)))
            case HDLocationNear(f) => Some(ConditionNear(resolveFilter(f)))
            case HDReplace(f) => Some(ConditionReplace(resolveFilter(f)))
            case _ =>
              Gendustry.logWarn("Unknown condition %s", condition)
              None
          }
        }).flatten.toList

        if (!conditions.exists(_.isInstanceOf[ConditionReplace])) {
          conditions :+= ConditionReplace(BlockFilterAir)
        }

        val drops = (for (drop <- Misc.filterType(definition.definition, classOf[HDDrops]).flatMap(_.drops)) yield {
          val stacks = drop.additional map (ref => TuningLoader.loader.getConcreteStackNoWildcard(ref))
          val species = AlleleManager.alleleRegistry.getAllele(drop.uid)

          if (species.isInstanceOf[IAlleleBeeSpecies]) {
            Some(HiveDrop(drop.chance / 100.0, species.asInstanceOf[IAlleleBeeSpecies], drop.ignobleShare, stacks))
          } else {
            Gendustry.logWarn("%s is not a valid bee species in hive definition %s", drop.uid, definition.id)
            None
          }
        }).flatten

        if (drops.isEmpty) {
          Gendustry.logWarn("Hive definition %s contains no valid drops", definition.id)
        }

        val spawnDebug = Misc.filterType(definition.definition, classOf[HDSpawnDebug]).exists(_.debug)

        val hive = HiveDescription(
          id = definition.id,
          chance = spawnChance,
          yMin = range.min,
          yMax = range.max,
          validBiome = biomes,
          validTemperature = temperatures,
          validHumidity = humidities,
          conditions = conditions,
          drops = drops.toList,
          spawnDebug = spawnDebug
        )

        Gendustry.logDebug("Registering hive definition: %s", hive)

        HiveManager.hiveRegistry.registerHive("Gendustry:" + definition.id, hive)

        hives += definition.id -> hive

        val modelLocation = getSingleStatement(definition, classOf[HDModelLocation]) map (_.loc) getOrElse
          Misc.iconName(Gendustry.modId, "beehives", definition.id)

        val lightLevel = getSingleStatement(definition, classOf[HDLight]) map (_.level) getOrElse 0
        val color = getSingleStatement(definition, classOf[HDColor]) map (_.color) getOrElse 0xFFFFFF

        val block = BeeHive(
          hiveId = definition.id,
          modelLocation = modelLocation,
          color = color,
          lightLevel = lightLevel,
          hive = hive
        )

        Gendustry.logDebug("Registered hive block: %s", block)

        Blocks.regBlock(block)
      }
    }

    // clear data that's no longer needed
    definitions = List.empty
  }
}
