/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.misc

import java.io.{File, FileInputStream}

import cpw.mods.fml.client.FMLClientHandler
import net.bdew.gendustry.Gendustry
import net.bdew.lib.Client
import net.minecraft.client.resources.{IReloadableResourceManager, IResourceManager, IResourceManagerReloadListener}
import net.minecraft.util.StringTranslate

object ResourceListener extends IResourceManagerReloadListener {
  def init() {
    Gendustry.logInfo("Registered reload listener")
    Client.minecraft.getResourceManager.asInstanceOf[IReloadableResourceManager].registerReloadListener(this)
  }

  def loadLangFile(fileName: String) {
    val langFile = new File(Gendustry.configDir, fileName)
    Gendustry.logInfo("Loading language file %s", langFile.getCanonicalPath)
    val stream = new FileInputStream(langFile)
    try {
      StringTranslate.inject(stream)
    } finally {
      stream.close()
    }
  }

  override def onResourceManagerReload(rm: IResourceManager) {
    val newLang = FMLClientHandler.instance().getCurrentLanguage
    Gendustry.logInfo("Resource manager reload, new language: %s", newLang)
    val configFiles = Gendustry.configDir.list().sorted
    configFiles.filter(_.endsWith(".en_US.lang")).foreach(loadLangFile)
    if (newLang != "en_US")
      configFiles.filter(_.endsWith("." + newLang + ".lang")).foreach(loadLangFile)
  }
}
