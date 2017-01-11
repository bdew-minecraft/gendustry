/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.apiimpl

import forestry.api.genetics.IAlleleSpecies
import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.api.EnumMutationSetting
import net.bdew.gendustry.api.registries.IMutatronOverrides
import net.bdew.lib.Misc

object MutatronOverridesImpl extends IMutatronOverrides {
  var overrides = Map.empty[String, EnumMutationSetting]

  override def set(species: IAlleleSpecies, setting: EnumMutationSetting): Unit = set(species.getUID, setting)
  override def set(speciesUid: String, setting: EnumMutationSetting): Unit = {
    Gendustry.logInfo("Registering mutatron override from %s: %s -> %s", Misc.getActiveModId, speciesUid, setting)
    overrides += (speciesUid -> setting)
  }
}
