/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry

import net.bdew.gendustry.custom.BeeModelProvider
import net.bdew.gendustry.machines.apiary.upgrades.ItemApiaryUpgrade
import net.bdew.gendustry.misc.{GendustryCreativeTabs, GeneticsCache, ResourceListener}
import net.bdew.gendustry.model.ExtendedModelLoader
import net.minecraftforge.client.model.ModelLoaderRegistry

object GendustryClient {
  def preInit(): Unit = {
    ResourceListener.init()
    ModelLoaderRegistry.registerLoader(ExtendedModelLoader)
    ItemApiaryUpgrade.registerUpgradeModels()
  }

  def init(): Unit = {
    ItemApiaryUpgrade.registerItemModels()
    BeeModelProvider.registerModelsManual()
  }

  def postInit(): Unit = {
    GeneticsCache.load()
    GendustryCreativeTabs.init()
  }
}
