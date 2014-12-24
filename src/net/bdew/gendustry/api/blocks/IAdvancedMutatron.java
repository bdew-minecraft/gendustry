/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.api.blocks;

import net.minecraft.item.ItemStack;

import java.util.Map;

public interface IAdvancedMutatron extends IWorkerMachine {
    /**
     * @return List of possible mutations for current inventory
     */
    Map<Integer, ItemStack> getPossibleMutations();

    /**
     * Sets the requested mutation and start process if possible
     *
     * @param mutation key of the required mutation in the map
     */
    void setMutation(int mutation);
}
