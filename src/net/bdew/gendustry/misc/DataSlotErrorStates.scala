/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.misc

import forestry.api.core.{ErrorStateRegistry, IErrorState}
import net.bdew.lib.data.base.{DataSlotContainer, DataSlotVal, UpdateKind}
import net.minecraft.nbt.NBTTagCompound

case class DataSlotErrorStates(name: String, parent: DataSlotContainer) extends DataSlotVal[Set[IErrorState]] {
  override var value: Set[IErrorState] = Set.empty

  def set(v: IErrorState): Unit = {
    value += v
    parent.dataSlotChanged(this)
  }

  def clear(v: IErrorState): Unit = {
    value -= v
    parent.dataSlotChanged(this)
  }

  def clearAll(): Unit = {
    value = Set.empty
    parent.dataSlotChanged(this)
  }

  def toggle(v: IErrorState, on: Boolean): Unit = {
    if (on)
      set(v)
    else
      clear(v)
  }

  def isOk = value.isEmpty

  import net.bdew.lib.nbt._

  override def load(t: NBTTagCompound, kind: UpdateKind.Value): Unit = {
    value = (t.getList[String](name) flatMap { x => Option(ErrorStateRegistry.getErrorState(x)) }).toSet
  }

  override def save(t: NBTTagCompound, kind: UpdateKind.Value): Unit = {
    t.setList(name, value map (_.getUniqueName))
  }
}
