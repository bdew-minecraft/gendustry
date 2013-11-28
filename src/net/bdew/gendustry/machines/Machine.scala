/*
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.machines

import net.minecraft.tileentity.TileEntity
import cpw.mods.fml.common.registry.GameRegistry
import net.bdew.gendustry.Gendustry
import net.minecraft.block.Block
import net.minecraftforge.common.Configuration
import net.bdew.lib.block.HasTE
import net.bdew.gendustry.config.{Tuning, Ids}
import net.minecraft.item.ItemStack

abstract class Machine(cfg: Configuration, val name: String) {
  lazy val tuning = Tuning.getSection("Machines").getSection(name)

  def registerBlock(block: Block) {
    GameRegistry.registerBlock(block, name)
    GameRegistry.registerCustomItemStack(name, new ItemStack(block))
    if (block.isInstanceOf[HasTE[_]]) {
      registerTE(block.asInstanceOf[HasTE[_]].TEClass)
    }
  }

  def registerTE(teClass: Class[_ <: TileEntity]) {
    GameRegistry.registerTileEntity(teClass, "%s.%s".format(Gendustry.modId, name))
  }

  def getBlockId = cfg.getBlock(name, Ids.blockIds.next()).getInt
}
