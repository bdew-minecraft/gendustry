/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.machines.extractor

import net.bdew.gendustry.Gendustry
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IconRegister
import net.minecraft.util.Icon
import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import net.bdew.lib.block.HasTE
import net.bdew.lib.tile.inventory.BreakableInventoryBlock
import net.bdew.gendustry.config.Machines
import net.bdew.gendustry.gui.BlockGuiWrenchable

class BlockExtractor(id: Int) extends Block(id, Material.rock) with HasTE[TileExtractor] with BreakableInventoryBlock with BlockGuiWrenchable {
  private var icons: Array[Icon] = null
  val TEClass = classOf[TileExtractor]
  lazy val guiId = Machines.extractor.guiId

  setUnlocalizedName(Gendustry.modId + ".extractor")
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
    icons(0) = reg.registerIcon(Gendustry.modId + ":extractor/bottom")
    icons(1) = reg.registerIcon(Gendustry.modId + ":extractor/top")
    icons(2) = reg.registerIcon(Gendustry.modId + ":extractor/side")
  }
}