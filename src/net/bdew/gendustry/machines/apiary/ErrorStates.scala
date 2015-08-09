/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.machines.apiary

import cpw.mods.fml.relauncher.{Side, SideOnly}
import forestry.api.core.{ErrorStateRegistry, IErrorState}
import net.bdew.gendustry.Gendustry
import net.bdew.lib.Misc
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.util.IIcon

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
    icon = register.registerIcon(Misc.iconName(Gendustry.modId, "error", name))
  }
}

object GendustryErrorStates {
  val Disabled = GendustryErrorState("disabled", 500)
  def init() {
    ErrorStateRegistry.registerErrorState(GendustryErrorStates.Disabled)
  }
}

object ForestryErrorStates {
  val ok = ErrorStateRegistry.getErrorState("Forestry:ok")
  val noPower = ErrorStateRegistry.getErrorState("Forestry:noPower")
  val noRedstone = ErrorStateRegistry.getErrorState("Forestry:noRedstone")
  val disabledRedstone = ErrorStateRegistry.getErrorState("Forestry:disabledRedstone")
}