/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.config

import java.io.File

import net.bdew.lib.gui.GuiHandler
import net.minecraftforge.common.config.Configuration

object Config {
  val guiHandler = new GuiHandler

  var neiAddSamples = false

  var neiAddMutagenProducerRecipes = false
  var neiAddMutatronRecipes = false
  var neiAddSamplerRecipes = false
  var neiAddImprinterRecipes = false
  var neiAddExtractorRecipes = false
  var neiAddLiquifierRecipes = false
  var neiAddReplicatorRecipes = false
  var neiAddTransposerRecipes = false

  var powerShowUnits = "MJ"
  var powerShowMultiplier = 1F

  var renderBeeEffects = false
  var beeEffectFrequency = 2

  def load(cfg: File) {
    val c = new Configuration(cfg)
    c.load()

    try {
      neiAddSamples = c.get("NEI", "Add Samples to Search", true).getBoolean(false)

      neiAddMutagenProducerRecipes = c.get("NEI", "Add Mutagen Producer Recipes", true).getBoolean(false)
      neiAddMutatronRecipes = c.get("NEI", "Add Mutatron Recipes", true).getBoolean(false)
      neiAddSamplerRecipes = c.get("NEI", "Add Sampler Recipes", true).getBoolean(false)
      neiAddImprinterRecipes = c.get("NEI", "Add Imprinter Recipes", true).getBoolean(false)
      neiAddExtractorRecipes = c.get("NEI", "Add Extractor Recipes", true).getBoolean(false)
      neiAddLiquifierRecipes = c.get("NEI", "Add Liquifier Recipes", true).getBoolean(false)
      neiAddReplicatorRecipes = c.get("NEI", "Add Replicator Recipes", true).getBoolean(false)
      neiAddTransposerRecipes = c.get("NEI", "Add Transposer Recipes", true).getBoolean(false)

      renderBeeEffects = c.get("Rendering", "Render Bee Effects", true).getBoolean(false)
      beeEffectFrequency = c.get("Rendering", "Bee Effects Frequency", 2, "Higher = less particles").getInt(2)

      if (beeEffectFrequency <= 0) renderBeeEffects = false

      powerShowUnits = c.get("Display", "PowerShowUnits", "RF", "Units to use when displaying power. Valid values: MJ, EU, RF, T", Array("MJ", "EU", "RF", "T")).getString
      if (powerShowUnits != "MJ") powerShowMultiplier = Tuning.getSection("Power").getFloat(powerShowUnits + "_MJ_Ratio")
    } finally {
      c.save()
    }
  }
}