/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.machines.replicator

import net.bdew.gendustry.gui.BlockGuiWrenchable
import net.bdew.gendustry.machines.BaseMachineBlock
import net.bdew.gendustry.misc.BlockTooltipHelper
import net.bdew.lib.block.{BlockKeepData, BlockTooltip, HasTE}
import net.bdew.lib.covers.BlockCoverable
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemStack
import net.minecraft.world.World

object BlockReplicator extends BaseMachineBlock("replicator") with HasTE[TileReplicator] with BlockCoverable with BlockGuiWrenchable with BlockTooltip with BlockKeepData {
  val TEClass = classOf[TileReplicator]
  lazy val guiId: Int = MachineReplicator.guiId

  override def getTooltip(stack: ItemStack, world: World, flags: ITooltipFlag): List[String] = {
    if (stack.hasTagCompound && stack.getTagCompound.hasKey("data")) {
      val data = stack.getTagCompound.getCompoundTag("data")
      List.empty ++
        BlockTooltipHelper.getPowerTooltip(data, "power") ++
        BlockTooltipHelper.getTankTooltip(data, "dnaTank") ++
        BlockTooltipHelper.getTankTooltip(data, "proteinTank") ++
        BlockTooltipHelper.getItemsTooltip(data)
    } else List.empty
  }
}
