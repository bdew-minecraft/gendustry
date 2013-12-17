/*
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.machines.mutatron

import net.bdew.gendustry.machines.ProcessorMachine
import net.minecraft.tileentity.TileEntity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraftforge.common.Configuration
import net.bdew.gendustry.gui.GuiProvider
import cpw.mods.fml.relauncher.{Side, SideOnly}

class MachineMutatron(cfg: Configuration) extends ProcessorMachine(cfg, "Mutatron") with GuiProvider {
  var block: BlockMutatron = null
  lazy val guiId = 2

  lazy val tankSize = tuning.getInt("TankSize")
  lazy val mutagenPerItem = tuning.getInt("MutagenPerItem")
  lazy val labwareConsumeChance = tuning.getFloat("LabwareConsumeChance")
  lazy val degradeChanceNatural = tuning.getFloat("DegradeChanceNatural")
  lazy val deathChanceArtificial = tuning.getFloat("DeathChanceArtificial")
  lazy val secretChance = tuning.getFloat("SecretMutationChance")

  if (cfg.get("Machines Enabled", name, true).getBoolean(true)) {
    block = new BlockMutatron(getBlockId)
    registerBlock(block)
  }

  @SideOnly(Side.CLIENT)
  def getGui(te: TileEntity, player: EntityPlayer): GuiContainer = new GuiMutatron(te.asInstanceOf[TileMutatron], player)
  def getContainer(te: TileEntity, player: EntityPlayer): Container = new ContainerMutatron(te.asInstanceOf[TileMutatron], player)
}
