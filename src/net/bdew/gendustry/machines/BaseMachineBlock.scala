/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.machines

import net.bdew.lib.block.BaseBlock
import net.minecraft.block.material.{MapColor, Material}

object MachineMaterial extends Material(MapColor.IRON)

class BaseMachineBlock(name: String) extends BaseBlock(name, MachineMaterial) {
  setHardness(2)
}
