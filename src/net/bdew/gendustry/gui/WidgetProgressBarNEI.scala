/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.gui

import net.bdew.lib.data.DataSlotFloat
import net.bdew.lib.gui.widgets.WidgetProgressBar
import net.bdew.lib.gui.{Point, Rect, Texture}

import scala.collection.mutable

class WidgetProgressBarNEI(rect: Rect, texture: Texture, dSlot: DataSlotFloat, recipeId: String) extends WidgetProgressBar(rect, texture, dSlot) {
  override def mouseClicked(p: Point, button: Int) {
    //    if (NEIRecipeProxy.hasNei)
    //      NEIRecipeProxy.openRecipes(recipeId)
  }

  override def handleTooltip(p: Point, tip: mutable.MutableList[String]) {
    super.handleTooltip(p, tip)
    //    if (NEIRecipeProxy.hasNei) tip += Misc.toLocal("gendustry.label.recipes")
  }
}
