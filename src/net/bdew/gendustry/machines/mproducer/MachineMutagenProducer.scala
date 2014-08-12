/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.machines.mproducer

import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.lib.gui.GuiProvider
import net.bdew.lib.machine.{Machine, ProcessorMachine}
import net.minecraft.entity.player.EntityPlayer

object MachineMutagenProducer extends Machine("MutagenProducer", BlockMutagenProducer) with GuiProvider with ProcessorMachine {
  def guiId = 1
  type TEClass = TileMutagenProducer

  lazy val tankSize = tuning.getInt("TankSize")

  @SideOnly(Side.CLIENT)
  def getGui(te: TileMutagenProducer, player: EntityPlayer) = new GuiMutagenProducer(te, player)
  def getContainer(te: TileMutagenProducer, player: EntityPlayer) = new ContainerMutagenProducer(te, player)
}
