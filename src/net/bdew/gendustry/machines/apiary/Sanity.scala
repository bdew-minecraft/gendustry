/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.machines.apiary

import _root_.forestry.api.apiculture.IBeeRoot
import forestry.api.genetics.AlleleManager
import net.bdew.gendustry.Gendustry
import net.minecraft.util.{ChatComponentTranslation, EnumChatFormatting}

object Sanity {
  var checkDone = false
  def check(house: TileApiary): Unit = {
    if (checkDone) return
    checkDone = true
    import scala.collection.JavaConversions._
    try {
      val bees = AlleleManager.alleleRegistry.getSpeciesRoot("rootBees").asInstanceOf[IBeeRoot]
      val defGenome = bees.templateAsGenome(bees.getDefaultTemplate)
      val defSpecies = defGenome.getPrimary
      var found = false
      for (mutation <- bees.getMutations(false)) {
        if (mutation.getChance(house, defSpecies.asInstanceOf, defSpecies, defGenome, defGenome) > 0) {
          found = true
          Gendustry.logWarn("Detected probably bugged mutation! %s+%s (class: %s) doesn't check the species. Please report it to the mod author.",
            mutation.getAllele0.getName, mutation.getAllele1.getName, mutation.getClass.getCanonicalName)
        }
      }
      if (found) {
        val player = house.getWorld.getClosestPlayer(house.xCoord, house.yCoord, house.zCoord, 20)
        if (player != null) {
          player.addChatMessage(new ChatComponentTranslation(
            EnumChatFormatting.RED + "[Gendustry] WARNING! Possibly bugged mutations detected, check the log for details."))
        }
      }
    } catch {
      case t: Throwable => Gendustry.logWarnException("Error in mutations sanity check", t)
    }
  }
}
