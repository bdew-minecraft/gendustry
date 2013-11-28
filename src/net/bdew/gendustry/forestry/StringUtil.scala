/*
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.forestry

object StringUtil {
  val cl = Class.forName("forestry.core.utils.StringUtil")
  val mLocalize = cl.getMethod("localize", classOf[String])

  def localize(s: String) = mLocalize.invoke(null, s).asInstanceOf[String]
}
