/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.machines.replicator

import net.bdew.lib.gui.GuiProvider
import net.bdew.lib.machine.{Machine, ProcessorMachine}
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

object MachineReplicator extends Machine("Replicator", BlockReplicator) with GuiProvider with ProcessorMachine {
  def guiId = 10
  type TEClass = TileReplicator

  lazy val dnaTankSize = tuning.getInt("DNATankSize")
  lazy val proteinTankSize = tuning.getInt("ProteinTankSize")
  lazy val dnaPerItem = tuning.getInt("DNAPerItem")
  lazy val proteinPerItem = tuning.getInt("ProteinPerItem")

  lazy val makePristineBees = tuning.getBoolean("MakePristineBees")

  @SideOnly(Side.CLIENT)
  def getGui(te: TileReplicator, player: EntityPlayer) = new GuiReplicator(te, player)
  def getContainer(te: TileReplicator, player: EntityPlayer) = new ContainerReplicator(te, player)
}
