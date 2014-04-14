/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.fluids

import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import net.bdew.gendustry.Gendustry
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.util.IIcon
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fluids.{Fluid, BlockFluidClassic}

class BlockFluid(val fluid: Fluid, val ownIcons: Boolean) extends BlockFluidClassic(fluid, Material.water) {
  setBlockName(Gendustry.modId + "." + fluid.getName)

  override def canDisplace(world: IBlockAccess, x: Int, y: Int, z: Int): Boolean = {
    world.getBlock(x, y, z).getMaterial.isLiquid
    if (world.getBlock(x, y, z).getMaterial.isLiquid) return false
    return super.canDisplace(world, x, y, z)
  }

  override def displaceIfPossible(world: World, x: Int, y: Int, z: Int): Boolean = {
    if (world.getBlock(x, y, z).getMaterial.isLiquid) return false
    return super.displaceIfPossible(world, x, y, z)
  }

  @SideOnly(Side.CLIENT)
  override def getIcon(side: Int, meta: Int): IIcon = if (side == 0 || side == 1) fluid.getStillIcon else fluid.getFlowingIcon

  @SideOnly(Side.CLIENT)
  override def registerBlockIcons(register: IIconRegister) {
    if (ownIcons) {
      fluid.setStillIcon(register.registerIcon(Gendustry.modId + ":fluids/" + fluid.getName + "/still"))
      fluid.setFlowingIcon(register.registerIcon(Gendustry.modId + ":fluids/" + fluid.getName + "/flowing"))
    }
  }
}