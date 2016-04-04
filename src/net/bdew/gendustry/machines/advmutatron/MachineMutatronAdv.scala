/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.machines.advmutatron

import net.bdew.lib.gui.GuiProvider
import net.bdew.lib.machine.{Machine, ProcessorMachine}
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

object MachineMutatronAdv extends Machine("MutatronAdv", BlockMutatronAdv) with GuiProvider with ProcessorMachine {
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
