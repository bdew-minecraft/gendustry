/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.api;

import net.bdew.gendustry.api.blocks.IBlockAPI;
import net.bdew.gendustry.api.items.IItemAPI;

/**
 * Access to the API. This will be available after Gendustry PreInit runs.
 */
public class GendustryAPI {
    public static IItemAPI Items;
    public static IBlockAPI Blocks;
}
