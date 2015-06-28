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

import scala.util.DynamicVariable

case class DataSlotErrorStates(name: String, parent: DataSlotContainer) extends DataSlotVal[Set[IErrorState]] {
  override var value: Set[IErrorState] = Set.empty

  val suspendUpdates = new DynamicVariable(false)

  def set(v: IErrorState): Unit = {
    value += v
    if (!suspendUpdates.value)
      parent.dataSlotChanged(this)
  }

  def clear(v: IErrorState): Unit = {
    value -= v
    if (!suspendUpdates.value)
      parent.dataSlotChanged(this)
  }

  def clearAll(): Unit = {
    value = Set.empty
    if (!suspendUpdates.value)
      parent.dataSlotChanged(this)
  }

  def toggle(v: IErrorState, on: Boolean): Unit = {
    if (on)
      set(v)
    else
      clear(v)
  }

  def withSuspendedUpdates(f: => Unit) = {
    val oldValue = value
    suspendUpdates.withValue(true) {
      f
    }
    if (value != oldValue)
      parent.dataSlotChanged(this)
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
