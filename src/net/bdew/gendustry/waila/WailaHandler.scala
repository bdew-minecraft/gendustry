/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.waila

import mcp.mobius.waila.api.IWailaRegistrar
import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.machines.apiary.TileApiary
import net.bdew.gendustry.power.TilePowered

object WailaHandler {
  def loadCallback(reg: IWailaRegistrar) {
    Gendustry.logInfo("WAILA callback received, loading...")
    reg.registerBodyProvider(WailaDataSlotsDataProvider, classOf[TilePowered])
    reg.registerBodyProvider(WailaApiaryDataProvider, classOf[TileApiary])
    reg.registerSyncedNBTKey("*", classOf[TilePowered])
  }
}
