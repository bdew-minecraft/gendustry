/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.compat.triggers

import java.util

import buildcraft.api.statements._
import forestry.api.core.IErrorLogicSource
import net.bdew.gendustry.power.TilePowered
import net.bdew.lib.power.TileBaseProcessor
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing

object TriggerProvider extends ITriggerProvider {
  override def getInternalTriggers(container: IStatementContainer) = null

  override def getExternalTriggers(side: EnumFacing, tile: TileEntity) = {
    import scala.collection.JavaConversions._
    val triggers = new util.LinkedList[ITriggerExternal]()

    if (tile.isInstanceOf[IErrorLogicSource])
      triggers.addAll(ForestryErrorTriggers.triggers)

    if (tile.isInstanceOf[TilePowered])
      triggers.addAll(PowerTriggers.triggers)

    if (tile.isInstanceOf[TileBaseProcessor])
      triggers.add(TriggerWorking)

    triggers
  }

  def registerTriggers() {
    StatementManager.registerTriggerProvider(this)
    StatementManager.registerStatement(TriggerWorking)
    ForestryErrorTriggers.register()
    PowerTriggers.register()
  }
}
