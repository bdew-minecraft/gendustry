/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry

import java.util.logging.Logger
import net.bdew.gendustry.config._
import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.Mod.EventHandler
import cpw.mods.fml.common.event.{FMLServerStartingEvent, FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import cpw.mods.fml.common.network.NetworkMod
import cpw.mods.fml.common.network.NetworkRegistry
import java.io.File
import net.bdew.gendustry.machines.apiary.upgrades.Upgrades
import net.bdew.gendustry.compat.PowerProxy
import cpw.mods.fml.relauncher.Side
import net.bdew.gendustry.gui.HintIcons
import net.bdew.gendustry.compat.triggers.TriggerProvider
import net.minecraft.command.CommandHandler

@Mod(modid = Gendustry.modId, version = "GENDUSTRY_VER", name = "Gendustry", dependencies = "required-after:Forestry@[2.3.1.0,);after:BuildCraft|energy;after:BuildCraft|Silicon;after:IC2;after:CoFHCore;after:BinnieCore;after:ExtraBees;after:ExtraTrees;after:MineFactoryReloaded;required-after:bdlib@[BDLIB_VER,)", modLanguage = "scala")
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
object Gendustry {
  var log: Logger = null
  var instance = this

  final val modId = "gendustry"
  final val channel = "bdew.gendustry"

  var configDir: File = null
  var configFile: File = null

  def logInfo(msg: String, args: Any*) = log.info(msg.format(args: _*))
  def logWarn(msg: String, args: Any*) = log.warning(msg.format(args: _*))

  @EventHandler
  def preInit(event: FMLPreInitializationEvent) {
    log = event.getModLog
    PowerProxy.logModVersions()
    configDir = event.getModConfigurationDirectory
    configFile = event.getSuggestedConfigurationFile
    TuningLoader.load("tuning")
    TuningLoader.load("recipes")
    TuningLoader.load("override", false)
    TriggerProvider.registerTriggers()
    if (event.getSide == Side.CLIENT) {
      HintIcons.init()
    }
  }

  @EventHandler
  def init(event: FMLInitializationEvent) {
    Config.load(configFile)
    NetworkRegistry.instance.registerGuiHandler(this, Config.guiHandler)
    Upgrades.init()
    TuningLoader.loadDealayed()
  }

  @EventHandler
  def postInit(event: FMLPostInitializationEvent) {
  }

  @EventHandler
  def serverStarting(event: FMLServerStartingEvent) {
    val commandHandler = event.getServer.getCommandManager.asInstanceOf[CommandHandler]
    commandHandler.registerCommand(new CommandGiveTemplate)
    commandHandler.registerCommand(new CommandGiveSample)
  }
}