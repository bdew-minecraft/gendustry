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
import net.bdew.lib.Misc
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

abstract class BaseTrigger[T](val id: String, ordering: String, tileClass: Class[T]) extends ITriggerExternal {
  var icon: TextureAtlasSprite = null
  override def getUniqueTag = "gendustry." + ordering + "." + id

  @SideOnly(Side.CLIENT)
  override def getGuiSprite: TextureAtlasSprite = icon

  // Fixme: figure out icon reg
  //  @SideOnly(Side.CLIENT)
  //  override def registerIcons(ir: IIconRegister) =
  //    icon = ir.registerIcon(Misc.iconName(Gendustry.modId, "trigger", id))

  override def getDescription = Misc.toLocal("gendustry.trigger." + id)
  override def rotateLeft() = this

  override def createParameter(index: Int) = null
  override def maxParameters() = 0
  override def minParameters() = 0

  override def isTriggerActive(target: TileEntity, side: EnumFacing, source: IStatementContainer, parameters: Array[IStatementParameter]) =
    if (tileClass.isInstance(target))
      getState(side, target.asInstanceOf[T])
    else
      false

  def getState(side: EnumFacing, tile: T): Boolean
}
