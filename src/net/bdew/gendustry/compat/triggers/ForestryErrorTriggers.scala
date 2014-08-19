/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.compat.triggers

import buildcraft.api.gates.ActionManager
import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.machines.apiary.{ErrorCodes, TileApiary}
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.common.util.ForgeDirection

trait ForestryErrorSource extends TileEntity {
  def getErrorOrdinal: Int
}

case class ForestryErrorTrigger(code: Int) extends BaseTrigger("forestry.error." + code, "y%03d".format(code), classOf[ForestryErrorSource]) {
  override def getIcon = ErrorCodes.getIcon(code).icon
  override def getDescription = ErrorCodes.getDescription(code)
  override def registerIcons(ir: IIconRegister) {}
  override def getState(side: ForgeDirection, tile: ForestryErrorSource) =
    tile.asInstanceOf[TileApiary].getErrorOrdinal == code
}

object ForestryErrorTriggers {
  val apiaryTriggerCodes = Seq(1, 2, 9, 10, 11, 12, 13, 14, 15, 16, 17)

  val validTriggerCodes = apiaryTriggerCodes.toSet
  val validTriggers = validTriggerCodes.map(x => x -> ForestryErrorTrigger(x)).toMap

  val apiaryTriggers = apiaryTriggerCodes.map(validTriggers)

  def register() {
    validTriggers.values.foreach(ActionManager.registerTrigger(_))
    Gendustry.logInfo("Created %d BC triggers for Forestry error codes", validTriggers.size)
  }
}
