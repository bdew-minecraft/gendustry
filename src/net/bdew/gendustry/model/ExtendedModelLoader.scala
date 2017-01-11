/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.model

import net.bdew.gendustry.Gendustry
import net.bdew.lib.covers.CoverModelEnhancer
import net.bdew.lib.render.models.ModelEnhancer
import net.minecraft.client.resources.IResourceManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.{ICustomModelLoader, ModelLoaderRegistry}

object ExtendedModelLoader extends ICustomModelLoader {
  override def accepts(modelLocation: ResourceLocation) =
    modelLocation.getResourceDomain.equals(Gendustry.modId) && modelLocation.getResourcePath.endsWith(".extended")

  def wrap(model: String, enhancer: ModelEnhancer) =
    enhancer.wrap(ModelLoaderRegistry.getModel(new ResourceLocation(model)))

  override def loadModel(modelLocation: ResourceLocation) =
    modelLocation.getResourcePath match {
      case "models/block/sided_covers.extended" => wrap("minecraft:block/cube_bottom_top", CoverModelEnhancer)
      case _ => null
    }

  override def onResourceManagerReload(resourceManager: IResourceManager) = {}
}
