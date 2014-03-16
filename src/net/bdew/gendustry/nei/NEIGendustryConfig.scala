/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.nei

import codechicken.nei.api.{API, IConfigureNEI}
import net.bdew.gendustry.config.{Machines, Config, Items}
import cpw.mods.fml.common.event.FMLInterModComms
import net.bdew.gendustry.Gendustry
import codechicken.nei.forge.GuiContainerManager
import codechicken.nei.recipe.{ICraftingHandler, IUsageHandler}

class NEIGendustryConfig extends IConfigureNEI {
  def getName: String = "Gendustry"
  def getVersion: String = "GENDUSTRY_VER"

  def addRecipeHandler(h: IUsageHandler with ICraftingHandler) {
    API.registerRecipeHandler(h)
    API.registerUsageHandler(h)
  }

  def loadConfig() {
    NEICache.load()
    if (Config.neiAddSamples)
      NEICache.geneSamples.foreach(x => API.addNBTItem(Items.geneSample.newStack(x)))

    addRecipeHandler(new TemplateCraftingHandler)

    if (Machines.mutagenProducer.enabled) {
      addRecipeHandler(new MutagenProducerHandler)
      FMLInterModComms.sendRuntimeMessage(Gendustry, "NEIPlugins", "register-crafting-handler", "Gendustry@Mutagen Producer@MutagenProducer")
    }

    if (Machines.mutatron.enabled) {
      addRecipeHandler(new MutatronHandler)
      FMLInterModComms.sendRuntimeMessage(Gendustry, "NEIPlugins", "register-crafting-handler", "Gendustry@Mutatron@Mutatron")
    }

    if (Machines.sampler.enabled) {
      addRecipeHandler(new SamplerHandler)
      FMLInterModComms.sendRuntimeMessage(Gendustry, "NEIPlugins", "register-crafting-handler", "Gendustry@Sampler@Sampler")
    }

    if (Machines.imprinter.enabled) {
      addRecipeHandler(new ImprinterHandler)
      FMLInterModComms.sendRuntimeMessage(Gendustry, "NEIPlugins", "register-crafting-handler", "Gendustry@Imprinter@Imprinter")
    }

    if (Machines.extractor.enabled) {
      addRecipeHandler(new ExtractorHandler)
      FMLInterModComms.sendRuntimeMessage(Gendustry, "NEIPlugins", "register-crafting-handler", "Gendustry@Extractor@Extractor")
    }

    if (Machines.liquifier.enabled) {
      addRecipeHandler(new LiquifierHandler)
      FMLInterModComms.sendRuntimeMessage(Gendustry, "NEIPlugins", "register-crafting-handler", "Gendustry@Liquifier@Liquifier")
    }


    GuiContainerManager.addTooltipHandler(new SmeltingTooltipHandler)
  }
}
