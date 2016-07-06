/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.api.registries;

public interface IRegistriesApi {
    /**
     * @return Mutagen source registry
     */
    IFluidSourceRegistry getMutagenRegistry();

    /**
     * @return Liquid DNA source registry
     */
    IFluidSourceRegistry getLiquidDnaRegistry();

    /**
     * @return Protein source registry
     */
    IFluidSourceRegistry getProteinRegistry();

    /**
     * @return Mutatron overrides
     */
    IMutatronOverrides getMutatronOverrides();
}
