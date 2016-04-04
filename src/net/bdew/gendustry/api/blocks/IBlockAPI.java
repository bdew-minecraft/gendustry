/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.api.blocks;

import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

/**
 * Interface to various information about blocks and TileEntities
 * WARNING: Everything retrieved from this interface is not guaranteed to be correct or up to date on client side!
 */
public interface IBlockAPI {
    /**
     * @return True if a valid Industrial Apiary
     */
    boolean isIndustrialApiary(World w, BlockPos pos);

    /**
     * @return Industrial Apiary Interface
     */
    IIndustrialApiary getIndustrialApiary(World w, BlockPos pos);

    /**
     * @return True if a valid mutatron
     */
    boolean isMutatron(World w, BlockPos pos);

    /**
     * @return Mutatron interface
     */
    IMutatron getMutatron(World w, BlockPos pos);

    /**
     * @return True if a valid Advanced mutatron
     */
    boolean isAdvancedMutatron(World w, BlockPos pos);

    /**
     * @return Advanced mutatron Interface
     */
    IAdvancedMutatron getAdvancedMutatron(World w, BlockPos pos);

    /**
     * @return True if a valid machine that does work (most machines in this mod)
     */
    boolean isWorkerMachine(World w, BlockPos pos);

    /**
     * @return worker interface
     */
    IWorkerMachine getWorkerMachine(World w, BlockPos pos);
}
