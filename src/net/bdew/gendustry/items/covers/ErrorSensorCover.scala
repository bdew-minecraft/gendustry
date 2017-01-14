/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.items.covers

import forestry.api.core.{ForestryAPI, IErrorLogicSource, IErrorState}
import net.bdew.gendustry.Gendustry
import net.bdew.lib.Misc
import net.bdew.lib.covers.{ItemCover, TileCoverable}
import net.bdew.lib.helpers.ChatHelper._
import net.bdew.lib.items.BaseItem
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.entity.player.{EntityPlayer, EntityPlayerMP}
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.{EnumFacing, ResourceLocation}
import net.minecraftforge.client.model.ModelLoader

trait ErrorSensor {
  def id: String
  def getUnLocalizedName: String
  def isActive(te: IErrorLogicSource): Boolean
}

object ErrorSensorNoError extends ErrorSensor {
  override def id: String = "noError"
  override def isActive(te: IErrorLogicSource): Boolean = !te.getErrorLogic.hasErrors
  override def getUnLocalizedName: String = "gendustry.cover.error.none"
}

object ErrorSensorAnyError extends ErrorSensor {
  override def id: String = "anyError"
  override def isActive(te: IErrorLogicSource): Boolean = te.getErrorLogic.hasErrors
  override def getUnLocalizedName: String = "gendustry.cover.error.any"
}

case class ErrorSensorForestry(state: IErrorState) extends ErrorSensor {
  override def id: String = state.getUniqueName
  override def isActive(te: IErrorLogicSource): Boolean = te.getErrorLogic.contains(state)
  override def getUnLocalizedName: String = state.getUnlocalizedDescription
}

object ErrorSensors {
  val errorStateRegistry = ForestryAPI.errorStateRegistry

  val sensors = List(ErrorSensorAnyError, ErrorSensorNoError) ++ (List(
    "forestry:too_hot",
    "forestry:too_cold",
    "forestry:too_arid",
    "forestry:too_humid",
    "forestry:is_raining",
    "forestry:not_gloomy",
    "forestry:not_lucid",
    "forestry:not_day",
    "forestry:not_night",
    "forestry:no_flower",
    "forestry:no_queen",
    "forestry:no_drone",
    "forestry:no_sky",
    "forestry:no_space",
    "forestry:no_power"
  ) map (name => ErrorSensorForestry(errorStateRegistry.getErrorState(name))))

  val idMap = sensors.map(x => x.id -> x).toMap
}

object ErrorSensorCover extends BaseItem("error_sensor_cover") with ItemCover {
  override def isCoverTicking: Boolean = false

  override def isValidTile(te: TileCoverable, side: EnumFacing, stack: ItemStack) = te.isInstanceOf[IErrorLogicSource]

  def getErrorSensor(stack: ItemStack): Option[ErrorSensor] = {
    if (stack.hasTagCompound) {
      ErrorSensors.idMap.get(stack.getTagCompound.getString("sensor"))
    } else None
  }

  def setErrorSensor(stack: ItemStack, sensor: ErrorSensor): ItemStack = {
    val copy = stack.copy()
    if (!copy.hasTagCompound) copy.setTagCompound(new NBTTagCompound)
    copy.getTagCompound.setString("sensor", sensor.id)
    copy
  }

  def clearErrorSensor(stack: ItemStack): ItemStack = {
    val copy = stack.copy()
    if (copy.hasTagCompound) copy.setTagCompound(null)
    copy
  }

  override def isEmittingSignal(te: TileCoverable, side: EnumFacing, cover: ItemStack): Boolean = {
    for {
      tile <- Misc.asInstanceOpt(te, classOf[IErrorLogicSource])
      sensor <- getErrorSensor(cover)
    } {
      if (sensor.isActive(tile)) return true
    }
    false
  }

  override def onInstall(te: TileCoverable, side: EnumFacing, cover: ItemStack, player: EntityPlayerMP): ItemStack = {
    val sensor = ErrorSensors.sensors.head
    player.sendMessage(L("gendustry.cover.error.message", L(sensor.getUnLocalizedName).setColor(Color.YELLOW)))
    player.sendMessage(L("gendustry.cover.error.hint"))
    setErrorSensor(cover, sensor)
  }

  override def onRemove(cover: ItemStack): ItemStack =
    clearErrorSensor(cover)

  override def clickCover(te: TileCoverable, side: EnumFacing, cover: ItemStack, player: EntityPlayer): Boolean = {
    if (!player.inventory.getCurrentItem.isEmpty) return false
    if (!player.world.isRemote) {
      val current = getErrorSensor(cover) getOrElse ErrorSensors.sensors.head
      val next = Misc.nextInSeq(ErrorSensors.sensors, current)
      te.covers(side) := Some(setErrorSensor(cover, next))
      te.onCoversChanged()
      player.sendMessage(L("gendustry.cover.error.message", L(next.getUnLocalizedName).setColor(Color.YELLOW)))
      te.getWorldObject.neighborChanged(te.getPos.offset(side), te.getBlockType, te.getPos)
    }
    true
  }

  override def getDisplayItem(te: TileCoverable, side: EnumFacing, cover: ItemStack): ItemStack = {
    if (isEmittingSignal(te, side, cover))
      new ItemStack(this, 1, 1)
    else
      cover
  }

  override def registerItemModels(): Unit = {
    super.registerItemModels()
    ModelLoader.setCustomModelResourceLocation(this, 1, new ModelResourceLocation(new ResourceLocation(Gendustry.modId, "error_sensor_cover_on"), "inventory"))
  }
}
