/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.gui.rscontrol

import net.bdew.lib.gui.{Texture, Rect, Point}
import net.bdew.lib.gui.widgets.Widget
import net.bdew.gendustry.gui.Textures
import scala.collection.mutable
import net.bdew.lib.{Misc, Client}
import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.util.ResourceLocation

class WidgetRSModeButton(p: Point, te: TileRSContollable, container: ContainerRSControllable) extends Widget {
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
    tip += Misc.toLocal("gendustry.rsmode." + te.rsmode.cval.toString.toLowerCase)
  }

  override def mouseClicked(p: Point, button: Int) {
    Client.minecraft.getSoundHandler.playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F))
    Client.minecraft.playerController.windowClick(container.windowId, container.RSMODE_SLOT_NUM, RSMode.next(te.rsmode).id, 0, Client.player)
  }
}
