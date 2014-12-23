/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.machines.imprinter

import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.gui.BlockGuiWrenchable
import net.bdew.lib.block.HasTE
import net.bdew.lib.covers.BlockCoverable
import net.bdew.lib.tile.inventory.BreakableInventoryBlock
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.util.IIcon

object BlockImprinter extends Block(Material.rock) with HasTE[TileImprinter] with BlockCoverable[TileImprinter] with BreakableInventoryBlock with BlockGuiWrenchable {
  val TEClass = classOf[TileImprinter]
  private var icons: Array[IIcon] = null
  lazy val guiId: Int = MachineImprinter.guiId

  setBlockName(Gendustry.modId + ".imprinter")
  setHardness(5)

  override def getIcon(side: Int, meta: Int): IIcon = {
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
  override def registerBlockIcons(reg: IIconRegister) {
    icons = new Array[IIcon](3)
    icons(0) = reg.registerIcon(Gendustry.modId + ":imprinter/bottom")
    icons(1) = reg.registerIcon(Gendustry.modId + ":imprinter/top")
    icons(2) = reg.registerIcon(Gendustry.modId + ":imprinter/side")
  }
}