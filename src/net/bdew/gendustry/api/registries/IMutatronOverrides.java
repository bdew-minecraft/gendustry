/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.api.registries;

import forestry.api.genetics.IAlleleSpecies;
import net.bdew.gendustry.api.EnumMutationSetting;

public interface IMutatronOverrides {
    /**
     * @param species Species to set override for
     * @param setting Override setting
     */
    void set(IAlleleSpecies species, EnumMutationSetting setting);

    /**
     * @param speciesUid Species allele UID to set override for
     * @param setting    Override setting
     */
    void set(String speciesUid, EnumMutationSetting setting);
}
