/*
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.machines

import net.minecraftforge.common.Configuration

abstract class ProcessorMachine(cfg: Configuration, name: String) extends PoweredMachine(cfg, name) {
  lazy val mjPerItem = tuning.getFloat("MjPerItem")
  lazy val powerUseRate = tuning.getFloat("PowerUseRate")
}
