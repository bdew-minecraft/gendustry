/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.machines.advmutatron

import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.gui.{HintIcons, Textures, WidgetPowerCustom, WidgetProgressBarNEI}
import net.bdew.lib.Misc
import net.bdew.lib.gui._
import net.bdew.lib.gui.widgets.{WidgetFluidGauge, WidgetLabel}
import net.minecraft.entity.player.EntityPlayer

class GuiMutatronAdv(val te: TileMutatronAdv, player: EntityPlayer) extends BaseScreen(new ContainerMutatronAdv(te, player), 176, 188) {
  val background = Texture(Gendustry.modId, "textures/gui/mutatron_adv.png", rect)

  override def initGui() {
    super.initGui()
    widgets.add(new WidgetProgressBarNEI(new Rect(89, 41, 40, 15), Textures.greenProgress(40), te.progress, "Mutatron"))
    widgets.add(new WidgetPowerCustom(new Rect(8, 19, 16, 58), Textures.powerFill, te.power))
    widgets.add(new WidgetFluidGauge(new Rect(32, 19, 16, 58), Textures.tankOverlay, te.tank))
    widgets.add(new WidgetSelector(new Point(7, 84), te.selectedMutation, -1))
    widgets.add(new WidgetLabel(Misc.toLocal("tile.gendustry.mutatron_adv.name"), 8, 6, Color.darkGray))
    widgets.add(new WidgetLabel(Misc.toLocal("gendustry.label.select"), 8, 89, Color.darkGray))

    inventorySlots.getSlotFromInventory(te, te.slots.inIndividual1).setBackgroundLocation(HintIcons.queenOrSapling)
    inventorySlots.getSlotFromInventory(te, te.slots.inIndividual2).setBackgroundLocation(HintIcons.droneOrPollen)
    inventorySlots.getSlotFromInventory(te, te.slots.inLabware).setBackgroundLocation(HintIcons.labware)
  }
}