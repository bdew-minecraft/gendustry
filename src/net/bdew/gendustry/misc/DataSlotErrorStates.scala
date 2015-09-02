/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.misc

import java.io.{DataInputStream, DataOutputStream}

import com.google.common.collect.ImmutableSet
import forestry.api.core.{IErrorLogic, IErrorState}
import net.bdew.gendustry.machines.apiary.ForestryErrorStates
import net.bdew.lib.Event
import net.bdew.lib.data.base.{DataSlotContainer, DataSlotVal, UpdateKind}
import net.minecraft.nbt.NBTTagCompound

import scala.util.DynamicVariable

case class DataSlotErrorStates(name: String, parent: DataSlotContainer) extends DataSlotVal[Set[IErrorState]] with IErrorLogic {
  override var value: Set[IErrorState] = Set.empty

  val suspendUpdates = new DynamicVariable(false)

  // ==== IErrorLogic

  override def getErrorStates: ImmutableSet[IErrorState] = ImmutableSet.copyOf(value.toArray)
  override def setCondition(condition: Boolean, errorState: IErrorState): Boolean = {
    if (condition)
      set(errorState)
    else
      clear(errorState)
    condition
  }

  override def hasErrors: Boolean = !isOk
  override def clearErrors(): Unit = clearAll()
  override def contains(state: IErrorState): Boolean = value.contains(state)

  override def writeData(data: DataOutputStream): Unit = {
    data.writeInt(value.size)
    for (x <- value)
      data.writeUTF(x.getUniqueName)
  }

  override def readData(data: DataInputStream): Unit = {
    val errNum = data.readInt()
    val errors = for (i <- 0 until errNum)
      yield Option(ForestryErrorStates.errorStates.getErrorState(data.readUTF()))
    value = errors.flatten.toSet
  }

  // ================

  def set(v: IErrorState): Unit = {
    value += v
    changed()
  }

  def clear(v: IErrorState): Unit = {
    value -= v
    changed()
  }

  def clearAll(): Unit = {
    value = Set.empty
    changed()
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
    if (value != oldValue) changed()
  }

  val onChange = Event()

  def changed(): Unit ={
    if (!suspendUpdates.value) {
      parent.dataSlotChanged(this)
      onChange.trigger()
    }
  }

  def isOk = value.isEmpty

  import net.bdew.lib.nbt._

  override def load(t: NBTTagCompound, kind: UpdateKind.Value): Unit = {
    value = (t.getList[String](name) flatMap { x => Option(ForestryErrorStates.errorStates.getErrorState(x)) }).toSet
  }

  override def save(t: NBTTagCompound, kind: UpdateKind.Value): Unit = {
    t.setList(name, value map (_.getUniqueName))
  }
}
