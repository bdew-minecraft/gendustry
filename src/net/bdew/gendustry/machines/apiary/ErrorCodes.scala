/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.machines.apiary

import net.bdew.lib.Misc
import net.bdew.lib.gui.Texture
import net.minecraft.util.IIcon

object ErrorCodes {

  import scala.language.existentials

  private val cEnumErrorCode = Class.forName("forestry.core.EnumErrorCode")
  private val mGetIcon = cEnumErrorCode.getMethod("getIcon")
  private val mGetDescription = cEnumErrorCode.getMethod("getDescription")
  private val mGetHelp = cEnumErrorCode.getMethod("getHelp")

  val values = cEnumErrorCode.getEnumConstants

  def isValid(i: Int) = values.isDefinedAt(i)

  def getIcon(i: Int) = Texture(Texture.ITEMS, mGetIcon.invoke(values(i)).asInstanceOf[IIcon])
  def getDescription(i: Int) = Misc.toLocal("for." + mGetDescription.invoke(values(i)).asInstanceOf[String])
  def getHelp(i: Int) = Misc.toLocal("for." + mGetHelp.invoke(values(i)).asInstanceOf[String])
}
