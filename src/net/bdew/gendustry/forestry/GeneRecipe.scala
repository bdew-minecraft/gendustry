/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.forestry

import net.bdew.gendustry.items.{GeneSample, GeneTemplate}
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.util.NonNullList
import net.minecraft.world.World

class GeneRecipe extends IRecipe {
  def matches(inv: InventoryCrafting, world: World): Boolean = !getCraftingResult(inv).isEmpty
  def getCraftingResult(inv: InventoryCrafting): ItemStack = {
    var template: ItemStack = ItemStack.EMPTY
    var samples = Seq.empty[GeneSampleInfo]
    for (i <- 0 until 3; j <- 0 until 3) {
      val itm = inv.getStackInRowAndColumn(i, j)
      if (!itm.isEmpty) {
        if (itm.getItem == GeneSample && itm.hasTagCompound)
          samples :+= GeneSample.getInfo(itm)
        else if (itm.getItem == GeneTemplate && template.isEmpty)
          template = itm
        else if (!itm.isEmpty)
          return ItemStack.EMPTY
      }
    }
    if (samples.isEmpty || template.isEmpty) return ItemStack.EMPTY
    val out = template.copy()
    for (s <- samples) {
      if (!GeneTemplate.addSample(out, s)) return ItemStack.EMPTY
    }
    return out
  }

  override def getRemainingItems(inv: InventoryCrafting): NonNullList[ItemStack] =
    NonNullList.withSize(inv.getSizeInventory, ItemStack.EMPTY)

  def getRecipeSize: Int = 9
  def getRecipeOutput: ItemStack = new ItemStack(GeneTemplate)
}
