/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.gui

import net.bdew.gendustry.nei.NEIRecipeProxy
import net.bdew.lib.Misc
import net.bdew.lib.data.DataSlotFloat
import net.bdew.lib.gui.widgets.WidgetProgressBar
import net.bdew.lib.gui.{Point, Rect, Texture}

import scala.collection.mutable

class WidgetProgressBarNEI(rect: Rect, texture: Texture, dslot: DataSlotFloat, recipeid: String) extends WidgetProgressBar(rect, texture, dslot) {
  override def mouseClicked(p: Point, button: Int) {
    if (NEIRecipeProxy.hasNei)
      NEIRecipeProxy.openRecipes(recipeid)
  }

  override def handleTooltip(p: Point, tip: mutable.MutableList[String]) {
    super.handleTooltip(p, tip)
    if (NEIRecipeProxy.hasNei) tip += Misc.toLocal("gendustry.label.recipes")
  }
}
