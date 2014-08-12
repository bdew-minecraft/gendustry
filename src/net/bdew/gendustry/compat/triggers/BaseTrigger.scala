/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.compat.triggers

import buildcraft.api.gates.{ITrigger, ITriggerParameter}
import net.bdew.lib.Misc
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.IIcon
import net.minecraftforge.common.util.ForgeDirection

abstract class BaseTrigger[T <: TileEntity](val id: String, ordering: String, tileClass: Class[T]) extends ITrigger {
  var icon: IIcon = null
  def getLegacyId = -1
  def getUniqueTag = "gendustry." + ordering + "." + id
  def getIcon = icon
  def registerIcons(ir: IIconRegister) =
    icon = ir.registerIcon("gendustry:trigger/" + id)
  def hasParameter = false
  def requiresParameter = false
  def getDescription = Misc.toLocal("gendustry.trigger." + id)
  def createParameter() = null
  def rotateLeft() = this
  def isTriggerActive(side: ForgeDirection, tile: TileEntity, parameter: ITriggerParameter) =
    if (tileClass.isInstance(tile))
      getState(side, tile.asInstanceOf[T])
    else
      false

  def getState(side: ForgeDirection, tile: T): Boolean
}
