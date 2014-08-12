/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.waila

import mcp.mobius.waila.api.{IWailaConfigHandler, IWailaDataAccessor}
import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.machines.apiary.{ErrorCodes, TileApiary}
import net.bdew.lib.Misc
import net.minecraft.item.ItemStack

object WailaApiaryDataProvider extends BaseDataProvider(classOf[TileApiary]) {
  override def getBodyStrings(target: TileApiary, stack: ItemStack, acc: IWailaDataAccessor, cfg: IWailaConfigHandler) = {
    val err = target.errorState.cval
    val state = if (err == -1) {
      Misc.toLocal(Gendustry.modId + ".error.power")
    } else if (err == -2) {
      Misc.toLocal(Gendustry.modId + ".error.disabled")
    } else if (ErrorCodes.isValid(err)) {
      ErrorCodes.getDescription(err)
    } else {
      ErrorCodes.getDescription(0)
    }

    List(
      Misc.toLocalF("gendustry.label.status", state),
      Misc.toLocalF("gendustry.label.control", Misc.toLocal("gendustry.rsmode." + target.rsmode.cval.toString.toLowerCase))
    ) ++ target.getStats
  }
}
