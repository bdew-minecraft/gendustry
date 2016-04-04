/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.gui.rscontrol

import net.bdew.lib.data.base.{TileDataSlots, UpdateKind}

trait TileRSControllable extends TileDataSlots {
  val rsmode = DataSlotRSMode("rsmode", this).setUpdate(UpdateKind.SAVE, UpdateKind.GUI, UpdateKind.WORLD)

  def canWork = {
    val powered = getWorld.isBlockIndirectlyGettingPowered(getPos) > 0
    (rsmode :== RSMode.ALWAYS) ||
      ((rsmode :== RSMode.RS_ON) && powered) ||
      ((rsmode :== RSMode.RS_OFF) && !powered)
  }
}
