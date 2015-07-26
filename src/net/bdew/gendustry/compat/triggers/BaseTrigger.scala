/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.compat.triggers

import buildcraft.api.statements.{IStatementContainer, IStatementParameter, ITriggerExternal}
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.lib.Misc
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.IIcon
import net.minecraftforge.common.util.ForgeDirection

abstract class BaseTrigger[T](val id: String, ordering: String, tileClass: Class[T]) extends ITriggerExternal {
  var icon: IIcon = null
  override def getUniqueTag = "gendustry." + ordering + "." + id

  @SideOnly(Side.CLIENT)
  override def getIcon = icon

  @SideOnly(Side.CLIENT)
  override def registerIcons(ir: IIconRegister) =
    icon = ir.registerIcon("gendustry:trigger/" + id)

  override def getDescription = Misc.toLocal("gendustry.trigger." + id)
  override def rotateLeft() = this

  override def createParameter(index: Int) = null
  override def maxParameters() = 0
  override def minParameters() = 0

  override def isTriggerActive(target: TileEntity, side: ForgeDirection, source: IStatementContainer, parameters: Array[IStatementParameter]) =
    if (tileClass.isInstance(target))
      getState(side, target.asInstanceOf[T])
    else
      false

  def getState(side: ForgeDirection, tile: T): Boolean
}
