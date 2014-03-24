/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.gui.rscontrol

import net.bdew.lib.data.base.{UpdateKind, DataSlotVal, TileDataSlots}
import net.minecraft.nbt.NBTTagCompound

case class DataslotRSMode(name: String, parent: TileDataSlots) extends DataSlotVal[RSMode.Value] {
  var cval: RSMode.Value = RSMode.ALWAYS
  def save(t: NBTTagCompound, kind: UpdateKind.Value) = t.setByte(name, cval.id.toByte)
  def load(t: NBTTagCompound, kind: UpdateKind.Value) = cval = RSMode(t.getByte(name))
}
