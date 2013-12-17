/*
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.machines.mutatron

import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.gui.{WidgetProgressBarNEI, WidgetMJGauge, Textures}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import net.bdew.lib.gui.{Rect, BaseScreen}
import net.bdew.lib.gui.widgets.{WidgetLabel, WidgetFluidGauge}
import net.bdew.lib.Misc

class GuiMutatron(val te: TileMutatron, player: EntityPlayer) extends BaseScreen(new ContainerMutatron(te, player), 176, 166) {
  val texture: ResourceLocation = new ResourceLocation(Gendustry.modId + ":textures/gui/mutatron.png")
  override def initGui() {
    super.initGui()
    addWidget(new WidgetProgressBarNEI(new Rect(89, 41, 40, 15), Textures.greenProgress(40), te.progress, "Mutatron"))
    addWidget(new WidgetMJGauge(new Rect(8, 19, 16, 58), Textures.powerFill, te.power))
    addWidget(new WidgetFluidGauge(new Rect(32, 19, 16, 58), Textures.tankOverlay, te.tank))
    addWidget(new WidgetLabel(Misc.toLocal("tile.gendustry.mutatron.name"), 8, 6, 4210752))
  }
}