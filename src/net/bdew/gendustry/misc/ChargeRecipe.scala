/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.misc

import net.bdew.gendustry.config.Tuning
import net.bdew.gendustry.power.ItemPowered
import net.minecraft.init.{Blocks, Items}
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.crafting.IRecipe
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.world.World

class ChargeRecipe extends IRecipe {
  lazy val redstoneValue = Tuning.getSection("Power").getSection("RedstoneCharging").getInt("RedstoneValue")

  def matches(inv: InventoryCrafting, world: World): Boolean = getCraftingResult(inv) != null
  def getCraftingResult(inv: InventoryCrafting): ItemStack = {
    var tool: ItemStack = null
    var redstone = 0
    for (i <- 0 until 3; j <- 0 until 3; stack <- Option(inv.getStackInRowAndColumn(i, j))) {
      if (stack.getItem.isInstanceOf[ItemPowered])
        tool = stack
      else if (stack.getItem == Items.REDSTONE)
        redstone += 1
      else if (stack.getItem == Item.getItemFromBlock(Blocks.REDSTONE_BLOCK))
        redstone += 9
    }

    if (tool == null || redstone == 0) return null

    val item = tool.getItem.asInstanceOf[ItemPowered]
    val charge = item.getCharge(tool)

    if ((item.maxCharge - charge) >= redstone * redstoneValue) {
      item.stackWithCharge(charge + (redstone * redstoneValue))
    } else null
  }

  override def getRemainingItems(inv: InventoryCrafting): Array[ItemStack] =
    new Array[ItemStack](inv.getSizeInventory)

  def getRecipeSize: Int = 9
  def getRecipeOutput: ItemStack = new ItemStack(Blocks.FIRE)
}
