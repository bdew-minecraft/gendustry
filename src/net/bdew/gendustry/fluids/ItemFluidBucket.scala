/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.fluids

import java.util.Locale

import cpw.mods.fml.common.eventhandler.Event.Result
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.registry.GameRegistry
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.gendustry.Gendustry
import net.bdew.lib.Misc
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.item.{ItemBucket, ItemStack}
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.player.FillBucketEvent
import net.minecraftforge.fluids.Fluid

class ItemFluidBucket(fluid: Fluid) extends ItemBucket(fluid.getBlock) {
  setUnlocalizedName(Gendustry.modId + "." + fluid.getName.toLowerCase(Locale.US) + ".bucket")

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
    itemIcon = reg.registerIcon(Misc.iconName(Gendustry.modId, "bucket", fluid.getName))
  }
}