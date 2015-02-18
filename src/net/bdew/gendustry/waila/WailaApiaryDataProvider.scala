/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.waila

import mcp.mobius.waila.api.{IWailaConfigHandler, IWailaDataAccessor}
import net.bdew.gendustry.machines.apiary.TileApiary
import net.bdew.lib.Misc
import net.minecraft.item.ItemStack

object WailaApiaryDataProvider extends BaseDataProvider(classOf[TileApiary]) {
  override def getBodyStrings(target: TileApiary, stack: ItemStack, acc: IWailaDataAccessor, cfg: IWailaConfigHandler) = {
    List(
      Misc.toLocalF("gendustry.label.status", Misc.toLocal("for." + target.getErrorState.getDescription)),
      Misc.toLocalF("gendustry.label.control", Misc.toLocal("gendustry.rsmode." + target.rsmode.value.toString.toLowerCase))
    ) ++ (Option(target.queen) map (_.getDisplayName)) ++ (
      if (acc.getPlayer.isSneaking)
        target.getStats
      else
        Some(Misc.toLocal("gendustry.label.shift"))
      )
  }
}
