/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.config.loader

import java.io._

import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.api.IConfigLoader
import net.bdew.lib.Misc
import net.bdew.lib.recipes.RecipesHelper

object TuningLoader extends IConfigLoader {
  val loader = new Loader

  def loadDelayed() = loader.processRecipeStatements()

  def loadConfigFiles() {
    if (!Gendustry.configDir.exists()) {
      Gendustry.configDir.mkdir()
      val nl = System.getProperty("line.separator")
      val f = new FileWriter(new File(Gendustry.configDir, "readme.txt"))
      f.write("Any .cfg files in this directory will be loaded after the internal configuration, in alphabetic order" + nl)
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

  override def loadConfig(reader: Reader): Boolean = {
    Gendustry.logInfo("Loading config submitted by mod %s", Misc.getActiveModId)
    try {
      loader.load(reader)
      true
    } catch {
      case e: Throwable =>
        Gendustry.logErrorException("Error loading config submitted by mod %s", e, Misc.getActiveModId)
        false
    }
  }

  override def loadConfig(file: File): Boolean = {
    Gendustry.logInfo("Loading config %s submitted by mod %s", file.getAbsolutePath, Misc.getActiveModId)
    try {
      Misc.withAutoClose(new FileReader(file)) { reader =>
        loader.load(reader)
        true
      }
    } catch {
      case e: Throwable =>
        Gendustry.logErrorException("Error loading config %s submitted by mod %s", e, file.getAbsolutePath, Misc.getActiveModId)
        false
    }
  }

  override def loadConfig(resourceName: String): Boolean = {
    val res = getClass.getResource(resourceName)
    if (res == null) {
      Gendustry.logError("Unable to load resource %s submitted by mod %s - resource not found", resourceName, Misc.getActiveModId)
      return false
    }
    Gendustry.logInfo("Loading config %s submitted by mod %s", res.toString, Misc.getActiveModId)
    try {
      Misc.withAutoClose(getClass.getResourceAsStream(resourceName)) { stream =>
        loader.load(new InputStreamReader(stream))
        true
      }
    } catch {
      case e: Throwable =>
        Gendustry.logErrorException("Error loading config %s submitted by mod %s", e, getClass.getResource(resourceName).toString, Misc.getActiveModId)
        false
    }
  }
}
