/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.gui

import net.bdew.lib.gui._
import net.bdew.gendustry.Gendustry

object Textures {
  val texture = new ScaledResourceLocation(Gendustry.modId, "textures/gui/widgets.png", 256)
  val tankOverlay = Texture(texture, 16, 0, 16, 58)
  val powerFill = Texture(texture, 0, 0, 16, 58)
  val texturePowerError = Texture(texture, 32, 0, 16, 16)
  val slotSelect = Texture(texture, 48, 0, 18, 18)
  def greenProgress(width: Float) = Texture(texture, 136 - width, 58, width, 15)
  def whiteProgress(width: Float) = Texture(texture, 136 - width, 73, width, 15)
}
