/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.misc

import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.config.Tuning
import net.bdew.gendustry.power.ItemPowered
import net.minecraft.init.{Blocks, Items}
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.crafting.IRecipe
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.util.{NonNullList, ResourceLocation}
import net.minecraft.world.World
import net.minecraftforge.registries.IForgeRegistryEntry

class ChargeRecipe extends IForgeRegistryEntry.Impl[IRecipe] with IRecipe {
  setRegistryName(new ResourceLocation(Gendustry.modId, "charging"))

  lazy val redstoneValue = Tuning.getSection("Power").getSection("RedstoneCharging").getInt("RedstoneValue")

  override def matches(inv: InventoryCrafting, world: World): Boolean = !getCraftingResult(inv).isEmpty
  override def getCraftingResult(inv: InventoryCrafting): ItemStack = {
    var tool = ItemStack.EMPTY
    var redstone = 0
    for (i <- 0 until inv.getWidth; j <- 0 until inv.getHeight) {
      val stack = inv.getStackInRowAndColumn(i, j)
      if (!stack.isEmpty) {
        if (stack.getItem.isInstanceOf[ItemPowered])
          tool = stack
        else if (stack.getItem == Items.REDSTONE)
          redstone += 1
        else if (stack.getItem == Item.getItemFromBlock(Blocks.REDSTONE_BLOCK))
          redstone += 9
        else
          return ItemStack.EMPTY
      }
    }

    if (tool.isEmpty || redstone == 0) return ItemStack.EMPTY

    val item = tool.getItem.asInstanceOf[ItemPowered]
    val charge = item.getCharge(tool)

    if ((item.maxCharge - charge) >= redstone * redstoneValue) {
      item.stackWithCharge(charge + (redstone * redstoneValue))
    } else ItemStack.EMPTY
  }

  override def getRemainingItems(inv: InventoryCrafting): NonNullList[ItemStack] =
    NonNullList.withSize(inv.getSizeInventory, ItemStack.EMPTY)

  override def canFit(width: Int, height: Int): Boolean = width > 1 || height > 1
  override def getRecipeOutput: ItemStack = ItemStack.EMPTY
}
