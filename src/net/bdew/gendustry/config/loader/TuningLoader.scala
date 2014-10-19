/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.config.loader

import java.io._

import net.bdew.gendustry.Gendustry
import net.bdew.lib.recipes.RecipesHelper

object TuningLoader {
  val loader = new Loader

  def loadDealayed() = loader.processRecipeStatements()

  def loadConfigFiles() {
    if (!Gendustry.configDir.exists()) {
      Gendustry.configDir.mkdir()
      val nl = System.getProperty("line.separator")
      val f = new FileWriter(new File(Gendustry.configDir, "readme.txt"))
      f.write("Any .cfg files in this directory will be loaded after the internal configuration, in alpahabetic order" + nl)
      f.write("Files in 'overrides' directory with matching names cab be used to override internal configuration" + nl)
      f.close()
    }

    RecipesHelper.loadConfigs(
      modName = "Gendustry",
      listResource = "/assets/gendustry/config/files.lst",
      configDir = Gendustry.configDir,
      resBaseName = "/assets/gendustry/config/",
      loader = loader)
  }
}
