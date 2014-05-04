/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry

import net.minecraft.client.resources.{ReloadableResourceManager, ResourceManager, ResourceManagerReloadListener}
import net.bdew.lib.Client
import cpw.mods.fml.client.FMLClientHandler
import net.minecraft.util.StringTranslate
import java.io.{File, FileInputStream}

object ResourceListener extends ResourceManagerReloadListener {
  def init() {
    Gendustry.logInfo("Registered reload listener")
    Client.minecraft.getResourceManager.asInstanceOf[ReloadableResourceManager].registerReloadListener(this)
  }

  def loadLangFile(fileName: String) {
    val langfile = new File(Gendustry.configDir, fileName)
    Gendustry.logInfo("Loading language file %s", langfile.getCanonicalPath)
    val stream = new FileInputStream(langfile)
    try {
      StringTranslate.inject(stream)
    } finally {
      stream.close()
    }
  }

  override def onResourceManagerReload(resourcemanager: ResourceManager) {
    val newLang = FMLClientHandler.instance().getCurrentLanguage
    Gendustry.logInfo("Resouece manager reload, new language: %s", newLang)
    val configFiles = Gendustry.configDir.list().sorted
    configFiles.filter(_.endsWith(".en_US.lang")).foreach(loadLangFile)
    if (newLang != "en_US")
      configFiles.filter(_.endsWith("." + newLang + ".lang")).foreach(loadLangFile)
  }
}
