/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.machines.replicator

import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import net.bdew.gendustry.Gendustry
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IconRegister
import net.minecraft.util.Icon
import net.bdew.lib.block.HasTE
import net.bdew.lib.tile.inventory.BreakableInventoryBlock
import net.bdew.gendustry.config.Machines
import net.bdew.gendustry.gui.BlockGuiWrenchable
import net.bdew.lib.covers.BlockCoverable

class BlockReplicator(id: Int) extends Block(id, Material.rock) with HasTE[TileReplicator] with BlockCoverable[TileReplicator] with BreakableInventoryBlock with BlockGuiWrenchable {
  val TEClass = classOf[TileReplicator]
  private var icons: Array[Icon] = null
  lazy val guiId: Int = Machines.replicator.guiId

  setUnlocalizedName(Gendustry.modId + ".replicator")
  setHardness(5)

  override def getIcon(side: Int, meta: Int): Icon = {
    side match {
      case 0 =>
        return icons(0)
      case 1 =>
        return icons(1)
      case _ =>
        return icons(2)
    }
  }

  @SideOnly(Side.CLIENT)
  override def registerIcons(reg: IconRegister) {
    icons = new Array[Icon](3)
    icons(0) = reg.registerIcon(Gendustry.modId + ":replicator/bottom")
    icons(1) = reg.registerIcon(Gendustry.modId + ":replicator/top")
    icons(2) = reg.registerIcon(Gendustry.modId + ":replicator/side")
  }
}