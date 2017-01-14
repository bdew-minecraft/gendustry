/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.apiimpl

import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.api.registries.IFluidSourceRegistry
import net.bdew.gendustry.fluids.FluidSourceRegistry
import net.bdew.lib.Misc
import net.minecraft.block.Block
import net.minecraft.item.{Item, ItemStack}
import net.minecraftforge.fluids.{Fluid, FluidRegistry}
import net.minecraftforge.oredict.OreDictionary

import scala.collection.mutable

class FluidSourceWrapper(fluidId: String, registry: FluidSourceRegistry) extends IFluidSourceRegistry {
  private val values = mutable.Map.empty[Item, mutable.Map[Int, Int]]
  private var merged = false

  override def getFluid: Fluid = FluidRegistry.getFluid(fluidId)

  override def canAdd: Boolean = !merged

  override def get(item: ItemStack): Int =
    if (merged)
      registry.getValue(item)
    else {
      val v = registry.getValue(item)
      if (v > 0) return v
      val sub = values.getOrElse(item.getItem, return 0)
      if (sub.contains(item.getItemDamage)) {
        sub(item.getItemDamage)
      } else if (sub.contains(OreDictionary.WILDCARD_VALUE)) {
        sub(OreDictionary.WILDCARD_VALUE)
      } else {
        0
      }
    }

  override def get(item: Item): Int = get(new ItemStack(item))
  override def get(block: Block): Int = get(new ItemStack(block))

  override def add(item: ItemStack, value: Int): Boolean = {
    if (item.isEmpty) {
      Gendustry.logError("Ignoring invalid fluid source API call from %s - empty item", Misc.getActiveModId)
      false
    } else if (merged) {
      Gendustry.logError("Ignoring invalid fluid source API call from %s - registry cannot be modified after postInit", Misc.getActiveModId)
      false
    } else {
      Gendustry.logInfo("Registering %s source from %s: %s (%s) -> %d mb", fluidId, Misc.getActiveModId, item.getUnlocalizedName, item.getItemDamage, value)
      values.getOrElseUpdate(item.getItem, mutable.Map.empty) += (item.getItemDamage -> value)
      true
    }
  }

  override def add(item: Item, value: Int): Boolean = add(new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE), value)
  override def add(block: Block, value: Int): Boolean = add(new ItemStack(block, 1, OreDictionary.WILDCARD_VALUE), value)

  def doMerge(): Unit = {
    val tmp = for (key <- registry.values.keySet ++ values.keySet)
      yield key -> (values.getOrElse(key, mutable.Map.empty) ++ registry.values.getOrElse(key, mutable.Map.empty))
    registry.values.clear()
    values.clear()
    merged = true
    registry.values ++= tmp
  }
}
