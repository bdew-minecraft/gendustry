/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.fluids

import java.util.Locale

import net.bdew.gendustry.Gendustry
import net.minecraft.init.Items
import net.minecraft.item.{ItemBucket, ItemStack}
import net.minecraft.util.math.RayTraceResult
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.player.FillBucketEvent
import net.minecraftforge.fluids.{BlockFluidBase, Fluid}
import net.minecraftforge.fml.common.eventhandler.Event.Result
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class ItemFluidBucket(fluid: Fluid) extends ItemBucket(fluid.getBlock) {
  setUnlocalizedName(Gendustry.modId + "." + fluid.getName.toLowerCase(Locale.US) + ".bucket")
  setRegistryName(Gendustry.modId, fluid.getName + "Bucket")

  setContainerItem(Items.BUCKET)

  MinecraftForge.EVENT_BUS.register(this)

  @SubscribeEvent
  def onBucketFill(event: FillBucketEvent) {
    if (event.getTarget != null && event.getTarget.typeOfHit == RayTraceResult.Type.BLOCK) {
      val state = event.getWorld.getBlockState(event.getTarget.getBlockPos)
      if (state.getBlock == fluid.getBlock && state.getValue(BlockFluidBase.LEVEL) == 0) {
        event.getWorld.setBlockToAir(event.getTarget.getBlockPos)
        event.setFilledBucket(new ItemStack(this))
        event.setResult(Result.ALLOW)
      }
    }
  }
}