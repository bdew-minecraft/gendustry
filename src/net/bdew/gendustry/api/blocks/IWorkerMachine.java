/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.api.blocks;

public interface IWorkerMachine {
    /**
     * @return true if a process is ongoing
     */
    boolean isWorking();

    /**
     * @return Progress of current operation [0, 1]
     */
    float getProgress();
}
