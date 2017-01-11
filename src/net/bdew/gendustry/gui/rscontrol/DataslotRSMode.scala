/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.gui.rscontrol

import net.bdew.lib.data.base.{DataSlotContainer, DataSlotVal, UpdateKind}
import net.minecraft.nbt.NBTTagCompound

case class DataSlotRSMode(name: String, parent: DataSlotContainer) extends DataSlotVal[RSMode.Value] {
  override def default = RSMode.ALWAYS
  override def save(t: NBTTagCompound, kind: UpdateKind.Value) = t.setByte(name, value.id.toByte)
  override def loadValue(t: NBTTagCompound, kind: UpdateKind.Value) = RSMode(t.getByte(name))
}
