/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.api;

import net.bdew.gendustry.api.blocks.IBlockAPI;
import net.bdew.gendustry.api.items.IItemAPI;
import net.bdew.gendustry.api.registries.IRegistriesApi;

/**
 * Access to the API. This will be available after Gendustry PreInit runs.
 */
public class GendustryAPI {
    public static IItemAPI Items;
    public static IBlockAPI Blocks;
    public static IRegistriesApi Registries;
    public static IConfigLoader ConfigLoader;
}
