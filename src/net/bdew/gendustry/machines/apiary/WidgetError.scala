/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.machines.apiary

import forestry.api.core.ForestryAPI
import net.bdew.gendustry.gui.Textures
import net.bdew.lib.gui.widgets.Widget
import net.bdew.lib.gui.{Point, Rect, Texture}
import net.bdew.lib.{Client, Misc}
import net.minecraft.util.text.TextFormatting

import scala.collection.mutable

class WidgetError(x: Int, y: Int, apiary: TileApiary) extends Widget {
  val rect: Rect = new Rect(x, y, 16, 16)

  def getDisplayedError = {
    import scala.collection.JavaConversions._
    val errors = apiary.getErrorStates
    val pos = ((Client.world.getTotalWorldTime / 40) % errors.size()).toInt
    errors.toList.sortBy(_.getID).apply(pos)
  }

  override def draw(mouse: Point) {
    if (apiary.errorConditions.isOk)
      parent.drawTexture(rect, Textures.errors.ok)
    else
      parent.drawTexture(rect, Texture(ForestryAPI.textureManager.getGuiTextureMap, getDisplayedError.getSprite))
  }

  override def handleTooltip(p: Point, tip: mutable.MutableList[String]) {
    tip ++= apiary.errorConditions.toList.sortBy(_.getID) map { err =>
      TextFormatting.RED + Misc.toLocal(err.getUnlocalizedDescription)
    }
    tip ++= apiary.getStats
  }
}
