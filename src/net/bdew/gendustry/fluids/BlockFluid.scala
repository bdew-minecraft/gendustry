/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.fluids

import net.bdew.gendustry.Gendustry
import net.minecraft.block.material.Material
import net.minecraft.util.math.BlockPos
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.fluids.{BlockFluidClassic, Fluid}

class BlockFluid(val fluid: Fluid, val ownIcons: Boolean) extends BlockFluidClassic(fluid, Material.WATER) {
  setRegistryName(Gendustry.modId, fluid.getName)
  setUnlocalizedName(Gendustry.modId + "." + fluid.getName)

  override def canDisplace(world: IBlockAccess, pos: BlockPos): Boolean = {
    val state = world.getBlockState(pos)
    if (state.getBlock.getMaterial(state).isLiquid)
      false
    else
      super.canDisplace(world, pos)
  }

  override def displaceIfPossible(world: World, pos: BlockPos): Boolean = {
    val state = world.getBlockState(pos)
    if (state.getBlock.getMaterial(state).isLiquid)
      false
    else
      super.displaceIfPossible(world, pos)
  }
}