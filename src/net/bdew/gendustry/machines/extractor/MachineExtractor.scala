/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.machines.extractor

import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.lib.gui.GuiProvider
import net.bdew.lib.machine.{Machine, ProcessorMachine}
import net.minecraft.entity.player.EntityPlayer

object MachineExtractor extends Machine("Extractor", BlockExtractor) with GuiProvider with ProcessorMachine {
  def guiId = 8
  type TEClass = TileExtractor

  lazy val tankSize = tuning.getInt("TankSize")
  lazy val labwareConsumeChance = tuning.getInt("LabwareConsumeChance")

  @SideOnly(Side.CLIENT)
  def getGui(te: TileExtractor, player: EntityPlayer) = new GuiExtractor(te, player)
  def getContainer(te: TileExtractor, player: EntityPlayer) = new ContainerExtractor(te, player)
}
