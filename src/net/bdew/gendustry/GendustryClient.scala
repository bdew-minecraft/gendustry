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
