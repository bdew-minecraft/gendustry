/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.apiimpl

import net.bdew.gendustry.api.blocks.IWorkerMachine
import net.bdew.lib.power.TileBaseProcessor

trait TileWorker extends TileBaseProcessor with IWorkerMachine {
  override def getProgress = progress
}
