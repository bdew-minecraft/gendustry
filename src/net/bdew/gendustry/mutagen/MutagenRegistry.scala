/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.mutagen

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import scala.collection.mutable
import net.minecraftforge.oredict.OreDictionary

object MutagenRegistry {
  val values = mutable.Map.empty[Int, mutable.Map[Int, Int]]

  def register(block: Block, value: Integer): Unit = register(block.blockID, OreDictionary.WILDCARD_VALUE, value)
  def register(item: Item, value: Integer): Unit = register(item.itemID, OreDictionary.WILDCARD_VALUE, value)
  def register(item: ItemStack, value: Integer): Unit = register(item.itemID, item.getItemDamage, value)

  def register(id: Int, damage: Int, value: Int) {
    if (values.contains(id)) {
      values(id) += (damage -> value)
    } else {
      val sub = mutable.Map.empty[Int, Int]
      sub += (damage -> value)
      values.put(id, sub)
    }
  }

  def getValue(item: ItemStack): Int = {
    if (!values.contains(item.itemID)) return 0
    val sub = values(item.itemID)
    if (sub.contains(item.getItemDamage)) {
      return sub(item.getItemDamage)
    } else if (sub.contains(OreDictionary.WILDCARD_VALUE)) {
      return sub(OreDictionary.WILDCARD_VALUE)
    } else {
      return 0
    }
  }
}