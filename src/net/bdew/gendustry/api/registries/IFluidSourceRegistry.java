/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.api.registries;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;

public interface IFluidSourceRegistry {
    /**
     * @return The fluid this registry produces
     */
    Fluid getFluid();

    /**
     * @return True if new values will be accepted
     */
    boolean canAdd();

    /**
     * Registers a new entry. Should be called before PostInit.
     *
     * @param item  ItemStack to add. Damage can be set to OreDictionary.WILDCARD_VALUE to accept all damage values.
     * @param value Amount of fluid produced, mB
     * @return True if item added successfully
     */
    boolean add(ItemStack item, int value);

    /**
     * Registers a new entry Should be called before PostInit.
     *
     * @param item  ItemStack to add
     * @param value Amount of fluid produced, mB
     * @return True if item added successfully
     */
    boolean add(Item item, int value);

    /**
     * Registers a new entry Should be called before PostInit.
     *
     * @param block Block to add
     * @param value Amount of fluid produced, mB
     * @return True if item added successfully
     */
    boolean add(Block block, int value);

    /**
     * Gets current value of item, 0 if not registered
     *
     * @param item Item to check
     * @return Amount of fluid produced, mB
     */
    int get(ItemStack item);

    /**
     * Gets current value of item, 0 if not registered
     *
     * @param item Item to check
     * @return Amount of fluid produced, mB
     */
    int get(Item item);

    /**
     * Gets current value of item, 0 if not registered
     *
     * @param block Block to check
     * @return Amount of fluid produced, mB
     */
    int get(Block block);
}
