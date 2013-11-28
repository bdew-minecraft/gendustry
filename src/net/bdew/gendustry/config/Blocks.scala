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
import net.bdew.gendustry.machines.mproducer.BlockMutagenProducer
import net.bdew.gendustry.machines.mutatron.BlockMutatron
import net.bdew.gendustry.mutagen.BlockMutagen
import net.bdew.gendustry.mutagen.FluidMutagen
import net.bdew.gendustry.test.PowerEmitterBlock
import net.bdew.gendustry.test.PowerEmitterTile
import net.minecraftforge.common.Configuration

object Blocks {
  var mutagenFluid: FluidMutagen = null
  var mutagen: BlockMutagen = null
  var mutagenProducer: BlockMutagenProducer = null
  var mutatron: BlockMutatron = null
  var testPowerEmitter: PowerEmitterBlock = null

  def load(cfg: Configuration) {
    mutagenFluid = new FluidMutagen

    mutagen = new BlockMutagen(cfg.getBlock("Mutagen", Ids.blockIds.next()).getInt)
    GameRegistry.registerBlock(mutagen, "Mutagen")

    testPowerEmitter = new PowerEmitterBlock(cfg.getBlock("Test Power Emitter", Ids.blockIds.next()).getInt)
    GameRegistry.registerBlock(testPowerEmitter, "PowerEmitterBlock")
    GameRegistry.registerTileEntity(classOf[PowerEmitterTile], "PowerEmitterBlock")
  }
}