/*
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry

import java.util.logging.Logger
import net.bdew.gendustry.config._
import net.minecraftforge.common.Configuration
import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.Mod.EventHandler
import cpw.mods.fml.common.event.FMLInitializationEvent
import cpw.mods.fml.common.event.FMLPostInitializationEvent
import cpw.mods.fml.common.event.FMLPreInitializationEvent
import cpw.mods.fml.common.network.NetworkMod
import cpw.mods.fml.common.network.NetworkRegistry
import net.bdew.gendustry.gui.GuiHandler
import java.io.File
import net.bdew.gendustry.machines.apiary.upgrades.Upgrades

@Mod(modid = Gendustry.modId, version = "@GendustryVer@", name = "Gendustry", dependencies = "required-after:Forestry;required-after:BuildCraft|Core;required-after:bdlib@[@BdLibVer@,)", modLanguage = "scala")
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
object Gendustry {
  var log: Logger = null
  var instance = this

  final val modId = "gendustry"
  final val channel = "bdew.gendustry"

  var configDir: File = null

  def logInfo(msg: String, args: Any*) = log.info(msg.format(args: _*))
  def logWarn(msg: String, args: Any*) = log.warning(msg.format(args: _*))

  @EventHandler
  def preInit(event: FMLPreInitializationEvent) {
    log = event.getModLog
    configDir = event.getModConfigurationDirectory
    TuningLoader.load("tuning")
    val config: Configuration = Config.load(event.getSuggestedConfigurationFile)
    try {
      Ids.init(config)
      Blocks.load(config)
      Machines.load(config)
      Items.load(config)
    } finally {
      config.save()
    }
  }

  @EventHandler
  def init(event: FMLInitializationEvent) {
    NetworkRegistry.instance.registerGuiHandler(this, GuiHandler)
    Upgrades.init()
    TuningLoader.load("recipes")
  }

  @EventHandler
  def postInit(event: FMLPostInitializationEvent) {
  }
}