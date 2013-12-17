/*
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.machines.sampler

import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.gui.{WidgetProgressBarNEI, WidgetMJGauge, Textures}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import net.bdew.lib.gui.{Rect, BaseScreen}
import net.bdew.lib.gui.widgets.WidgetLabel
import net.bdew.lib.Misc

class GuiSampler(val te: TileSampler, player: EntityPlayer) extends BaseScreen(new ContainerSampler(te, player), 176, 166) {
  val texture: ResourceLocation = new ResourceLocation(Gendustry.modId + ":textures/gui/sampler.png")
  override def initGui() {
    super.initGui()
    addWidget(new WidgetProgressBarNEI(new Rect(63, 49, 66, 15), Textures.whiteProgress(66), te.progress, "Sampler"))
    addWidget(new WidgetMJGauge(new Rect(8, 19, 16, 58), Textures.powerFill, te.power))
    addWidget(new WidgetLabel(Misc.toLocal("tile.gendustry.sampler.name"), 8, 6, 4210752))
  }
}