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

object Machines extends MachineManager(Config.IDs, Tuning.getSection("Machines"), Config.guiHandler) {
  val mutagenProducer = registerMachine(new MachineMutagenProducer)
  val mutatron = registerMachine(new MachineMutatron)
  val apiary = registerMachine(new MachineApiary)
  val imprinter = registerMachine(new MachineImprinter)
  val sampler = registerMachine(new MachineSampler)
  val mutatronAdv = registerMachine(new MachineMutatronAdv)

  Gendustry.logInfo("Machines loaded")
}
