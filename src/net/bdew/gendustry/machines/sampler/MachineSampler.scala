/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.machines.sampler

import net.bdew.lib.gui.GuiProvider
import net.bdew.lib.machine.{Machine, ProcessorMachine}
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

object MachineSampler extends Machine("Sampler", BlockSampler) with GuiProvider with ProcessorMachine {
  def guiId = 5
  type TEClass = TileSampler

  lazy val labwareConsumeChance = tuning.getInt("LabwareConsumeChance")

  @SideOnly(Side.CLIENT)
  def getGui(te: TileSampler, player: EntityPlayer): GuiContainer = new GuiSampler(te, player)
  def getContainer(te: TileSampler, player: EntityPlayer): Container = new ContainerSampler(te, player)
}
