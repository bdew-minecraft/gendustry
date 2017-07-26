/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry

import java.io.File

import net.bdew.gendustry.api.GendustryAPI
import net.bdew.gendustry.apiimpl.{BlockApiImpl, ItemApiImpl, RegistriesApiImpl}
import net.bdew.gendustry.compat.{ForestryHelper, PowerProxy}
import net.bdew.gendustry.config._
import net.bdew.gendustry.config.loader.TuningLoader
import net.bdew.gendustry.custom._
import net.bdew.gendustry.machines.apiary.GendustryErrorStates
import net.bdew.gendustry.machines.apiary.upgrades.Upgrades
import net.bdew.gendustry.misc._
import net.bdew.gendustry.recipes.{ChargeRecipe, GeneRecipe}
import net.minecraft.command.CommandHandler
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event._
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.registry.ForgeRegistries
import net.minecraftforge.fml.relauncher.Side
import org.apache.logging.log4j.Logger

@Mod(modid = Gendustry.modId, version = "GENDUSTRY_VER", name = "Gendustry", dependencies = "required-after:forestry@[5.0.0.0,);after:BuildCraft|energy;after:BuildCraft|Silicon;after:IC2;after:CoFHCore;after:BinnieCore;after:ExtraBees;after:ExtraTrees;after:MineFactoryReloaded;after:MagicBees;required-after:bdlib@[BDLIB_VER,)", modLanguage = "scala")
object Gendustry {
  var log: Logger = _
  var instance = this

  final val modId = "gendustry"
  final val channel = "bdew.gendustry"

  var configDir: File = _

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
    GendustryAPI.ConfigLoader = TuningLoader

    PowerProxy.logModVersions()

    ForestryHelper.logAvailableRoots()

    configDir = new File(event.getModConfigurationDirectory, "gendustry")
    TuningLoader.loadConfigFiles()

    GendustryErrorStates.init()

    //    if (Misc.haveModVersion("BuildCraftAPI|statements"))
    //      TriggerProvider.registerTriggers()

    Fluids.load()
    Blocks.load()
    Items.load()
    Machines.load()

    if (event.getSide == Side.CLIENT) {
      GendustryClient.preInit()
    }
  }

  @EventHandler
  def init(event: FMLInitializationEvent) {
    if (event.getSide.isClient)
      Config.load(new File(configDir, "client.config"))

    NetworkRegistry.INSTANCE.registerGuiHandler(this, Config.guiHandler)

    Upgrades.init()

    ForgeRegistries.RECIPES.register(GeneRecipe)
    if (Tuning.getSection("Power").getSection("RedstoneCharging").getBoolean("Enabled")) {
      ForgeRegistries.RECIPES.register(ChargeRecipe)
    }

    if (ForestryHelper.haveRoot("Bees")) {
      CustomContent.registerBranches()
      CustomContent.registerSpecies()
    } else {
      logInfo("Apiculture module seems to be disabled in Forestry, not registering custom bees")
    }

    if (event.getSide == Side.CLIENT) {
      GendustryClient.init()
    }

    FMLInterModComms.sendMessage("waila", "register", "net.bdew.gendustry.waila.WailaHandler.loadCallback")
  }

  @EventHandler
  def postInit(event: FMLPostInitializationEvent) {
    TuningLoader.loadDelayed()
    if (ForestryHelper.haveRoot("Bees")) {
      CustomFlowerAlleles.registerAlleles()
      CustomContent.registerTemplates()
      CustomContent.registerMutations()
      CustomHives.registerHives()
    }
    if (event.getSide == Side.CLIENT) {
      GendustryClient.postInit()
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
