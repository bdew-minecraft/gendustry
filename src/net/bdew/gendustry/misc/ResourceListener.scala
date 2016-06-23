/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.misc

import java.io.{File, FileInputStream, InputStream}

import net.bdew.gendustry.Gendustry
import net.bdew.lib.{Client, Misc}
import net.minecraft.client.resources._

object ResourceListener extends IResourceManagerReloadListener {
  lazy val mLoadLocaleData = classOf[Locale].getDeclaredMethod("loadLocaleData", classOf[InputStream])
  lazy val fCurrentLocale = classOf[LanguageManager].getDeclaredField("CURRENT_LOCALE")

  def init() {
    mLoadLocaleData.setAccessible(true)
    fCurrentLocale.setAccessible(true)
    Client.minecraft.getResourceManager.asInstanceOf[IReloadableResourceManager].registerReloadListener(this)
    Gendustry.logInfo("Registered reload listener")
  }

  def loadLangFile(lang: Locale, fileName: String) {
    val langFile = new File(Gendustry.configDir, fileName)
    Gendustry.logInfo("Loading language file %s", langFile.getCanonicalPath)
    Misc.withAutoClose(new FileInputStream(langFile)) { in =>
      mLoadLocaleData.invoke(lang, in)
    }
  }

  override def onResourceManagerReload(rm: IResourceManager) {
    val newLang = Client.minecraft.getLanguageManager.getCurrentLanguage
    val locale = fCurrentLocale.get(null).asInstanceOf[Locale]
    Gendustry.logInfo("Resource manager reload, new language: %s", newLang.getLanguageCode)
    val configFiles = Gendustry.configDir.list().sorted
    configFiles.filter(_.endsWith(".en_US.lang")).foreach(loadLangFile(locale, _))
    if (newLang.getLanguageCode != "en_US")
      configFiles.filter(_.endsWith("." + newLang + ".lang")).foreach(loadLangFile(locale, _))
    Client.minecraft.getLanguageManager.onResourceManagerReload(rm)
  }
}
