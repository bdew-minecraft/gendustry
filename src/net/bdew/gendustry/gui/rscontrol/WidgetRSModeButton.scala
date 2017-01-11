/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.gui.rscontrol

import java.util.Locale

import net.bdew.gendustry.gui.Textures
import net.bdew.lib.gui.widgets.Widget
import net.bdew.lib.gui.{Point, Rect, Texture}
import net.bdew.lib.{Client, Misc}
import net.minecraft.client.Minecraft
import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.init.SoundEvents
import net.minecraft.inventory.ClickType

import scala.collection.mutable

class WidgetRSModeButton(p: Point, te: TileRSControllable, container: ContainerRSControllable) extends Widget {
  val rect = new Rect(p, 16, 16)
  val iconRect = new Rect(p +(1, 1), 14, 14)

  var icon: Texture = null
  var hover: String = null

  override def draw(mouse: Point) {
    if (rect.contains(mouse))
      parent.drawTexture(rect, Textures.button16.hover)
    else
      parent.drawTexture(rect, Textures.button16.base)

    parent.drawTexture(iconRect, Textures.button16.iconRSMode(te.rsmode))
  }

  override def handleTooltip(p: Point, tip: mutable.MutableList[String]) {
    tip += Misc.toLocal("gendustry.rsmode." + te.rsmode.value.toString.toLowerCase(Locale.US))
  }

  override def mouseClicked(p: Point, button: Int) {
    Minecraft.getMinecraft.getSoundHandler.playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F))
    Client.minecraft.playerController.windowClick(container.windowId, container.RSMODE_SLOT_NUM, RSMode.next(te.rsmode).id, ClickType.PICKUP, Client.player)
  }
}
