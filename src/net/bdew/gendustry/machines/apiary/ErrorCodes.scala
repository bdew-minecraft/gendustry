/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.machines.apiary

import forestry.api.core.{ErrorStateRegistry, IErrorState}
import net.bdew.lib.Misc
import net.bdew.lib.gui.Texture

object ErrorCodes {
  private val cEnumErrorCode = Class.forName("forestry.core.EnumErrorCode").asInstanceOf[Class[IErrorState]]
  private val fName = cEnumErrorCode.getDeclaredField("name")
  fName.setAccessible(true)

  val map = cEnumErrorCode.getEnumConstants.map(c => fName.get(c).asInstanceOf[String] -> c).toMap

  val UNKNOWN = ErrorStateRegistry.getErrorStateFromCode(0)

  def getValueSafe(i: Int) = Option(ErrorStateRegistry.getErrorStateFromCode(i.toShort)).getOrElse(UNKNOWN)
  def getErrorByName(n: String) = map.getOrElse(n, UNKNOWN)

  def isValid(i: Int) = ErrorStateRegistry.getErrorStateFromCode(i.toShort) != null
  def getIcon(i: Int) = Texture(Texture.ITEMS, getValueSafe(i).getIcon)
  def getDescription(i: Int) = Misc.toLocal("for." + getValueSafe(i).getDescription)
  def getHelp(i: Int) = Misc.toLocal("for." + getValueSafe(i).getHelp)
}
