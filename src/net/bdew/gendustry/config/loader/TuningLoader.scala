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
import cpw.mods.fml.common.FMLCommonHandler

object TuningLoader {

  // Mutations are collected here for later processing

  val loader = new Loader

  def loadDealayed() = loader.processDelayedStatements()

  def loadConfigFiles() {
    val listReader = new BufferedReader(new InputStreamReader(
      getClass.getResourceAsStream("/assets/gendustry/config/files.lst")))
    val list = Iterator.continually(listReader.readLine)
      .takeWhile(_ != null)
      .map(_.trim)
      .filterNot(_.startsWith("#"))
      .filterNot(_.isEmpty)
      .toList
    listReader.close()

    if (!Gendustry.configDir.exists()) {
      Gendustry.configDir.mkdir()
      val f = new FileWriter(new File(Gendustry.configDir, "readme.txt"))
      f.write("Any .cfg files in this directory will be loaded after the internal configuration, in alpahabetic order\n")
      f.write("Files in 'overrides' directory with matching names cab be used to override internal configuration\n")
      f.close()
    }

    val overrideDir = new File(Gendustry.configDir, "overrides")
    if (!overrideDir.exists()) overrideDir.mkdir()

    Gendustry.logInfo("Loading internal config files")

    for (fileName <- list) {
      val overrideFile = new File(overrideDir, fileName)
      if (overrideFile.exists()) {
        tryLoadConfig(new FileReader(overrideFile), overrideFile.getCanonicalPath)
      } else {
        val resname = "/assets/gendustry/config/" + fileName
        tryLoadConfig(new InputStreamReader(getClass.getResourceAsStream(resname)), getClass.getResource(resname).toString)
      }
    }

    Gendustry.logInfo("Loading user config files")

    for (fileName <- Gendustry.configDir.list().sorted if fileName.endsWith(".cfg")) {
      val file = new File(Gendustry.configDir, fileName)
      if (file.canRead) tryLoadConfig(new FileReader(file), file.getCanonicalPath)
    }
  }

  def tryLoadConfig(reader: Reader, path: String) {
    Gendustry.logInfo("Loading config: %s", path)
    try {
      loader.load(reader)
    } catch {
      case e: Throwable =>
        FMLCommonHandler.instance().raiseException(e, "Gendustry config loading failed in file %s: %s".format(path, e.getMessage), true)
    } finally {
      reader.close()
    }
  }
}
