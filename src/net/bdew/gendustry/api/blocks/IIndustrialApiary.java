/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.api.blocks;

import forestry.api.apiculture.IBeeHousing;
import net.bdew.gendustry.api.ApiaryModifiers;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Public interface to Industrial Apiary TileEntity
 * Extends forestry IBeeHousing, IBeeModifier, IBeeListener, IHousing so all of those are usable as well
 */
public interface IIndustrialApiary extends IBeeHousing, IForestryMultiErrorSource {
    /**
     * Do not cache, stored copies will not be updated
     *
     * @return calculated modifiers from all upgrades
     */
    ApiaryModifiers getModifiers();

    /**
     * @return List of upgrade item stacks
     */
    List<ItemStack> getUpgrades();
}
