/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.fluids

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import scala.collection.mutable
import net.minecraftforge.oredict.OreDictionary

class FluidRegistry {
  val values = mutable.Map.empty[Item, mutable.Map[Int, Int]]

  def register(stack: ItemStack, value: Int): Unit = register(stack.getItem, stack.getItemDamage, value)

  def register(item: Item, damage: Int, value: Int) {
    if (values.contains(item)) {
      values(item) += (damage -> value)
    } else {
      val sub = mutable.Map.empty[Int, Int]
      sub += (damage -> value)
      values.put(item, sub)
    }
  }

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

object MutagenSources extends FluidRegistry

object LiquidDNASources extends FluidRegistry

object ProteinSources extends FluidRegistry

