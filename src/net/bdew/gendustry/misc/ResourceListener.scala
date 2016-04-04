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
import java.util
import java.util.Properties

import net.bdew.gendustry.Gendustry
import net.bdew.lib.{Client, Misc}
import net.minecraft.client.resources.{IReloadableResourceManager, IResourceManager, IResourceManagerReloadListener}
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.common.registry.LanguageRegistry

object ResourceListener extends IResourceManagerReloadListener {
  def init() {
    Gendustry.logInfo("Registered reload listener")
    Client.minecraft.getResourceManager.asInstanceOf[IReloadableResourceManager].registerReloadListener(this)
  }

  def loadLangFile(lang: String, fileName: String) {
    val langFile = new File(Gendustry.configDir, fileName)
    Gendustry.logInfo("Loading language file %s", langFile.getCanonicalPath)
    Misc.withAutoClose(new FileInputStream(langFile)) { in =>
      val langPack = new Properties()
      langPack.load(in)
      val map = new util.HashMap[String, String]()
      map.putAll(langPack.asInstanceOf[util.Map[String, String]])
      LanguageRegistry.instance().injectLanguage(lang.intern(), map)
    }
  }

  override def onResourceManagerReload(rm: IResourceManager) {
    val newLang = FMLClientHandler.instance().getCurrentLanguage
    Gendustry.logInfo("Resource manager reload, new language: %s", newLang)
    val configFiles = Gendustry.configDir.list().sorted
    configFiles.filter(_.endsWith(".en_US.lang")).foreach(loadLangFile(newLang, _))
    if (newLang != "en_US")
      configFiles.filter(_.endsWith("." + newLang + ".lang")).foreach(loadLangFile(newLang, _))
    Client.minecraft.getLanguageManager.onResourceManagerReload(rm)
  }
}
