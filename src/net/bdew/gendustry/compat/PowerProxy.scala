/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.compat

import java.util

import cpw.mods.fml.common.{Loader, ModAPIManager, ModContainer}
import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.config.Tuning

object PowerProxy {
  final val IC2_MOD_ID = "IC2"
  final val TE_MOD_ID = "CoFHAPI"

  lazy val EUEnabled = Tuning.getSection("Power").getSection("EU").getBoolean("Enabled")
  lazy val RFEnabled = Tuning.getSection("Power").getSection("RF").getBoolean("Enabled")

  lazy val lookup: collection.Map[String, ModContainer] = {
    val mods = new util.ArrayList[ModContainer]
    val nameLookup = new util.HashMap[String, ModContainer]

    nameLookup.putAll(Loader.instance().getIndexedModList)
    ModAPIManager.INSTANCE.injectAPIModContainers(mods, nameLookup)

    import scala.collection.JavaConverters._
    nameLookup.asScala
  }

  lazy val haveIC2 = haveModVersion(IC2_MOD_ID)
  lazy val haveTE = haveModVersion(TE_MOD_ID)

  def haveModVersion(modId: String) = lookup.contains(modId)

  def getModVersion(modId: String): String = {
    val cont = lookup.getOrElse(modId, return "NOT FOUND")
    cont.getModId + " " + cont.getVersion
  }

  def logModVersions() {
    if (!haveIC2 && !haveTE) {
      Gendustry.logWarn("No useable energy system detected")
      Gendustry.logWarn("This mod requires at least one of the following mods to function properly:")
      Gendustry.logWarn("* CoFHCore (or any mod that includes the API)")
      Gendustry.logWarn("* IC2 Experimental")
    }
    Gendustry.logInfo("IC2 Version: %s", getModVersion(IC2_MOD_ID))
    Gendustry.logInfo("RF API Version: %s", getModVersion(TE_MOD_ID))
  }
}
