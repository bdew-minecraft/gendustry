package net.bdew.gendustry

import net.bdew.gendustry.custom.{CustomHoneyComb, CustomHoneyDrop}
import net.bdew.gendustry.misc.{GendustryCreativeTabs, GeneticsCache, ResourceListener}
import net.bdew.gendustry.model.ExtendedModelLoader
import net.bdew.lib.Client
import net.minecraftforge.client.model.ModelLoaderRegistry

object GendustryClient {
  def preInit(): Unit = {
    ResourceListener.init()
    ModelLoaderRegistry.registerLoader(ExtendedModelLoader)
  }

  def init(): Unit = {
    Client.minecraft.getItemColors.registerItemColorHandler(CustomHoneyComb, CustomHoneyComb)
    Client.minecraft.getItemColors.registerItemColorHandler(CustomHoneyDrop, CustomHoneyDrop)
  }

  def postInit(): Unit = {
    GeneticsCache.load()
    GendustryCreativeTabs.init()
  }
}
