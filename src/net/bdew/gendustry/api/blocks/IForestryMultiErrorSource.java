/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.api.blocks;

import forestry.api.core.IErrorState;

import java.util.Set;

/**
 * @deprecated use IErrorLogicSource from forestry API instead
 */
@Deprecated
public interface IForestryMultiErrorSource {
    Set<IErrorState> getErrorStates();
}
