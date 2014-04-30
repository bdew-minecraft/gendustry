/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.nei

import net.bdew.lib.gui.SimpleDrawTarget
import net.minecraft.client.Minecraft
import codechicken.lib.gui.GuiDraw

object NEIDrawTarget extends SimpleDrawTarget {
  def getZLevel = GuiDraw.gui.getZLevel
  def getFontRenderer = Minecraft.getMinecraft.fontRenderer
}
