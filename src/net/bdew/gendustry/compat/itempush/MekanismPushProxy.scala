/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.compat.itempush

import mekanism.api.{Coord4D, EnumColor}
import net.bdew.gendustry.Gendustry
import net.bdew.lib.Misc
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.common.util.ForgeDirection

import scala.language.reflectiveCalls

object MekanismPushProxy extends ItemPushProxy {
  type ILogisticalTransporter = {
    def insert(original: Coord4D, itemStack: ItemStack, color: EnumColor, doEmit: Boolean, min: Int): ItemStack
    def canReceiveFrom(tileEntity: TileEntity, side: ForgeDirection): Boolean
  }

  type ITransporterTile = TileEntity {
    def getTransmitter: ILogisticalTransporter
  }

  val iTransporterTileCls =
    try {
      Class.forName("mekanism.common.base.ITransporterTile").asInstanceOf[Class[ITransporterTile]]
    } catch {
      case t: Throwable =>
        Gendustry.logWarnException("Failed to load mekanism logistical transporter class", t)
        null
    }

  val iLogisticalTransporterCls =
    try {
      Class.forName("mekanism.common.base.ILogisticalTransporter").asInstanceOf[Class[ILogisticalTransporter]]
    } catch {
      case t: Throwable =>
        Gendustry.logWarnException("Failed to load mekanism logistical transporter class", t)
        null
    }

  val useable = iTransporterTileCls != null && iLogisticalTransporterCls != null

  def getTransporter(from: TileEntity, dir: ForgeDirection) = {
    for {
      tile <- Misc.getNeighbourTile(from, dir, iTransporterTileCls)
      pipe <- Option(tile.getTransmitter)
    } yield pipe
  }

  override def pushStack(from: TileEntity, dir: ForgeDirection, stack: ItemStack): ItemStack = {
    if (useable) {
      for (pipe <- getTransporter(from, dir)) {
        return pipe.insert(Coord4D.get(from), stack, null, true, 0)
      }
    }
    return stack
  }
}
