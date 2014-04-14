/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.gui.rscontrol

import net.bdew.lib.data.base.{UpdateKind, TileDataSlots}

trait TileRSContollable extends TileDataSlots {
  val rsmode = DataslotRSMode("rsmode", this).setUpdate(UpdateKind.SAVE, UpdateKind.GUI, UpdateKind.WORLD)

  def canWork = {
    val powered = getWorldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord)
    (rsmode :== RSMode.ALWAYS) ||
      ((rsmode :== RSMode.RS_ON) && powered) ||
      ((rsmode :== RSMode.RS_OFF) && !powered)
  }
}
