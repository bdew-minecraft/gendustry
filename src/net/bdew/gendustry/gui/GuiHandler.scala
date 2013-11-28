/*
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.gui

import cpw.mods.fml.common.network.IGuiHandler
import scala.collection.mutable
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World

object GuiHandler extends IGuiHandler {
  val guis = mutable.Map.empty[Int, GuiProvider]

  def register(p: GuiProvider) = {
    if (guis.isDefinedAt(p.guiId))
      sys.error("GUI ID Collision - %d was registered for %s and attemted to register by %s".format(p.guiId, guis(p.guiId), p))
    guis += (p.guiId -> p)
  }

  def getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef =
    guis(ID).getContainer(world.getBlockTileEntity(x, y, z), player)

  def getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef =
    guis(ID).getGui(world.getBlockTileEntity(x, y, z), player)
}
