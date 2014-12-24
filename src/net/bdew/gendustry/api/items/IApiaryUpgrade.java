/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.api.items;

import net.bdew.gendustry.api.ApiaryModifiers;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Apiary upgrade, this interface is implemented by the Item subclass of the upgrade
 * Can be implemented by other mods to add custom upgrades
 */
public interface IApiaryUpgrade {
    /**
     * @return Human readable name
     */
    String getDisplayName(ItemStack stack);

    /**
     * @return Human readable description (what would appear on a tooltip below the name)
     */
    List<String> getDisplayDetails(ItemStack stack);

    /**
     * A unique ID for stacking purposes. If 2 item stacks return the same ID they will
     * be added together when counting upgrades to check maximum number.
     * If two stacks return the same stacking ID they should return the same number from
     * getMaxNumber or weirdness will ensue.
     *
     * @return Unique stacking id
     */
    long getStackingId(ItemStack stack);

    /**
     * @return How many times this upgrade can be installed.
     */
    int getMaxNumber(ItemStack stack);

    /**
     * Applies the effects of an upgrade
     */
    void applyModifiers(ApiaryModifiers mods, ItemStack stack);
}
