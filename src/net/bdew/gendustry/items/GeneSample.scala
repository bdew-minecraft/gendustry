/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.items

import net.bdew.gendustry.config.Items
import net.bdew.gendustry.forestry.GeneSampleInfo
import net.bdew.gendustry.misc.{GendustryCreativeTabs, GeneticsCache}
import net.bdew.lib.Misc
import net.bdew.lib.items.BaseItem
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.NonNullList

object GeneSample extends BaseItem("gene_sample") {

  setMaxStackSize(1)
  setContainerItem(Items.geneSampleBlank)

  override def getCreativeTabs = Array(GendustryCreativeTabs.main, GendustryCreativeTabs.samples)

  override def getSubItems(tab: CreativeTabs, subItems: NonNullList[ItemStack]): Unit = {
    import scala.collection.JavaConversions._
    tab match {
      case GendustryCreativeTabs.main => subItems.add(new ItemStack(this))
      case GendustryCreativeTabs.samples =>
        subItems.addAll(GeneticsCache.geneSamples map newStack)
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

  override def getItemStackDisplayName(stack: ItemStack): String =
    if (stack.hasTagCompound) {
      val info = getInfo(stack)
      Misc.toLocalF(getUnlocalizedName(stack) + ".name", Misc.toLocal("gendustry.label.sample." + info.root.getUID), info.getLocalizedName)
    } else {
      Misc.toLocalF(getUnlocalizedName(stack) + ".name", "INVALID", "ERROR")
    }

}
