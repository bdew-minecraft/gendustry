/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.machines.mutatron

import net.minecraft.entity.player.EntityPlayer
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.lib.machine.{Machine, ProcessorMachine}
import net.bdew.lib.gui.GuiProvider

class MachineMutatron extends Machine("Mutatron", new BlockMutatron) with GuiProvider with ProcessorMachine {
  def guiId = 2
  type TEClass = TileMutatron

  lazy val tankSize = tuning.getInt("TankSize")
  lazy val mutagenPerItem = tuning.getInt("MutagenPerItem")
  lazy val labwareConsumeChance = tuning.getFloat("LabwareConsumeChance")
  lazy val degradeChanceNatural = tuning.getFloat("DegradeChanceNatural")
  lazy val deathChanceArtificial = tuning.getFloat("DeathChanceArtificial")
  lazy val secretChance = tuning.getFloat("SecretMutationChance")

  @SideOnly(Side.CLIENT)
  def getGui(te: TileMutatron, player: EntityPlayer) = new GuiMutatron(te, player)
  def getContainer(te: TileMutatron, player: EntityPlayer) = new ContainerMutatron(te, player)
}
