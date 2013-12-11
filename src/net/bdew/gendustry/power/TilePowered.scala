/*
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.power

import net.bdew.lib.tile.TileExtended
import net.bdew.gendustry.machines.PoweredMachine

trait TilePoweredBase extends TileExtended {
  def power: DataSlotPower
  def configurePower(cfg: PoweredMachine) = power.configure(cfg)
}

trait TilePowered extends TilePoweredBase with TilePoweredMJ with TilePoweredRF with TilePoweredEU
