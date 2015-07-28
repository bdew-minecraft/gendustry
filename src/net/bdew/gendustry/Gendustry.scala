/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry

import java.io.File

import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.Mod.EventHandler
import cpw.mods.fml.common.event._
import cpw.mods.fml.common.network.NetworkRegistry
import cpw.mods.fml.relauncher.Side
import net.bdew.gendustry.api.GendustryAPI
import net.bdew.gendustry.apiimpl.{BlockApiImpl, ItemApiImpl, RegistriesApiImpl}
import net.bdew.gendustry.compat.itempush.ItemPush
import net.bdew.gendustry.compat.triggers.TriggerProvider
import net.bdew.gendustry.compat.{ForestryHelper, PowerProxy}
import net.bdew.gendustry.config._
import net.bdew.gendustry.config.loader.TuningLoader
import net.bdew.gendustry.custom.{CustomContent, CustomHives}
import net.bdew.gendustry.forestry.GeneRecipe
import net.bdew.gendustry.gui.HintIcons
import net.bdew.gendustry.machines.apiary.GendustryErrorStates
import net.bdew.gendustry.machines.apiary.upgrades.Upgrades
import net.bdew.gendustry.misc._
import net.bdew.lib.Misc
import net.minecraft.command.CommandHandler
import net.minecraftforge.oredict.RecipeSorter
import org.apache.logging.log4j.Logger

@Mod(modid = Gendustry.modId, version = "GENDUSTRY_VER", name = "Gendustry", dependencies = "required-after:Forestry@[4.0.0.0,);after:BuildCraft|energy;after:BuildCraft|Silicon;after:IC2;after:CoFHCore;after:BinnieCore;after:ExtraBees;after:ExtraTrees;after:MineFactoryReloaded;after:MagicBees;required-after:bdlib@[BDLIB_VER,)", modLanguage = "scala")
object Gendustry {
  var log: Logger = null
  var instance = this

  final val modId = "gendustry"
  final val channel = "bdew.gendustry"

  var configDir: File = null

  def logDebug(msg: String, args: Any*) = log.debug(msg.format(args: _*))
  def logInfo(msg: String, args: Any*) = log.info(msg.format(args: _*))
  def logWarn(msg: String, args: Any*) = log.warn(msg.format(args: _*))
  def logError(msg: String, args: Any*) = log.error(msg.format(args: _*))
  def logWarnException(msg: String, t: Throwable, args: Any*) = log.warn(msg.format(args: _*), t)
  def logErrorException(msg: String, t: Throwable, args: Any*) = log.error(msg.format(args: _*), t)

  @EventHandler
  def preInit(event: FMLPreInitializationEvent) {
    log = event.getModLog

    GendustryAPI.Items = ItemApiImpl
    GendustryAPI.Blocks = BlockApiImpl
    GendustryAPI.Registries = RegistriesApiImpl

    PowerProxy.logModVersions()
    ItemPush.init()

    ForestryHelper.logAvailableRoots()

    configDir = new File(event.getModConfigurationDirectory, "gendustry")
    TuningLoader.loadConfigFiles()

    GendustryErrorStates.init()

    if (Misc.haveModVersion("BuildCraftAPI|statements"))
      TriggerProvider.registerTriggers()

    Fluids.load()
    Blocks.load()
    Items.load()
    Machines.load()

    if (event.getSide == Side.CLIENT) {
      ResourceListener.init()
      HintIcons.init()
    }
  }

  @EventHandler
  def init(event: FMLInitializationEvent) {
    if (event.getSide.isClient)
      Config.load(new File(configDir, "client.config"))
    NetworkRegistry.INSTANCE.registerGuiHandler(this, Config.guiHandler)
    RecipeSorter.register("gendustry:GeneCopyRecipe", classOf[GeneRecipe], RecipeSorter.Category.SHAPELESS, "")
    Upgrades.init()
    TuningLoader.loadDelayed()
    if (ForestryHelper.haveRoot("Bees")) {
      CustomContent.registerBranches()
      CustomContent.registerSpecies()
    } else {
      logInfo("Apiculture module seems to be disabled in Forestry, not registering custom bees")
    }
    FMLInterModComms.sendMessage("Waila", "register", "net.bdew.gendustry.waila.WailaHandler.loadCallback")
  }

  @EventHandler
  def postInit(event: FMLPostInitializationEvent) {
    if (ForestryHelper.haveRoot("Bees")) {
      CustomContent.registerTemplates()
      CustomContent.registerMutations()
      CustomHives.registerHives()
    }
    if (event.getSide == Side.CLIENT) {
      GeneticsCache.load()
      GendustryCreativeTabs.init()
    }
    RegistriesApiImpl.mergeToMainRegistry()
  }

  @EventHandler
  def serverStarting(event: FMLServerStartingEvent) {
    val commandHandler = event.getServer.getCommandManager.asInstanceOf[CommandHandler]
    commandHandler.registerCommand(new CommandGiveTemplate)
    commandHandler.registerCommand(new CommandGiveSample)
    commandHandler.registerCommand(new CommandDumpAlleles)
  }
}
