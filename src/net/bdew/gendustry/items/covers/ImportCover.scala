/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.items.covers

import net.bdew.lib.capabilities.Capabilities
import net.bdew.lib.capabilities.helpers.ItemHelper
import net.bdew.lib.covers.{ItemCover, TileCoverable}
import net.bdew.lib.items.BaseItem
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing

object ImportCover extends BaseItem("import_cover") with ItemCover {
  override def isCoverTicking: Boolean = true

  override def isValidTile(te: TileCoverable, side: EnumFacing, stack: ItemStack) =
    te.hasCapability(Capabilities.CAP_ITEM_HANDLER, side)

  override def tickCover(te: TileCoverable, side: EnumFacing, coverStack: ItemStack): Unit = {
    if (te.getWorldObject.getTotalWorldTime % 20 == 0) {
      for (source <- ItemHelper.getItemHandler(te.getWorldObject, te.getPos.offset(side), side.getOpposite))
        ItemHelper.pushItems(source, te.getCapability(Capabilities.CAP_ITEM_HANDLER, side), maxItems = 10)
    }
  }
}
