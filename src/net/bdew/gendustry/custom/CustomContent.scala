/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.custom

import forestry.api.apiculture.IAlleleBeeSpecies
import forestry.api.core.{EnumHumidity, EnumTemperature}
import forestry.api.genetics.AlleleManager
import forestry.api.genetics.IClassification.EnumClassLevel
import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.config.Tuning
import net.bdew.gendustry.config.loader.{MReqHumidity, MReqTemperature, TuningLoader}
import net.bdew.lib.recipes.gencfg.ConfigSection

object CustomContent {
  val reg = AlleleManager.alleleRegistry
  var mySpecies = List.empty[BeeSpecies]

  def registerBranches() {
    Gendustry.logInfo("Registering branches")
    val added = (Tuning.getSection("Branches").filterType(classOf[ConfigSection]) collect {
      case (_, cfg) =>
        Gendustry.logInfo("%s -> %s (%s)", cfg.getString("Parent"), cfg.getString("UID"), cfg.getString("Scientific"))
        val cls = reg.createAndRegisterClassification(EnumClassLevel.GENUS, cfg.getString("UID"), cfg.getString("Scientific"))
        reg.getClassification("family." + cfg.getString("Parent")).addMemberGroup(cls)
    }).size
    Gendustry.logInfo("Registered %d branches", added)
  }

  def registerSpecies() {
    Gendustry.logInfo("Registering bees")
    val added = (Tuning.getSection("Bees").filterType(classOf[ConfigSection]) collect {
      case (uid, cfg) =>
        val species = new BeeSpecies(cfg, uid)
        Gendustry.logInfo("Registering %s", species.getUID)
        mySpecies +:= species
        reg.registerAllele(species)
    }).size
    Gendustry.logInfo("Registered %d bees", added)
  }

  def lookupBeeSpecies(uid: String) =
    Option(AlleleManager.alleleRegistry.getAllele(uid))
      .getOrElse(sys.error("Species '%s' not found".format(uid)))
      .asInstanceOf[IAlleleBeeSpecies]

  def registerMuations() {
    Gendustry.logInfo("Registering mutations")

    val added = TuningLoader.loader.mutations count { st =>
      try {

        Gendustry.logInfo("Registering mutation %s + %s = %s", st.parent1, st.parent2, st.result)

        val mutation = new BeeMutation(
          lookupBeeSpecies(st.parent1),
          lookupBeeSpecies(st.parent2),
          lookupBeeSpecies(st.result),
          st.chance)

        if (st.secret) mutation.isSecret = true

        st.requirements foreach {
          case MReqHumidity(hum: String) =>
            mutation.reqHumidity = Some(EnumHumidity.valueOf(hum.toUpperCase))
          case MReqTemperature(temp: String) =>
            mutation.reqTemperature = Some(EnumTemperature.valueOf(temp.toUpperCase))
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
