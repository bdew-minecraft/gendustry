/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.custom

import net.bdew.gendustry.config.Tuning
import net.bdew.lib.recipes.gencfg.ConfigSection
import forestry.api.genetics.AlleleManager
import forestry.api.genetics.IClassification.EnumClassLevel

object CustomContent {
  val reg = AlleleManager.alleleRegistry
  var mySpecies = List.empty[BeeSpecies]

  def registerBranches() {
    Tuning.getSection("Branches").filterType(classOf[ConfigSection]).foreach({
      case (_, cfg) =>
        val cls = reg.createAndRegisterClassification(EnumClassLevel.GENUS, cfg.getString("UID"), cfg.getString("Scientific"))
        reg.getClassification("family." + cfg.getString("Parent")).addMemberGroup(cls)
    })
  }

  def registerSpecies() {
    Tuning.getSection("Bees").filterType(classOf[ConfigSection]).foreach({
      case (uid, cfg) =>
        val species = new BeeSpecies(cfg, uid)
        mySpecies +:= species
        reg.registerAllele(species)
    })
  }

  def registerTemplates() {
    mySpecies.foreach(sp => sp.getRoot.registerTemplate(sp.getTemplate))
  }
}
