/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.machines.imprinter

import net.minecraft.entity.player.EntityPlayer
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.lib.machine.{Machine, ProcessorMachine}
import net.bdew.lib.gui.GuiProvider

class MachineImprinter extends Machine("Imprinter", new BlockImprinter(_)) with GuiProvider with ProcessorMachine {
  def guiId = 4
  type TEClass = TileImprinter

  lazy val labwareConsumeChance = tuning.getInt("LabwareConsumeChance")
  lazy val deathChanceNatural = tuning.getInt("DeathChanceNatural")
  lazy val deathChanceArtificial = tuning.getInt("DeathChanceArtificial")

  @SideOnly(Side.CLIENT)
  def getGui(te: TileImprinter, player: EntityPlayer) = new GuiImprinter(te, player)
  def getContainer(te: TileImprinter, player: EntityPlayer) = new ContainerImprinter(te, player)
}
