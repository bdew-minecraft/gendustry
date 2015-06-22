/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.fluids

import net.minecraft.item.{Item, ItemStack}
import net.minecraftforge.oredict.OreDictionary

import scala.collection.mutable

class FluidSourceRegistry {
  val values = mutable.Map.empty[Item, mutable.Map[Int, Int]]

  def register(stack: ItemStack, value: Int): Unit = register(stack.getItem, stack.getItemDamage, value)

  def register(item: Item, damage: Int, value: Int) =
    values.getOrElseUpdate(item, mutable.Map.empty) += (damage -> value)

  def getValue(item: ItemStack): Int = {
    val sub = values.getOrElse(item.getItem, return 0)
    if (sub.contains(item.getItemDamage)) {
      return sub(item.getItemDamage)
    } else if (sub.contains(OreDictionary.WILDCARD_VALUE)) {
      return sub(OreDictionary.WILDCARD_VALUE)
    } else {
      return 0
    }
  }
}

object MutagenSources extends FluidSourceRegistry

object LiquidDNASources extends FluidSourceRegistry

object ProteinSources extends FluidSourceRegistry

