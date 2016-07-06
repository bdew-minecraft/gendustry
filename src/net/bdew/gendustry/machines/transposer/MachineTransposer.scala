/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.machines.transposer

import net.bdew.lib.gui.GuiProvider
import net.bdew.lib.machine.{Machine, ProcessorMachine}
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

object MachineTransposer extends Machine("Transposer", BlockTransposer) with GuiProvider with ProcessorMachine {
  def guiId = 9
  type TEClass = TileTransposer

  lazy val labwareConsumeChance = tuning.getInt("LabwareConsumeChance")

  @SideOnly(Side.CLIENT)
  def getGui(te: TileTransposer, player: EntityPlayer): GuiContainer = new GuiTransposer(te, player)
  def getContainer(te: TileTransposer, player: EntityPlayer): Container = new ContainerTransposer(te, player)
}
