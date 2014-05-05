/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.power

import ic2.api.item.{ElectricItem, IElectricItemManager}
import net.bdew.gendustry.config.Tuning
import net.minecraft.item.ItemStack
import net.bdew.lib.Misc
import net.minecraft.entity.EntityLivingBase

class ItemPoweredEUManager(item: ItemPoweredEU) extends IElectricItemManager {
  private lazy val ratio = Tuning.getSection("Power").getFloat("EU_MJ_Ratio")

  override def charge(itemStack: ItemStack, amount: Int, tier: Int, ignoreTransferLimit: Boolean, simulate: Boolean): Int = {
    val charge = item.getCharge(itemStack)
    val canCharge = Misc.clamp(item.maxCharge.toFloat - charge, 0F, amount / ratio).floor.toInt
    if (!simulate) item.setCharge(itemStack, charge + canCharge)
    return (canCharge * ratio).round
  }

  override def discharge(itemStack: ItemStack, amount: Int, tier: Int, ignoreTransferLimit: Boolean, simulate: Boolean) = 0
  override def getCharge(itemStack: ItemStack) = (item.getCharge(itemStack) * ratio).round

  override def use(itemStack: ItemStack, amount: Int, entity: EntityLivingBase) =
    ElectricItem.rawManager.use(itemStack, amount, entity)

  override def canUse(itemStack: ItemStack, amount: Int) =
    ElectricItem.rawManager.canUse(itemStack, amount)
  override def chargeFromArmor(itemStack: ItemStack, entity: EntityLivingBase) =
    ElectricItem.rawManager.chargeFromArmor(itemStack, entity)
  override def getToolTip(itemStack: ItemStack) =
    ElectricItem.rawManager.getToolTip(itemStack)
}
