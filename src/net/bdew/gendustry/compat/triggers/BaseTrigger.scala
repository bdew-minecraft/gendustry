/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.compat.triggers

import buildcraft.api.gates.{ITileTrigger, ITriggerParameter}
import net.bdew.lib.Misc
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.IIcon
import net.minecraftforge.common.util.ForgeDirection

abstract class BaseTrigger[T <: TileEntity](val id: String, ordering: String, tileClass: Class[T]) extends ITileTrigger {
  var icon: IIcon = null
  override def getUniqueTag = "gendustry." + ordering + "." + id
  override def getIcon = icon
  override def registerIcons(ir: IIconRegister) =
    icon = ir.registerIcon("gendustry:trigger/" + id)
  override def hasParameter = false
  override def requiresParameter = false
  override def getDescription = Misc.toLocal("gendustry.trigger." + id)
  override def createParameter() = null
  override def rotateLeft() = this
  override def isTriggerActive(side: ForgeDirection, tile: TileEntity, parameter: ITriggerParameter) =
    if (tileClass.isInstance(tile))
      getState(side, tile.asInstanceOf[T])
    else
      false

  def getState(side: ForgeDirection, tile: T): Boolean
}
