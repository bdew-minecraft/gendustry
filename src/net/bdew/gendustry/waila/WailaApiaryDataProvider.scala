/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.waila

import java.util.Locale

import mcp.mobius.waila.api.{IWailaConfigHandler, IWailaDataAccessor}
import net.bdew.gendustry.items.covers.ErrorSensorCover
import net.bdew.gendustry.machines.apiary.TileApiary
import net.bdew.lib.Misc
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumChatFormatting

object WailaApiaryDataProvider extends BaseDataProvider(classOf[TileApiary]) {
  override def getBodyStrings(target: TileApiary, stack: ItemStack, acc: IWailaDataAccessor, cfg: IWailaConfigHandler) = {
    var strings = target.errorConditions.toList.sortBy(_.getID) map { err =>
      EnumChatFormatting.RED + Misc.toLocal("for." + err.getDescription)
    }
    strings :+= Misc.toLocalF("gendustry.label.control", Misc.toLocal("gendustry.rsmode." + target.rsmode.value.toString.toLowerCase(Locale.US)))

    strings ++= target.queen.map(_.getDisplayName)

    target.covers(acc.getSide).value foreach { cover =>
      if (cover.getItem == ErrorSensorCover) {
        ErrorSensorCover.getErrorSensor(cover) foreach { sensor =>
          strings :+= "%s (%s%s%s)".format(
            Misc.toLocalF("gendustry.cover.label", cover.getDisplayName),
            EnumChatFormatting.YELLOW,
            Misc.toLocal(sensor.getUnLocalizedName),
            EnumChatFormatting.RESET
          )
        }
      } else {
        strings :+= Misc.toLocalF("gendustry.cover.label", cover.getDisplayName)
      }
    }

    if (acc.getPlayer.isSneaking)
      strings ++= target.getStats
    else
      strings :+= Misc.toLocal("gendustry.label.shift")

    strings
  }
}
