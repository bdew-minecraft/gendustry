/*
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.test

import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.bdew.lib.block.HasTE

class PowerEmitterBlock(id: Int) extends Block(id, Material.iron) with HasTE[PowerEmitterTile] {
  val TEClass = classOf[PowerEmitterTile]
  setUnlocalizedName("test.power.emitter")
  setHardness(1)
}
