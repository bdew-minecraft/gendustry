/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.gui

import net.bdew.lib.render.IconPreloader

object HintIcons extends IconPreloader(1) {
  val sample = TextureLoc("gendustry:hints/sample")
  val blankSample = TextureLoc("gendustry:hints/blank_sample")
  val labware = TextureLoc("gendustry:hints/labware")
  val template = TextureLoc("gendustry:hints/template")

  val sampleOrTemplate = TextureLoc("gendustry:hints/sample_template")
  val sampleOrTemplateBlank = TextureLoc("gendustry:hints/sample_template_blank")
  val queenOrSapling = TextureLoc("gendustry:hints/queen_sapling")
  val droneOrPollen = TextureLoc("gendustry:hints/drone_pollen")
  val droneOrSapling = TextureLoc("gendustry:hints/drone_sapling")

  val queen = TextureLoc("gendustry:hints/queen")
  val drone = TextureLoc("gendustry:hints/drone")

  val upgrade = TextureLoc("gendustry:hints/upgrade")
  val meat = TextureLoc("gendustry:hints/meat")
}
