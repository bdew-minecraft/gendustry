/*
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.config

import cpw.mods.fml.common.registry.GameRegistry
import net.bdew.gendustry.mutagen.BlockMutagen
import net.bdew.gendustry.test.PowerEmitterBlock
import net.bdew.gendustry.test.PowerEmitterTile
import net.bdew.lib.config.BlockManager
import net.bdew.gendustry.Gendustry

object Blocks extends BlockManager(Config.IDs) {
  var mutagen = regBlockCls(classOf[BlockMutagen], "Mutagen")
  var testPowerEmitter = new PowerEmitterBlock(ids.getBlockId("Test Power Emitter"))
  GameRegistry.registerBlock(testPowerEmitter, "PowerEmitterBlock")
  GameRegistry.registerTileEntity(classOf[PowerEmitterTile], "PowerEmitterBlock")
  Gendustry.logInfo("Blocks loaded")
}