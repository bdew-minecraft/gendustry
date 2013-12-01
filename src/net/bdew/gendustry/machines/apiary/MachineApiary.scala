/*
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.machines.apiary

import net.bdew.gendustry.machines.PoweredMachine
import net.minecraft.tileentity.TileEntity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraftforge.common.Configuration
import net.bdew.gendustry.gui.GuiProvider
import cpw.mods.fml.relauncher.{Side, SideOnly}

class MachineApiary(cfg: Configuration) extends PoweredMachine(cfg, "IndustrialApiary") with GuiProvider {
  var block: BlockApiary = null
  lazy val guiId = 3

  lazy val baseMjPerTick = tuning.getInt("BaseMjPerTick")

  if (cfg.get("Machines Enabled", name, true).getBoolean(true)) {
    block = new BlockApiary(getBlockId)
    registerBlock(block)
  }

  @SideOnly(Side.CLIENT)
  def getGui(te: TileEntity, player: EntityPlayer): GuiContainer = new GuiApiary(te.asInstanceOf[TileApiary], player)
  def getContainer(te: TileEntity, player: EntityPlayer): Container = new ContainerApiary(te.asInstanceOf[TileApiary], player)
}
