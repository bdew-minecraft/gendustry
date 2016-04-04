/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.fluids

import net.bdew.gendustry.Gendustry
import net.minecraft.block.material.Material
import net.minecraft.util.BlockPos
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.fluids.{BlockFluidClassic, Fluid}

class BlockFluid(val fluid: Fluid, val ownIcons: Boolean) extends BlockFluidClassic(fluid, Material.water) {
  setRegistryName(Gendustry.modId, fluid.getName)

  override def canDisplace(world: IBlockAccess, pos: BlockPos): Boolean = {
    if (world.getBlockState(pos).getBlock.getMaterial.isLiquid) return false
    return super.canDisplace(world, pos)
  }

  override def displaceIfPossible(world: World, pos: BlockPos): Boolean = {
    if (world.getBlockState(pos).getBlock.getMaterial.isLiquid) return false
    return super.displaceIfPossible(world, pos)
  }
}