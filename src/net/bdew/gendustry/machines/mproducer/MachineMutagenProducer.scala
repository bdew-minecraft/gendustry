/*
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.machines.mproducer

import net.minecraft.tileentity.TileEntity
import net.minecraft.entity.player.EntityPlayer
import net.bdew.gendustry.gui.GuiProvider
import cpw.mods.fml.relauncher.{SideOnly, Side}
import net.bdew.lib.machine.{Machine, ProcessorMachine}

class MachineMutagenProducer extends Machine("MutagenProducer", new BlockMutagenProducer(_)) with GuiProvider with ProcessorMachine {
  def guiId = 1

  lazy val tankSize = tuning.getInt("TankSize")

  @SideOnly(Side.CLIENT)
  def getGui(te: TileEntity, player: EntityPlayer) = new GuiMutagenProducer(te.asInstanceOf[TileMutagenProducer], player)
  def getContainer(te: TileEntity, player: EntityPlayer) = new ContainerMutagenProducer(te.asInstanceOf[TileMutagenProducer], player)
}
