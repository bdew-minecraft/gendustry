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
import net.minecraft.client.renderer.texture.IconRegister
import net.minecraft.util.Icon
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fluids.{Fluid, BlockFluidClassic}

class BlockFluid(id: Int, val fluid: Fluid, val ownIcons: Boolean) extends BlockFluidClassic(id, fluid, Material.water) {
  setUnlocalizedName(Gendustry.modId + "." + fluid.getName)

  override def canDisplace(world: IBlockAccess, x: Int, y: Int, z: Int): Boolean = {
    if (world.getBlockMaterial(x, y, z).isLiquid) return false
    return super.canDisplace(world, x, y, z)
  }

  override def displaceIfPossible(world: World, x: Int, y: Int, z: Int): Boolean = {
    if (world.getBlockMaterial(x, y, z).isLiquid) return false
    return super.displaceIfPossible(world, x, y, z)
  }

  @SideOnly(Side.CLIENT)
  override def getIcon(side: Int, meta: Int): Icon = if (side == 0 || side == 1) fluid.getStillIcon else fluid.getFlowingIcon

  @SideOnly(Side.CLIENT)
  override def registerIcons(register: IconRegister) {
    if (ownIcons) {
      fluid.setStillIcon(register.registerIcon(Gendustry.modId + ":fluids/" + fluid.getName + "/still"))
      fluid.setFlowingIcon(register.registerIcon(Gendustry.modId + ":fluids/" + fluid.getName + "/flowing"))
    }
  }
}