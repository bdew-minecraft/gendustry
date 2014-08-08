/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.config

import net.bdew.gendustry.machines.mproducer.MachineMutagenProducer
import net.bdew.gendustry.machines.mutatron.MachineMutatron
import net.bdew.gendustry.machines.apiary.MachineApiary
import net.bdew.gendustry.machines.imprinter.MachineImprinter
import net.bdew.gendustry.machines.sampler.MachineSampler
import net.bdew.lib.config.MachineManager
import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.machines.advmutatron.MachineMutatronAdv
import net.bdew.gendustry.machines.liquifier.MachineLiquifier
import net.bdew.gendustry.machines.extractor.MachineExtractor
import net.bdew.gendustry.machines.transposer.MachineTransposer
import net.bdew.gendustry.machines.replicator.MachineReplicator

object Machines extends MachineManager(Tuning.getSection("Machines"), Config.guiHandler) {
  registerMachine(MachineMutagenProducer)
  registerMachine(MachineMutatron)
  registerMachine(MachineApiary)
  registerMachine(MachineImprinter)
  registerMachine(MachineSampler)
  registerMachine(MachineMutatronAdv)
  registerMachine(MachineLiquifier)
  registerMachine(MachineExtractor)
  registerMachine(MachineTransposer)
  registerMachine(MachineReplicator)

  Gendustry.logInfo("Machines loaded")
}
