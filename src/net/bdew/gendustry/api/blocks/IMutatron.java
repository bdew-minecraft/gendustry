/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.api.blocks;

import com.mojang.authlib.GameProfile;
import net.minecraft.item.ItemStack;

public interface IMutatron extends IWorkerMachine {
    /**
     * @return First input slot contents
     */
    ItemStack getParent1();

    /**
     * @return Second input slot contents
     */
    ItemStack getParent2();

    /**
     * @return Game profile of owner (or last user)
     */
    GameProfile getOwner();
}
