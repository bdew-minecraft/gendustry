/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.compat.itempush

import net.bdew.lib.Misc
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing

object ItemPush {
  var proxies = List.empty[ItemPushProxy]

  def init() {
    register(VanillaPush)
    if (Misc.haveModVersion("BuildCraftAPI|transport@[3.0,)")) register(BCPipePushProxy)
    //    if (Misc.haveModVersion("CoFHAPI|transport")) register(CofhConduitPushProxy)
    //    if (Misc.haveModVersion("Mekanism")) register(MekanismPushProxy)
  }

  def register(p: ItemPushProxy) = proxies :+= p

  def pushStack(from: TileEntity, dir: EnumFacing, st: ItemStack) = {
    var stack = st
    for (proxy <- proxies if stack != null && stack.stackSize > 0)
      stack = proxy.pushStack(from, dir, stack)
    if (stack != null && stack.stackSize <= 0)
      null
    else
      stack
  }
}
