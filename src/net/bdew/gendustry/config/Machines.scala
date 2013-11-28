/*
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.config

import net.minecraftforge.common.Configuration
import net.bdew.gendustry.machines.mproducer.MachineMutagenProducer
import net.bdew.gendustry.machines.mutatron.MachineMutatron
import net.bdew.gendustry.machines.apiary.MachineApiary
import net.bdew.gendustry.machines.imprinter.MachineImprinter
import net.bdew.gendustry.machines.sampler.MachineSampler

object Machines {
  var mutagenProducer: MachineMutagenProducer = null
  var mutatron: MachineMutatron = null
  var apiary: MachineApiary = null
  var imprinter: MachineImprinter = null
  var sampler: MachineSampler = null

  def load(cfg: Configuration) {
    mutagenProducer = new MachineMutagenProducer(cfg)
    mutatron = new MachineMutatron(cfg)
    apiary = new MachineApiary(cfg)
    imprinter = new MachineImprinter(cfg)
    sampler = new MachineSampler(cfg)
  }
}
