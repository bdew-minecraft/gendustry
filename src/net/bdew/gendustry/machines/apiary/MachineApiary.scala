/*
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.machines.apiary

import net.minecraft.tileentity.TileEntity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.client.gui.inventory.GuiContainer
import net.bdew.gendustry.gui.GuiProvider
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.lib.machine.{Machine, PoweredMachine}

class MachineApiary extends Machine("IndustrialApiary", new BlockApiary(_)) with GuiProvider with PoweredMachine {
  def guiId = 3

  lazy val baseMjPerTick = tuning.getInt("BaseMjPerTick")

  @SideOnly(Side.CLIENT)
  def getGui(te: TileEntity, player: EntityPlayer): GuiContainer = new GuiApiary(te.asInstanceOf[TileApiary], player)
  def getContainer(te: TileEntity, player: EntityPlayer): Container = new ContainerApiary(te.asInstanceOf[TileApiary], player)
}
