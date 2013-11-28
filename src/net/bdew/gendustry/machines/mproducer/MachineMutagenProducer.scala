/*
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.machines.mproducer

import net.bdew.gendustry.machines.ProcessorMachine
import net.minecraft.tileentity.TileEntity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraftforge.common.Configuration
import net.bdew.gendustry.gui.GuiProvider
import cpw.mods.fml.relauncher.{SideOnly, Side}

class MachineMutagenProducer(cfg: Configuration) extends ProcessorMachine(cfg, "MutagenProducer") with GuiProvider {
  var block: BlockMutagenProducer = null
  lazy val guiId = 1

  lazy val tankSize = tuning.getInt("TankSize")

  if (cfg.get("Machines Enabled", name, true).getBoolean(true)) {
    block = new BlockMutagenProducer(getBlockId)
    registerBlock(block)
  }

  @SideOnly(Side.CLIENT)
  def getGui(te: TileEntity, player: EntityPlayer): GuiContainer = new GuiMutagenProducer(te.asInstanceOf[TileMutagenProducer], player)
  def getContainer(te: TileEntity, player: EntityPlayer): Container = new ContainerMutagenProducer(te.asInstanceOf[TileMutagenProducer], player)
}
