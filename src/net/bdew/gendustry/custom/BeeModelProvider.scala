/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.custom

import java.util.Locale

import forestry.api.apiculture.{EnumBeeType, IBeeModelProvider}
import forestry.api.core.{ForestryAPI, IModelManager}
import forestry.api.genetics.AlleleManager
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

object BeeModelProvider extends IBeeModelProvider {
  lazy val beeRoot = AlleleManager.alleleRegistry.getSpeciesRoot("rootBees")

  @SideOnly(Side.CLIENT)
  private var models: Array[ModelResourceLocation] = null

  def registerModelsManual(): Unit = {
    registerModels(Item.REGISTRY.getObject(new ResourceLocation("forestry", "beeDroneGE")), ForestryAPI.modelManager)
    registerModels(Item.REGISTRY.getObject(new ResourceLocation("forestry", "beeLarvaeGE")), ForestryAPI.modelManager)
    registerModels(Item.REGISTRY.getObject(new ResourceLocation("forestry", "beePrincessGE")), ForestryAPI.modelManager)
    registerModels(Item.REGISTRY.getObject(new ResourceLocation("forestry", "beeQueenGE")), ForestryAPI.modelManager)
  }

  @SideOnly(Side.CLIENT)
  override def registerModels(item: Item, manager: IModelManager) {
    val beeIconDir = "bees/default/"
    AlleleManager.alleleRegistry.getSpeciesRoot()
    val beeType = beeRoot.getType(new ItemStack(item)).asInstanceOf[EnumBeeType]
    val beeTypeNameBase = beeIconDir + beeType.toString.toLowerCase(Locale.ENGLISH)
    if (models == null) {
      models = new Array[ModelResourceLocation](EnumBeeType.values.length)
    }
    models(beeType.ordinal) = manager.getModelLocation(beeTypeNameBase)
  }

  @SideOnly(Side.CLIENT)
  override def getModel(kind: EnumBeeType): ModelResourceLocation = {
    return models(kind.ordinal)
  }
}
