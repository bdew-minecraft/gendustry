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
import net.bdew.gendustry.config.Config
import cpw.mods.fml.common.event.FMLInterModComms
import net.bdew.gendustry.Gendustry
import codechicken.nei.recipe.{ICraftingHandler, IUsageHandler}
import net.bdew.gendustry.items.GeneSample
import net.bdew.gendustry.machines.mproducer.MachineMutagenProducer
import net.bdew.gendustry.machines.mutatron.MachineMutatron
import net.bdew.gendustry.machines.sampler.MachineSampler
import net.bdew.gendustry.machines.imprinter.MachineImprinter
import net.bdew.gendustry.machines.extractor.MachineExtractor
import net.bdew.gendustry.machines.liquifier.MachineLiquifier
import net.bdew.gendustry.machines.replicator.MachineReplicator
import net.bdew.gendustry.machines.transposer.MachineTransposer
import codechicken.nei.guihook.GuiContainerManager
import net.bdew.gendustry.misc.GeneticsCache

class NEIGendustryConfig extends IConfigureNEI {
  def getName: String = "Gendustry"
  def getVersion: String = "GENDUSTRY_VER"

  def addRecipeHandler(h: IUsageHandler with ICraftingHandler) {
    API.registerRecipeHandler(h)
    API.registerUsageHandler(h)
  }

  def loadConfig() {
    if (Config.neiAddSamples)
      GeneticsCache.geneSamples.foreach(x => API.addItemListEntry(GeneSample.newStack(x))) // TODO: is this right?

    addRecipeHandler(new TemplateCraftingHandler)

    if (MachineMutagenProducer.enabled) {
      addRecipeHandler(new MutagenProducerHandler)
      FMLInterModComms.sendRuntimeMessage(Gendustry, "NEIPlugins", "register-crafting-handler", "Gendustry@Mutagen Producer@MutagenProducer")
    }

    if (MachineMutatron.enabled) {
      addRecipeHandler(new MutatronHandler)
      FMLInterModComms.sendRuntimeMessage(Gendustry, "NEIPlugins", "register-crafting-handler", "Gendustry@Mutatron@Mutatron")
    }

    if (MachineSampler.enabled) {
      addRecipeHandler(new SamplerHandler)
      FMLInterModComms.sendRuntimeMessage(Gendustry, "NEIPlugins", "register-crafting-handler", "Gendustry@Sampler@Sampler")
    }

    if (MachineImprinter.enabled) {
      addRecipeHandler(new ImprinterHandler)
      FMLInterModComms.sendRuntimeMessage(Gendustry, "NEIPlugins", "register-crafting-handler", "Gendustry@Imprinter@Imprinter")
    }

    if (MachineExtractor.enabled) {
      addRecipeHandler(new ExtractorHandler)
      FMLInterModComms.sendRuntimeMessage(Gendustry, "NEIPlugins", "register-crafting-handler", "Gendustry@Extractor@Extractor")
    }

    if (MachineLiquifier.enabled) {
      addRecipeHandler(new LiquifierHandler)
      FMLInterModComms.sendRuntimeMessage(Gendustry, "NEIPlugins", "register-crafting-handler", "Gendustry@Liquifier@Liquifier")
    }

    if (MachineReplicator.enabled) {
      addRecipeHandler(new ReplicatorHandler)
      FMLInterModComms.sendRuntimeMessage(Gendustry, "NEIPlugins", "register-crafting-handler", "Gendustry@Replicator@Replicator")
    }

    if (MachineTransposer.enabled) {
      addRecipeHandler(new TransposerHandler)
      FMLInterModComms.sendRuntimeMessage(Gendustry, "NEIPlugins", "register-crafting-handler", "Gendustry@Transposer@Transposer")
    }

    GuiContainerManager.addTooltipHandler(new SmeltingTooltipHandler)
  }
}
