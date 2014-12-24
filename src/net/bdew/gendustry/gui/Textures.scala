/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.gui

import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.gui.rscontrol.RSMode
import net.bdew.lib.gui._

object Textures {
  val texture = new ScaledResourceLocation(Gendustry.modId, "textures/gui/widgets.png", 256)
  val tankOverlay = Texture(texture, 16, 0, 16, 58)
  val powerFill = Texture(texture, 0, 0, 16, 58)
  val slotSelect = Texture(texture, 64, 0, 18, 18)
  def greenProgress(width: Float) = Texture(texture, 136 - width, 58, width, 15)
  def whiteProgress(width: Float) = Texture(texture, 136 - width, 73, width, 15)

  object errors {
    val noPower = Texture(texture, 32, 0, 16, 16)
    val disabled = Texture(texture, 48, 0, 16, 16)
  }

  object button16 {
    val base = Texture(texture, 32, 18, 16, 16)
    val hover = Texture(texture, 48, 18, 16, 16)

    val iconRSMode = Map(
      RSMode.ALWAYS -> Texture(texture, 65, 35, 14, 14),
      RSMode.NEVER -> Texture(texture, 81, 35, 14, 14),
      RSMode.RS_ON -> Texture(texture, 49, 35, 14, 14),
      RSMode.RS_OFF -> Texture(texture, 33, 35, 14, 14)
    )
  }

}
