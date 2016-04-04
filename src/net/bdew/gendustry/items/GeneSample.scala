/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.items

import java.util

import net.bdew.gendustry.config.Items
import net.bdew.gendustry.forestry.GeneSampleInfo
import net.bdew.gendustry.misc.{GendustryCreativeTabs, GeneticsCache}
import net.bdew.lib.Misc
import net.bdew.lib.items.BaseItem
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.nbt.NBTTagCompound

object GeneSample extends BaseItem("GeneSample") {

  setMaxStackSize(1)
  setContainerItem(Items.geneSampleBlank)

  override def getCreativeTabs = Array(GendustryCreativeTabs.main, GendustryCreativeTabs.samples)

  override def getSubItems(item: Item, tab: CreativeTabs, list: util.List[ItemStack]) {
    import scala.collection.JavaConversions._
    val l = list.asInstanceOf[util.List[ItemStack]]
    tab match {
      case GendustryCreativeTabs.main => l.add(new ItemStack(this))
      case GendustryCreativeTabs.samples =>
        l.addAll(GeneticsCache.geneSamples map newStack)
      case _ =>
    }
  }

  def newStack(info: GeneSampleInfo): ItemStack = {
    val stack = new ItemStack(this)
    val tag = new NBTTagCompound()
    info.writeToNBT(tag)
    stack.setTagCompound(tag)
    return stack
  }

  def getInfo(stack: ItemStack): GeneSampleInfo = GeneSampleInfo.fromNBT(stack.getTagCompound)

  override def addInformation(stack: ItemStack, playerIn: EntityPlayer, tooltip: util.List[String], advanced: Boolean): Unit = {
    if (stack.hasTagCompound) {
      val info = getInfo(stack)
      tooltip.add(Misc.toLocal("gendustry.label.sample." + info.root.getUID))
      tooltip.add(info.getLocalizedName)
    }
  }
}
