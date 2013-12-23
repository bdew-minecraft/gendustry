/*
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.machines.sampler

import net.minecraft.tileentity.TileEntity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.client.gui.inventory.GuiContainer
import net.bdew.gendustry.gui.GuiProvider
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.lib.machine.{Machine, ProcessorMachine}

class MachineSampler extends Machine("Sampler", new BlockSampler(_)) with GuiProvider with ProcessorMachine {
  def guiId = 5

  lazy val labwareConsumeChance = tuning.getInt("LabwareConsumeChance")

  @SideOnly(Side.CLIENT)
  def getGui(te: TileEntity, player: EntityPlayer): GuiContainer = new GuiSampler(te.asInstanceOf[TileSampler], player)
  def getContainer(te: TileEntity, player: EntityPlayer): Container = new ContainerSampler(te.asInstanceOf[TileSampler], player)
}
