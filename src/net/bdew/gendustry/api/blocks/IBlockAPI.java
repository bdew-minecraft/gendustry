/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.api.blocks;

import net.minecraft.world.World;

/**
 * Interface to various informations about blocks and TileEntities
 * WARNING: Everything retrieved from this interface is not guaranteed to be correct or up to date on client side!
 */
public interface IBlockAPI {
    /**
     * @return True if a valid Industrial Apiary
     */
    boolean isIndustrialApiary(World w, int x, int y, int z);

    /**
     * @return Industrial Apiary Interface
     */
    IIndustrialApiary getIndustrialApiary(World w, int x, int y, int z);

    /**
     * @return True if a valid Advanced mutatron
     */
    boolean isAdvancedMutatron(World w, int x, int y, int z);

    /**
     * @return Advanced mutatron Interface
     */
    IAdvancedMutatron getAdvancedMutatron(World w, int x, int y, int z);

    /**
     * @return True if a valid machine that does work (most machines in this mod)
     */
    boolean isWorkerMachine(World w, int x, int y, int z);

    /**
     * @return wroker interface
     */
    IWorkerMachine getWorkerMachine(World w, int x, int y, int z);
}
