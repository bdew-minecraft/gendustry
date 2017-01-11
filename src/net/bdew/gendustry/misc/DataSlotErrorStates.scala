/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.misc

import com.google.common.collect.ImmutableSet
import forestry.api.core.{IErrorLogic, IErrorState}
import net.bdew.gendustry.machines.apiary.ForestryErrorStates
import net.bdew.lib.Event
import net.bdew.lib.PimpVanilla._
import net.bdew.lib.data.base.{DataSlotContainer, DataSlotVal, UpdateKind}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.PacketBuffer

import scala.util.DynamicVariable

case class DataSlotErrorStates(name: String, parent: DataSlotContainer) extends DataSlotVal[Set[IErrorState]] with IErrorLogic {
  override def default: Set[IErrorState] = Set.empty

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

  override def writeData(data: PacketBuffer): Unit = {
    data.writeInt(value.size)
    for (x <- value)
      data.writeString(x.getUniqueName)
  }

  override def readData(data: PacketBuffer): Unit = {
    val errNum = data.readInt()
    val errors = for (i <- 0 until errNum)
      yield Option(ForestryErrorStates.errorStates.getErrorState(data.readString(128)))
    update(errors.flatten.toSet)
  }

  // ================

  def set(v: IErrorState): Unit = {
    require(v != null)
    update(value + v, !suspendUpdates.value)
  }

  def clear(v: IErrorState): Unit = {
    require(v != null)
    update(value - v, !suspendUpdates.value)
  }

  def clearAll(): Unit = update(Set.empty, !suspendUpdates.value)

  def toggle(v: IErrorState, on: Boolean): Unit = {
    require(v != null)
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
      changed()
  }

  val onChange = Event()

  override def changed(): Unit = {
    super.changed()
    onChange.trigger()
  }

  def isOk = value.isEmpty

  override def loadValue(t: NBTTagCompound, kind: UpdateKind.Value): Set[IErrorState] =
    (t.getList[String](name) flatMap { x => Option(ForestryErrorStates.errorStates.getErrorState(x)) }).toSet

  override def save(t: NBTTagCompound, kind: UpdateKind.Value): Unit = {
    t.setList(name, value map (_.getUniqueName))
  }
}
