/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.waila

import mcp.mobius.waila.api.IWailaRegistrar
import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.power.TilePowered
import net.bdew.gendustry.machines.apiary.TileApiary

object WailaHandler {
  def loadCallabck(reg: IWailaRegistrar) {
    Gendustry.logInfo("Waila callback recieved, loading...")
    reg.registerBodyProvider(WailaDataslotsDataProvider, classOf[TilePowered])
    reg.registerBodyProvider(WailaApiaryDataProvider, classOf[TileApiary])
    reg.registerSyncedNBTKey("*", classOf[TilePowered])
  }
}
