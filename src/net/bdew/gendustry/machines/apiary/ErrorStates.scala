/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.machines.apiary

import forestry.api.core.{ForestryAPI, IErrorState}
import net.bdew.gendustry.Gendustry
import net.bdew.lib.{Client, Misc}
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

case class GendustryErrorState(name: String, id: Short) extends IErrorState {
  var icon: TextureAtlasSprite = null

  override def getID = id
  override def getUniqueName = "gendustry:" + name
  override def getUnlocalizedDescription: String = "gendustry.errorstate." + name + ".description"
  override def getUnlocalizedHelp: String = "gendustry.errorstate." + name + ".help"

  @SideOnly(Side.CLIENT)
  override def getSprite: TextureAtlasSprite = icon

  @SideOnly(Side.CLIENT)
  override def registerSprite(): Unit = {
    icon = Client.textureMapBlocks.registerSprite(Misc.iconName(Gendustry.modId, "error", name))
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
  val noPower = errorStates.getErrorState("forestry:noPower")
  val noRedstone = errorStates.getErrorState("forestry:noRedstone")
  val disabledRedstone = errorStates.getErrorState("forestry:disabledRedstone")
}