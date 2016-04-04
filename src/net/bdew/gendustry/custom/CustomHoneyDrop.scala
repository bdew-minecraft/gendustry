/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.custom

import java.util

import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.config.Tuning
import net.bdew.lib.items.BaseItem
import net.bdew.lib.recipes.gencfg.ConfigSection
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.{Item, ItemStack}

object CustomHoneyDrop extends BaseItem("HoneyDrop") {

  case class HoneyDropInfo(name: String, color1: Int, color2: Int)

  setHasSubtypes(true)
  setMaxDamage(-1)

  val data = (Tuning.getOrAddSection("HoneyDrops").filterType(classOf[ConfigSection]) map {
    case (ident, cfg) => cfg.getInt("ID") -> HoneyDropInfo(
      ident,
      cfg.getColor("PrimaryColor").asRGB,
      cfg.getColor("SecondaryColor").asRGB
    )
  }).toMap

  def getData(stack: ItemStack) = data.get(stack.getItemDamage)

  override def getColorFromItemStack(stack: ItemStack, pass: Int): Int = {
    val data = getData(stack).getOrElse(return 0)
    pass match {
      case 0 => data.color1
      case _ => data.color2
    }
  }

  override def getSubItems(item: Item, par2CreativeTabs: CreativeTabs, list: util.List[ItemStack]) {
    for ((id, name) <- data)
      list.add(new ItemStack(this, 1, id))
  }

  override def getUnlocalizedName(stack: ItemStack) =
    getData(stack).map(x => "%s.honeydrop.%s".format(Gendustry.modId, x.name)).getOrElse("invalid")
}

