/*
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.mutagen

import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.config.Blocks
import net.bdew.gendustry.config.Items
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IconRegister
import net.minecraft.item.ItemStack
import net.minecraft.util.Icon
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.Event.Result
import net.minecraftforge.event.ForgeSubscribe
import net.minecraftforge.event.entity.player.FillBucketEvent
import net.minecraftforge.fluids.BlockFluidClassic

class BlockMutagen(id: Int) extends BlockFluidClassic(id, Blocks.mutagenFluid, Material.water) {
  protected var stillIcon: Icon = null
  protected var flowingIcon: Icon = null

  Blocks.mutagenFluid.setBlockID(id)
  setUnlocalizedName(Gendustry.modId + ".mutagen")
  MinecraftForge.EVENT_BUS.register(this)

  override def colorMultiplier(iblockaccess: IBlockAccess, x: Int, y: Int, z: Int): Int = 0x66FF00

  override def canDisplace(world: IBlockAccess, x: Int, y: Int, z: Int): Boolean = {
    if (world.getBlockMaterial(x, y, z).isLiquid) return false
    return super.canDisplace(world, x, y, z)
  }

  override def displaceIfPossible(world: World, x: Int, y: Int, z: Int): Boolean = {
    if (world.getBlockMaterial(x, y, z).isLiquid) return false
    return super.displaceIfPossible(world, x, y, z)
  }

  @ForgeSubscribe
  def onBucketFill(event: FillBucketEvent) {
    if (event.world.getBlockMetadata(event.target.blockX, event.target.blockY, event.target.blockZ) != 0) return
    if (event.world.getBlockId(event.target.blockX, event.target.blockY, event.target.blockZ) != blockID) return
    event.world.setBlockToAir(event.target.blockX, event.target.blockY, event.target.blockZ)
    event.result = new ItemStack(Items.mutagenBucket)
    event.setResult(Result.ALLOW)
  }

  @SideOnly(Side.CLIENT)
  override def getIcon(side: Int, meta: Int): Icon = if (side == 0 || side == 1) stillIcon else flowingIcon

  @SideOnly(Side.CLIENT)
  override def registerIcons(register: IconRegister) {
    stillIcon = register.registerIcon(Gendustry.modId + ":mutagen/still")
    flowingIcon = register.registerIcon(Gendustry.modId + ":mutagen/flowing")
    Blocks.mutagenFluid.setIcons(stillIcon, flowingIcon)
  }
}