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
import forestry.api.core.{ForestryAPI, IErrorState}
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
    icon = register.registerIcon("gendustry:error/" + name)
  }
}

object GendustryErrorStates {
  val Disabled = GendustryErrorState("disabled", 500)
  def init() {
    ForestryAPI.errorStateRegistry.registerErrorState(GendustryErrorStates.Disabled)
  }
}

object ForestryErrorStates {
  val errorStates = ForestryAPI.errorStateRegistry
  val ok = errorStates.getErrorState("Forestry:ok")
  val noPower = errorStates.getErrorState("Forestry:noPower")
  val noRedstone = errorStates.getErrorState("Forestry:noRedstone")
  val disabledRedstone = errorStates.getErrorState("Forestry:disabledRedstone")
}