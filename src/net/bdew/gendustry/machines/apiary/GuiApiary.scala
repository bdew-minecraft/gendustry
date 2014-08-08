/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.machines.apiary

import net.bdew.gendustry.Gendustry
import net.minecraft.entity.player.EntityPlayer
import net.bdew.lib.gui.{Texture, Color, Rect, BaseScreen, Point}
import net.bdew.lib.gui.widgets.WidgetLabel
import net.bdew.gendustry.gui.{HintIcons, WidgetPowerCustom, Textures}
import net.bdew.lib.Misc
import net.bdew.gendustry.gui.rscontrol.WidgetRSModeButton

class GuiApiary(val te: TileApiary, player: EntityPlayer, cont: ContainerApiary) extends BaseScreen(cont, 176, 166) {
  val background = Texture(Gendustry.modId, "textures/gui/apiary.png", rect)
  override def initGui() {
    super.initGui()
    widgets.add(new WidgetError(155, 5, te))
    widgets.add(new WidgetPowerCustom(new Rect(8, 19, 16, 58), Textures.powerFill, te.power))
    widgets.add(new WidgetApiaryProgress(new Rect(69, 22, 36, 15), te.guiBreeding, te.guiProgress))
    widgets.add(new WidgetLabel(Misc.toLocal("tile.gendustry.apiary.name"), 8, 6, Color.darkgray))
    widgets.add(new WidgetRSModeButton(Point(137, 5), te, cont))

    te.slots.upgrades.foreach(inventorySlots.getSlot(_).setBackgroundIcon(HintIcons.upgrade))
    inventorySlots.getSlotFromInventory(te, te.slots.drone).setBackgroundIcon(HintIcons.drone)
    inventorySlots.getSlotFromInventory(te, te.slots.queen).setBackgroundIcon(HintIcons.queen)
  }
}