/*
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.config

import net.minecraftforge.common.Configuration
import java.io.File

object Config {
  var neiAddSamples = false

  def load(cfg: File): Configuration = {
    val c = new Configuration(cfg)
    c.load()
    c.addCustomCategoryComment("machines enabled", "Disabling a machine will remove it's block registration, loading any world will remove the blocks permanently")

    neiAddSamples = c.get("NEI", "Add samples", true).getBoolean(false)

    return c
  }
}