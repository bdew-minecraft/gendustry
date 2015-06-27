/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.machines.apiary

import net.bdew.lib.gui.widgets.Widget
import net.bdew.lib.gui.{Point, Rect, Texture}
import net.bdew.lib.{Client, Misc}
import net.minecraft.util.EnumChatFormatting

import scala.collection.mutable

class WidgetError(x: Int, y: Int, apiary: TileApiary) extends Widget {
  val rect: Rect = new Rect(x, y, 16, 16)

  def getDisplayedError = {
    import scala.collection.JavaConversions._
    val errors = apiary.getErrorStates
    if (errors.isEmpty) {
      ForestryErrorStates.ok
    } else {
      val pos = ((Client.world.getTotalWorldTime / 40) % errors.size()).toInt
      errors.toList.sortBy(_.getID).apply(pos)
    }
  }

  override def draw(mouse: Point) {
    parent.drawTexture(rect, Texture(Texture.ITEMS, getDisplayedError.getIcon))
  }

  override def handleTooltip(p: Point, tip: mutable.MutableList[String]) {
    tip ++= apiary.errorConditions.toList.sortBy(_.getID) map { err =>
      EnumChatFormatting.RED + Misc.toLocal("for." + err.getDescription)
    }
    tip ++= apiary.getStats
  }
}
