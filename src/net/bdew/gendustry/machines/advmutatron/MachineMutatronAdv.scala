/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.machines.advmutatron

import net.minecraft.entity.player.EntityPlayer
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.lib.machine.{Machine, ProcessorMachine}
import net.bdew.lib.gui.GuiProvider

class MachineMutatronAdv extends Machine("MutatronAdv", new BlockMutatronAdv) with GuiProvider with ProcessorMachine {
  def guiId = 6
  type TEClass = TileMutatronAdv

  lazy val tankSize = tuning.getInt("TankSize")
  lazy val mutagenPerItem = tuning.getInt("MutagenPerItem")
  lazy val labwareConsumeChance = tuning.getFloat("LabwareConsumeChance")
  lazy val degradeChanceNatural = tuning.getFloat("DegradeChanceNatural")
  lazy val deathChanceArtificial = tuning.getFloat("DeathChanceArtificial")

  @SideOnly(Side.CLIENT)
  def getGui(te: TileMutatronAdv, player: EntityPlayer) = new GuiMutatronAdv(te, player)
  def getContainer(te: TileMutatronAdv, player: EntityPlayer) = new ContainerMutatronAdv(te, player)
}
