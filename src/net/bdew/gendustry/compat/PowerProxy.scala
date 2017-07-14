/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.compat

import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.config.Tuning
import net.bdew.lib.Misc

object PowerProxy {
  final val IC2_MOD_ID = "ic2"
  final val TE_MOD_ID = "CoFHAPI"
  final val TESLA_MOD_ID = "tesla"

  lazy val EUEnabled = Tuning.getSection("Power").getSection("EU").getBoolean("Enabled")
  lazy val RFEnabled = Tuning.getSection("Power").getSection("RF").getBoolean("Enabled")
  lazy val TeslaEnabled = Tuning.getSection("Power").getSection("Tesla").getBoolean("Enabled")
  lazy val ForgeEnabled = Tuning.getSection("Power").getSection("Forge").getBoolean("Enabled")

  lazy val haveIC2 = Misc.haveModVersion(IC2_MOD_ID)
  lazy val haveTE = Misc.haveModVersion(TE_MOD_ID)
  lazy val haveTesla = Misc.haveModVersion(TESLA_MOD_ID)

  def logModVersions() {
    Gendustry.logInfo("IC2 Version: %s", Misc.getModVersionString(IC2_MOD_ID))
    Gendustry.logInfo("RF API Version: %s", Misc.getModVersionString(TE_MOD_ID))
    Gendustry.logInfo("Tesla Version: %s", Misc.getModVersionString(TESLA_MOD_ID))
  }
}
