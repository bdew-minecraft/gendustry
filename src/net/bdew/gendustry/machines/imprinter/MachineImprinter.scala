/*
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.machines.imprinter

import net.bdew.gendustry.machines.ProcessorMachine
import net.minecraft.tileentity.TileEntity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraftforge.common.Configuration
import net.bdew.gendustry.gui.GuiProvider
import cpw.mods.fml.relauncher.{Side, SideOnly}

class MachineImprinter(cfg: Configuration) extends ProcessorMachine(cfg, "Imprinter") with GuiProvider {
  var block: BlockImprinter = null
  lazy val guiId = 4

  lazy val labwareConsumeChance = tuning.getInt("LabwareConsumeChance")
  lazy val deathChanceNatural = tuning.getInt("DeathChanceNatural")
  lazy val deathChanceArtificial = tuning.getInt("DeathChanceArtificial")

  if (cfg.get("Machines Enabled", name, true).getBoolean(true)) {
    block = new BlockImprinter(getBlockId)
    registerBlock(block)
  }

  @SideOnly(Side.CLIENT)
  def getGui(te: TileEntity, player: EntityPlayer): GuiContainer = new GuiImprinter(te.asInstanceOf[TileImprinter], player)
  def getContainer(te: TileEntity, player: EntityPlayer): Container = new ContainerImprinter(te.asInstanceOf[TileImprinter], player)
}
