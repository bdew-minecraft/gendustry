/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.misc

import java.io.{File, FileInputStream}

import net.bdew.gendustry.Gendustry
import net.bdew.lib.{Client, Misc}
import net.minecraft.client.resources._
import net.minecraft.util.text.translation.LanguageMap

object ResourceListener extends IResourceManagerReloadListener {
  def init(): Unit = {
    Client.minecraft.getResourceManager.asInstanceOf[IReloadableResourceManager].registerReloadListener(this)
    Gendustry.logInfo("Registered reload listener")
  }

  def loadLangFile(fileName: String): Unit = {
    val langFile = new File(Gendustry.configDir, fileName)
    Gendustry.logInfo("Loading language file %s", langFile.getCanonicalPath)
    Misc.withAutoClose(new FileInputStream(langFile)) { in =>
      LanguageMap.inject(in)
    }
  }

  override def onResourceManagerReload(rm: IResourceManager): Unit = {
    val newLang = Client.minecraft.getLanguageManager.getCurrentLanguage
    Gendustry.logInfo("Resource manager reload, new language: %s", newLang.getLanguageCode)
    val configFiles = Gendustry.configDir.list().sorted
    configFiles.filter(_.endsWith(".en_US.lang")).foreach(loadLangFile)
    if (newLang.getLanguageCode != "en_US")
      configFiles.filter(_.endsWith("." + newLang.getLanguageCode + ".lang")).foreach(loadLangFile)
  }
}
