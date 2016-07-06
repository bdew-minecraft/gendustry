/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.api;

import java.io.File;
import java.io.Reader;

/**
 * Allows other mods to pass configuration data to Gendustry
 */
public interface IConfigLoader {
    /**
     * Loads additional configuration using Gendustry config system
     *
     * @param file file to load from
     * @return true if loaded successfully
     */
    boolean loadConfig(File file);

    /**
     * Loads additional configuration using Gendustry config system
     *
     * @param reader Reader to load from
     * @return true if loaded successfully
     */
    boolean loadConfig(Reader reader);

    /**
     * Loads additional configuration using Gendustry config system
     *
     * @param resourceName Name of resource (in any loaded mod) to load from
     * @return true if loaded successfully
     */
    boolean loadConfig(String resourceName);
}
