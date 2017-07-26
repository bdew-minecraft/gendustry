/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.forestry

import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.items.{GeneSample, GeneTemplate}
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.util.{NonNullList, ResourceLocation}
import net.minecraft.world.World
import net.minecraftforge.registries.IForgeRegistryEntry

class GeneRecipe extends IForgeRegistryEntry.Impl[IRecipe] with IRecipe {
  setRegistryName(new ResourceLocation(Gendustry.modId, "gene"))

  override def matches(inv: InventoryCrafting, world: World): Boolean = !getCraftingResult(inv).isEmpty
  override def getCraftingResult(inv: InventoryCrafting): ItemStack = {
    var template: ItemStack = ItemStack.EMPTY
    var samples = Seq.empty[GeneSampleInfo]
    for (i <- 0 until inv.getWidth; j <- 0 until inv.getHeight) {
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

  override def canFit(width: Int, height: Int): Boolean = width > 1 || height > 1

  override def getRecipeOutput: ItemStack = ItemStack.EMPTY
}
