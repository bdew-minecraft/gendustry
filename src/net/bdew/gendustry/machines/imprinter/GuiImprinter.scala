/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.machines.imprinter

import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.gui.{HintIcons, WidgetPowerCustom, WidgetProgressBarNEI, Textures}
import net.minecraft.entity.player.EntityPlayer
import net.bdew.lib.gui.{Color, Texture, Rect, BaseScreen}
import net.bdew.lib.gui.widgets.WidgetLabel
import net.bdew.lib.Misc

class GuiImprinter(val te: TileImprinter, player: EntityPlayer) extends BaseScreen(new ContainerImprinter(te, player), 176, 166) {
  val background = Texture(Gendustry.modId, "textures/gui/imprinter.png", rect)

  override def initGui() {
    super.initGui()
    widgets.add(new WidgetProgressBarNEI(new Rect(63, 49, 66, 15), Textures.whiteProgress(66), te.progress, "Imprinter"))
    widgets.add(new WidgetPowerCustom(new Rect(8, 19, 16, 58), Textures.powerFill, te.power))
    widgets.add(new WidgetLabel(Misc.toLocal("tile.gendustry.imprinter.name"), 8, 6, Color.darkgray))

    inventorySlots.getSlot(te.slots.inIndividual).setBackgroundIcon(HintIcons.queenOrSapling)
    inventorySlots.getSlot(te.slots.inLabware).setBackgroundIcon(HintIcons.labware)
    inventorySlots.getSlot(te.slots.inTemplate).setBackgroundIcon(HintIcons.template)
  }
}