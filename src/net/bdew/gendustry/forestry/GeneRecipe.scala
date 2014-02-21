/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.forestry

import net.minecraft.item.crafting.IRecipe
import net.minecraft.item.ItemStack
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.world.World
import net.bdew.gendustry.items.{GeneTemplate, GeneSample}
import net.bdew.gendustry.config.Items

class GeneRecipe extends IRecipe {
  def matches(inv: InventoryCrafting, world: World): Boolean = getCraftingResult(inv) != null
  def getCraftingResult(inv: InventoryCrafting): ItemStack = {
    var template: ItemStack = null
    var samples = Seq.empty[GeneSampleInfo]
    for (i <- 0 until 3; j <- 0 until 3) {
      val itm = inv.getStackInRowAndColumn(i, j)
      if (itm != null && itm.getItem.isInstanceOf[GeneSample] && itm.hasTagCompound)
        samples :+= Items.geneSample.getInfo(itm)
      else if (itm != null && itm.getItem.isInstanceOf[GeneTemplate] && template == null)
        template = itm
      else if (itm != null)
        return null
    }
    if (samples.isEmpty || template == null) return null
    val out = template.copy()
    for (s <- samples) {
      if (!Items.geneTemplate.addSample(out, s)) return null
    }
    return out
  }
  def getRecipeSize: Int = 9
  def getRecipeOutput: ItemStack = null
}
