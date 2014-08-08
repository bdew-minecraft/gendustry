/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.apiimpl

import net.bdew.lib.power.TileBaseProcessor
import net.bdew.gendustry.api.blocks.IWorkerMachine

trait TileWorker extends TileBaseProcessor with IWorkerMachine {
  override def getProgress = progress
}
