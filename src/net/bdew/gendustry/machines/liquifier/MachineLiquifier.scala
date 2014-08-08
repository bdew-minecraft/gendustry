/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.machines.liquifier

import net.minecraft.entity.player.EntityPlayer
import cpw.mods.fml.relauncher.{SideOnly, Side}
import net.bdew.lib.machine.{Machine, ProcessorMachine}
import net.bdew.lib.gui.GuiProvider

object MachineLiquifier extends Machine("Liquifier", BlockLiquifier) with GuiProvider with ProcessorMachine {
  def guiId = 7
  type TEClass = TileLiquifier

  lazy val tankSize = tuning.getInt("TankSize")

  @SideOnly(Side.CLIENT)
  def getGui(te: TileLiquifier, player: EntityPlayer) = new GuiLiquifier(te, player)
  def getContainer(te: TileLiquifier, player: EntityPlayer) = new ContainerLiquifier(te, player)
}
