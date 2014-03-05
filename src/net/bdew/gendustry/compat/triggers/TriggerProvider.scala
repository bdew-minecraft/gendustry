/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.compat.triggers

import buildcraft.api.gates.{ActionManager, ITrigger, ITriggerProvider}
import net.minecraft.block.Block
import net.minecraft.tileentity.TileEntity
import buildcraft.api.transport.IPipe
import net.bdew.gendustry.machines.apiary.TileApiary
import java.util
import net.bdew.gendustry.power.TilePowered
import net.bdew.lib.power.TileBaseProcessor

object TriggerProvider extends ITriggerProvider {
  def getPipeTriggers(pipe: IPipe) = null

  def getNeighborTriggers(block: Block, tile: TileEntity) = {
    import scala.collection.JavaConversions._
    val triggers = new util.LinkedList[ITrigger]()

    if (tile.isInstanceOf[TileApiary])
      triggers.addAll(ForestryErrorTriggers.apiaryTriggers)

    if (tile.isInstanceOf[TilePowered])
      triggers.addAll(PowerTriggers.triggers)

    if (tile.isInstanceOf[TileBaseProcessor])
      triggers.add(TriggerWorking)

    triggers
  }

  def registerTriggers() {
    ActionManager.registerTriggerProvider(this)
    ActionManager.registerTrigger(TriggerWorking)
    ForestryErrorTriggers.register()
    PowerTriggers.register()
  }
}
