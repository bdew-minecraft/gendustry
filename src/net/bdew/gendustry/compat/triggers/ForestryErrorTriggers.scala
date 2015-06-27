/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.compat.triggers

import buildcraft.api.statements.StatementManager
import forestry.api.core.{ErrorStateRegistry, IErrorState}
import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.api.blocks.IForestryMultiErrorSource
import net.bdew.lib.Misc
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.common.util.ForgeDirection

trait TileEntityErrorSource extends TileEntity with IForestryMultiErrorSource

case class ForestryErrorTrigger(state: IErrorState) extends BaseTrigger("forestry.error." + state.getID, "y%03d".format(state.getID), classOf[TileEntityErrorSource]) {
  override def getIcon = state.getIcon
  override def getDescription = Misc.toLocal("for." + state.getDescription)
  override def registerIcons(ir: IIconRegister) {}
  override def getState(side: ForgeDirection, tile: TileEntityErrorSource) =
    if (state.getUniqueName == "Forestry:ok")
      tile.getErrorStates.isEmpty
    else
      tile.getErrorStates.contains(state)
}

object ForestryErrorTriggers {

  val apiaryTriggerStates = List(
    "Forestry:ok",
    "Forestry:invalidBiome",
    "Forestry:isRaining",
    "Forestry:notGloomy",
    "Forestry:notLucid",
    "Forestry:notDay",
    "Forestry:notNight",
    "Forestry:noFlower",
    "Forestry:noQueen",
    "Forestry:noDrone",
    "Forestry:noSky",
    "Forestry:noSpace",
    "Forestry:noPower",
    "Forestry:noRedstone",
    "Forestry:disabledRedstone"
  ) map ErrorStateRegistry.getErrorState

  val validTriggerStates = apiaryTriggerStates.toSet
  val validTriggers = validTriggerStates.map(x => x -> ForestryErrorTrigger(x)).toMap

  val apiaryTriggers = apiaryTriggerStates.map(validTriggers)

  def register() {
    validTriggers.values.foreach(StatementManager.registerStatement)
    Gendustry.logInfo("Created %d BC triggers for Forestry error codes", validTriggers.size)
  }
}
