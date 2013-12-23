/*
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.machines.apiary

import net.bdew.gendustry.Gendustry
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import net.bdew.lib.gui.{Rect, BaseScreen}
import net.bdew.lib.gui.widgets.WidgetLabel
import net.bdew.gendustry.gui.Textures
import net.bdew.lib.Misc
import net.bdew.lib.power.WidgetPowerGauge

class GuiApiary(val te: TileApiary, player: EntityPlayer) extends BaseScreen(new ContainerApiary(te, player), 176, 166) {
  val texture = new ResourceLocation(Gendustry.modId + ":textures/gui/apiary.png")
  override def initGui() {
    super.initGui()
    addWidget(new WidgetError(155, 5, te))
    addWidget(new WidgetPowerGauge(new Rect(8, 19, 16, 58), Textures.powerFill, te.power))
    addWidget(new WidgetApiaryProgress(new Rect(69, 22, 36, 15), te.guiBreeding, te.guiProgress))
    addWidget(new WidgetLabel(Misc.toLocal("tile.gendustry.apiary.name"), 8, 6, 4210752))
  }
}