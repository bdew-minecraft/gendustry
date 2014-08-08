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
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.item.{ItemStack, ItemBucket}
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.event.entity.player.FillBucketEvent
import net.minecraftforge.common.MinecraftForge
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.eventhandler.Event.Result
import cpw.mods.fml.common.registry.GameRegistry

class ItemFluidBucket(fluid: Fluid) extends ItemBucket(fluid.getBlock) {
  setUnlocalizedName(Gendustry.modId + "." + fluid.getName.toLowerCase + ".bucket")

  setContainerItem(GameRegistry.findItem("minecraft", "bucket"))

  MinecraftForge.EVENT_BUS.register(this)

  @SubscribeEvent
  def onBucketFill(event: FillBucketEvent) {
    if (event.world.getBlockMetadata(event.target.blockX, event.target.blockY, event.target.blockZ) != 0) return
    if (event.world.getBlock(event.target.blockX, event.target.blockY, event.target.blockZ) != fluid.getBlock) return
    event.world.setBlockToAir(event.target.blockX, event.target.blockY, event.target.blockZ)
    event.result = new ItemStack(this)
    event.setResult(Result.ALLOW)
  }

  @SideOnly(Side.CLIENT)
  override def registerIcons(reg: IIconRegister) {
    itemIcon = reg.registerIcon(Gendustry.modId + ":bucket/" + fluid.getName.toLowerCase)
  }
}