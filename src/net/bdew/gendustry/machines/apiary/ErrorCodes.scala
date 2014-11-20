/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.machines.apiary

import forestry.api.core.EnumErrorCode
import net.bdew.lib.Misc
import net.bdew.lib.gui.Texture

object ErrorCodes {
  val values = EnumErrorCode.values().zipWithIndex.map(_.swap).toMap
  def getValueSafe(i: Int) = values.getOrElse(i, EnumErrorCode.UNKNOWN)
  def isValid(i: Int) = values.isDefinedAt(i)
  def getIcon(i: Int) = Texture(Texture.ITEMS, values(i).getIcon)
  def getDescription(i: Int) = Misc.toLocal("for." + values(i).getDescription)
  def getHelp(i: Int) = Misc.toLocal("for." + values(i).getHelp)
}
