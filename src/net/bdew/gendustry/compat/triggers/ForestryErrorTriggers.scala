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
import forestry.api.core.EnumErrorCode
import net.bdew.gendustry.Gendustry
import net.bdew.lib.Misc
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.common.util.ForgeDirection

trait ForestryErrorSource extends TileEntity {
  def getErrorOrdinal: Int
  def getErrorState: EnumErrorCode
}

case class ForestryErrorTrigger(state: EnumErrorCode) extends BaseTrigger("forestry.error." + state.ordinal(), "y%03d".format(state.ordinal()), classOf[ForestryErrorSource]) {
  override def getIcon = state.getIcon
  override def getDescription = Misc.toLocal("for." + state.getDescription)
  override def registerIcons(ir: IIconRegister) {}
  override def getState(side: ForgeDirection, tile: ForestryErrorSource) =
    tile.getErrorState == state
}

object ForestryErrorTriggers {

  val apiaryTriggerStates = Seq(
    EnumErrorCode.OK,
    EnumErrorCode.INVALIDBIOME,
    EnumErrorCode.NOTGLOOMY,
    EnumErrorCode.NOTLUCID,
    EnumErrorCode.NOTDAY,
    EnumErrorCode.NOTNIGHT,
    EnumErrorCode.NOFLOWER,
    EnumErrorCode.NOQUEEN,
    EnumErrorCode.NODRONE,
    EnumErrorCode.NOSKY,
    EnumErrorCode.NOSPACE,
    EnumErrorCode.NOPOWER
  )

  val validTriggerStates = apiaryTriggerStates.toSet
  val validTriggers = validTriggerStates.map(x => x -> ForestryErrorTrigger(x)).toMap

  val apiaryTriggers = apiaryTriggerStates.map(validTriggers)

  def register() {
    validTriggers.values.foreach(StatementManager.registerStatement)
    Gendustry.logInfo("Created %d BC triggers for Forestry error codes", validTriggers.size)
  }
}
