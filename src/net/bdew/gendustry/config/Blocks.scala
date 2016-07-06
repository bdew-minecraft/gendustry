/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.config

import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.misc.GendustryCreativeTabs
import net.bdew.lib.config.BlockManager

object Blocks extends BlockManager(GendustryCreativeTabs.main) {
  Gendustry.logInfo("Blocks loaded")
}