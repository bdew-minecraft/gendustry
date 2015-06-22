/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.apiimpl

import net.bdew.gendustry.api.registries.IRegistriesApi
import net.bdew.gendustry.fluids.{LiquidDNASources, MutagenSources, ProteinSources}

object RegistriesApiImpl extends IRegistriesApi {
  override val getMutagenRegistry = new FluidSourceWrapper("Mutagen", MutagenSources)
  override val getProteinRegistry = new FluidSourceWrapper("Protein", ProteinSources)
  override val getLiquidDnaRegistry = new FluidSourceWrapper("LiquidDNA", LiquidDNASources)

  def mergeToMainRegistry(): Unit = {
    getMutagenRegistry.doMerge()
    getProteinRegistry.doMerge()
    getLiquidDnaRegistry.doMerge()
  }
}
