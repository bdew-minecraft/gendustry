/*
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.machines.mproducer

import net.bdew.gendustry.Gendustry
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import net.bdew.lib.gui.{Rect, BaseScreen}
import net.bdew.lib.gui.widgets.{WidgetLabel, WidgetFluidGauge}
import net.bdew.lib.Misc
import net.bdew.gendustry.gui.{WidgetPowerCustom, WidgetProgressBarNEI, Textures}

class GuiMutagenProducer(val te: TileMutagenProducer, player: EntityPlayer) extends BaseScreen(new ContainerMutagenProducer(te, player), 176, 166) {
  val texture = new ResourceLocation(Gendustry.modId + ":textures/gui/mutagenproducer.png")
  override def initGui() {
    super.initGui()
    addWidget(new WidgetProgressBarNEI(new Rect(79, 41, 53, 15), Textures.greenProgress(53), te.progress, "MutagenProducer"))
    addWidget(new WidgetPowerCustom(new Rect(8, 19, 16, 58), Textures.powerFill, te.power))
    addWidget(new WidgetFluidGauge(new Rect(152, 19, 16, 58), Textures.tankOverlay, te.tank))
    addWidget(new WidgetLabel(Misc.toLocal("tile.gendustry.mutagen.producer.name"), 8, 6, 4210752))
  }
}