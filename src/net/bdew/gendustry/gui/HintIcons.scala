/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.gui

import net.bdew.gendustry.Gendustry
import net.minecraft.util.ResourceLocation

object HintIcons {
  val sample = new ResourceLocation(Gendustry.modId, "hints/sample")
  val blankSample = new ResourceLocation(Gendustry.modId, "hints/blank_sample")
  val labware = new ResourceLocation(Gendustry.modId, "hints/labware")
  val template = new ResourceLocation(Gendustry.modId, "hints/template")

  val sampleOrTemplate = new ResourceLocation(Gendustry.modId, "hints/sample_template")
  val sampleOrTemplateBlank = new ResourceLocation(Gendustry.modId, "hints/sample_template_blank")
  val queenOrSapling = new ResourceLocation(Gendustry.modId, "hints/queen_sapling")
  val droneOrPollen = new ResourceLocation(Gendustry.modId, "hints/drone_pollen")
  val droneOrSapling = new ResourceLocation(Gendustry.modId, "hints/drone_sapling")

  val queen = new ResourceLocation(Gendustry.modId, "hints/queen")
  val drone = new ResourceLocation(Gendustry.modId, "hints/drone")

  val upgrade = new ResourceLocation(Gendustry.modId, "hints/upgrade")
  val meat = new ResourceLocation(Gendustry.modId, "hints/meat")
}
