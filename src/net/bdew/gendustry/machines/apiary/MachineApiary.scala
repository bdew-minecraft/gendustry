/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.machines.apiary

import net.minecraft.entity.player.EntityPlayer
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.lib.machine.{Machine, PoweredMachine}
import net.bdew.lib.gui.GuiProvider

class MachineApiary extends Machine("IndustrialApiary", new BlockApiary(_)) with GuiProvider with PoweredMachine {
  def guiId = 3
  type TEClass = TileApiary

  lazy val baseMjPerTick = tuning.getInt("BaseMjPerTick")

  @SideOnly(Side.CLIENT)
  def getGui(te: TileApiary, player: EntityPlayer) = new GuiApiary(te, player, new ContainerApiary(te, player))
  def getContainer(te: TileApiary, player: EntityPlayer) = new ContainerApiary(te, player)
}
