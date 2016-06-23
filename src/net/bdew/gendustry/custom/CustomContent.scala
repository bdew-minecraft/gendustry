/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.custom

import java.util.Locale

import forestry.api.apiculture.{EnumBeeChromosome, IAlleleBeeSpecies}
import forestry.api.core.{EnumHumidity, EnumTemperature}
import forestry.api.genetics.AlleleManager
import forestry.api.genetics.IClassification.EnumClassLevel
import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.config.Tuning
import net.bdew.gendustry.config.loader._
import net.bdew.lib.Misc
import net.bdew.lib.recipes.StackBlock
import net.bdew.lib.recipes.gencfg.ConfigSection
import net.minecraft.block.Block
import net.minecraft.util.ResourceLocation
import net.minecraft.world.biome.Biome
import net.minecraftforge.oredict.OreDictionary

object CustomContent {
  val reg = AlleleManager.alleleRegistry
  var mySpecies = List.empty[BeeSpecies]

  def registerBranches() {
    Gendustry.logDebug("Registering branches")
    val added = (Tuning.getOrAddSection("Branches").filterType(classOf[ConfigSection]) collect {
      case (_, cfg) =>
        Gendustry.logDebug("%s -> %s (%s)", cfg.getString("Parent"), cfg.getString("UID"), cfg.getString("Scientific"))
        val cls = reg.createAndRegisterClassification(EnumClassLevel.GENUS, cfg.getString("UID"), cfg.getString("Scientific"))
        reg.getClassification("family." + cfg.getString("Parent")).addMemberGroup(cls)
    }).size
    Gendustry.logInfo("Registered %d branches", added)
  }

  def registerSpecies() {
    Gendustry.logDebug("Registering bees")
    val added = (Tuning.getOrAddSection("Bees").filterType(classOf[ConfigSection]) collect {
      case (uid, cfg) =>
        if (cfg.hasValue("RequireMod") && !Misc.haveModVersion(cfg.getString("RequireMod"))) {
          Gendustry.logInfo("Not registering species '%s' - required mod '%s' is not loaded", uid, cfg.getString("RequireMod"))
        } else if (cfg.hasValue("RequireOreDict") && OreDictionary.getOres(cfg.getString("RequireOreDict")).size() == 0) {
          Gendustry.logInfo("Not registering species '%s' - required ore dictionary entry '%s' not found", uid, cfg.getString("RequireOreDict"))
        } else {
          val species = new BeeSpecies(cfg, uid)
          Gendustry.logDebug("Registering %s", species.getUID)
          reg.registerAllele(species, EnumBeeChromosome.SPECIES)
          mySpecies +:= species
        }
    }).size
    Gendustry.logInfo("Registered %d bees", added)
  }

  def lookupBeeSpecies(uid: String) =
    Option(AlleleManager.alleleRegistry.getAllele(uid))
      .getOrElse(sys.error("Species '%s' not found".format(uid)))
      .asInstanceOf[IAlleleBeeSpecies]

  def registerMutations() {
    Gendustry.logDebug("Registering mutations")

    val added = TuningLoader.loader.mutations count { st =>
      try {

        Gendustry.logDebug("Registering mutation %s + %s = %s", st.parent1, st.parent2, st.result)

        val mutation = new BeeMutation(
          lookupBeeSpecies(st.parent1),
          lookupBeeSpecies(st.parent2),
          lookupBeeSpecies(st.result),
          st.chance)

        if (st.secret) mutation.isSecret = true

        st.requirements foreach {
          case MReqHumidity(hum: String) =>
            mutation.reqHumidity = Some(EnumHumidity.valueOf(hum.toUpperCase(Locale.US)))
          case MReqTemperature(temp: String) =>
            mutation.reqTemperature = Some(EnumTemperature.valueOf(temp.toUpperCase(Locale.US)))
          case MReqBlock(ref: StackBlock) =>
            val stack = TuningLoader.loader.getConcreteStack(ref)
            val block = Block.getBlockFromItem(stack.getItem)
            if (block == null) sys.error("Invalid block reference: %s".format(ref))
            mutation.reqBlock = Some(block)
            if (stack.getItemDamage != OreDictionary.WILDCARD_VALUE)
              mutation.reqBlockMeta = Some(stack.getItemDamage)
          case MReqBiome(name: String) =>
            mutation.reqBiome = Option(Biome.REGISTRY.getObject(new ResourceLocation(name)))
        }

        mutation.getRoot.registerMutation(mutation)

        true
      } catch {
        case e: Throwable =>
          Gendustry.logWarn("Adding mutation failed: %s (%s)", e.getMessage, st)
          false
      }
    }
    Gendustry.logInfo("Registered %d mutations", added)
  }

  def registerTemplates() {
    mySpecies.foreach(sp => sp.getRoot.registerTemplate(sp.getTemplate))
  }
}
