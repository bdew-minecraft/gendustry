/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.compat.itempush

import net.minecraft.tileentity.TileEntity
import net.minecraft.item.ItemStack
import net.bdew.gendustry.compat.PowerProxy
import net.minecraftforge.common.util.ForgeDirection

object ItemPush {
  var proxies = List.empty[ItemPushProxy]

  def init() {
    register(VanillaPush)
    if (PowerProxy.haveBC) register(BCPipePushProxy)
    if (PowerProxy.haveTE) register(CofhConduitPushProxy)
  }

  def register(p: ItemPushProxy) = proxies :+= p

  def pushStack(from: TileEntity, dir: ForgeDirection, st: ItemStack) = {
    var stack = st
    for (proxy <- proxies if stack != null && stack.stackSize > 0)
      stack = proxy.pushStack(from, dir, stack)
    if (stack != null && stack.stackSize <= 0)
      null
    else
      stack
  }
}
