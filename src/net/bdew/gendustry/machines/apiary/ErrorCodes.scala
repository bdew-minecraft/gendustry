/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.machines.apiary

import cpw.mods.fml.relauncher.{Side, SideOnly}
import forestry.api.core.{ErrorStateRegistry, IErrorState}
import net.bdew.lib.Misc
import net.bdew.lib.gui.Texture
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.util.IIcon

object ErrorCodes {
  val UNKNOWN = ErrorStateRegistry.getErrorState("Forestry:unknown")

  def getErrorById(i: Int) = Option(ErrorStateRegistry.getErrorState(i.toShort)).getOrElse(UNKNOWN)
  def getErrorByName(n: String) = Option(ErrorStateRegistry.getErrorState(n)).getOrElse(UNKNOWN)

  def isValid(i: Int) = ErrorStateRegistry.getErrorState(i.toShort) != null
  def getIcon(i: Int) = Texture(Texture.ITEMS, getErrorById(i).getIcon)
  def getDescription(i: Int) = Misc.toLocal("for." + getErrorById(i).getDescription)
  def getHelp(i: Int) = Misc.toLocal("for." + getErrorById(i).getHelp)

  def init() {
    ErrorStateRegistry.registerErrorState(GendustryErrorStates.Disabled)
  }
}

case class GendustryErrorState(name: String, id: Short) extends IErrorState {
  var icon: IIcon = null
  override def getID = id
  override def getUniqueName = "gendustry:" + name
  override def getDescription = "gendustry.errorstate." + name + ".description"
  override def getHelp = "gendustry.errorstate." + name + ".help"

  @SideOnly(Side.CLIENT)
  override def getIcon = icon

  @SideOnly(Side.CLIENT)
  override def registerIcons(register: IIconRegister) {
    icon = register.registerIcon("gendustry:error/" + name)
  }
}

object GendustryErrorStates {
  val Disabled = GendustryErrorState("disabled", 500)
}