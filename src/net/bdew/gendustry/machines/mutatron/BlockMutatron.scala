/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.machines.mutatron

import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.gui.BlockGuiWrenchable
import net.bdew.gendustry.misc.BlockTooltipHelper
import net.bdew.lib.block.{BlockKeepData, BlockTooltip, HasTE}
import net.bdew.lib.covers.BlockCoverable
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.IIcon

object BlockMutatron extends Block(Material.rock) with HasTE[TileMutatron] with BlockCoverable[TileMutatron] with BlockGuiWrenchable with BlockTooltip with BlockKeepData {
  val TEClass = classOf[TileMutatron]
  private var icons: Array[IIcon] = null
  lazy val guiId: Int = MachineMutatron.guiId

  setBlockName(Gendustry.modId + ".mutatron")
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

  override def getTooltip(stack: ItemStack, player: EntityPlayer, advanced: Boolean): List[String] = {
    if (stack.hasTagCompound && stack.getTagCompound.hasKey("data")) {
      val data = stack.getTagCompound.getCompoundTag("data")
      List.empty ++
        BlockTooltipHelper.getPowerTooltip(data, "power") ++
        BlockTooltipHelper.getTankTooltip(data, "tank") ++
        BlockTooltipHelper.getItemsTooltip(data)

    } else List.empty
  }

  @SideOnly(Side.CLIENT)
  override def registerBlockIcons(reg: IIconRegister) {
    icons = new Array(3)
    icons(0) = reg.registerIcon(Gendustry.modId + ":mutatron/bottom")
    icons(1) = reg.registerIcon(Gendustry.modId + ":mutatron/top")
    icons(2) = reg.registerIcon(Gendustry.modId + ":mutatron/side")
  }
}