/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.api;

import net.minecraft.item.ItemStack;

public interface IApiaryUpgrade {
    void applyModifiers(ApiaryModifiers mods, ItemStack stack);

    long getStackingId(ItemStack stack);

    int getMaxNumber(ItemStack stack);
}
