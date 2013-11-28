/*
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.gui

import net.minecraft.world.World
import net.minecraft.entity.player.EntityPlayer
import buildcraft.api.tools.IToolWrench
import net.bdew.gendustry.Gendustry
import net.minecraft.block.Block

trait BlockGuiWrenchable extends Block {
  val guiId: Int
  override def onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, meta: Int, xoffs: Float, yoffs: Float, zoffs: Float): Boolean = {
    if (player.isSneaking) {
      val equipped = if (player.getCurrentEquippedItem != null) player.getCurrentEquippedItem.getItem else null
      if (equipped.isInstanceOf[IToolWrench] && equipped.asInstanceOf[IToolWrench].canWrench(player, x, y, z)) {
        if (!world.isRemote) world.destroyBlock(x, y, z, true)
        return true
      }
      return false
    } else {
      if (!world.isRemote) player.openGui(Gendustry.instance, guiId, world, x, y, z)
      return true
    }
  }
}
