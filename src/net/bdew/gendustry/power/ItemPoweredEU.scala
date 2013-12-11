/*
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.power

import ic2.api.item.{ElectricItem, IElectricItemManager, ISpecialElectricItem}
import net.minecraft.item.ItemStack
import net.bdew.gendustry.config.Tuning
import net.minecraft.entity.EntityLivingBase
import net.bdew.gendustry.compat.PowerProxy
import net.bdew.lib.Misc
import cpw.mods.fml.common.Optional

@Optional.Interface(modid = PowerProxy.IC2_MOD_ID, iface = "ic2.api.energy.tile.ISpecialElectricItem")
trait ItemPoweredEU extends ItemPoweredBase with ISpecialElectricItem {
  private lazy val ratio = Tuning.getSection("Power").getFloat("EU_MJ_Ratio")
  private lazy val manager = new ItemPoweredEUManager(this)

  override def useCharge(stack: ItemStack, uses: Int, player: EntityLivingBase) = {
    if (PowerProxy.haveIC2)
      ElectricItem.rawManager.chargeFromArmor(stack, player)
    super.useCharge(stack, uses, player)
  }

  def canProvideEnergy(itemStack: ItemStack) = false
  def getChargedItemId(itemStack: ItemStack) = itemID
  def getEmptyItemId(itemStack: ItemStack) = itemID
  def getMaxCharge(itemStack: ItemStack) = (maxCharge * ratio).round
  def getTier(itemStack: ItemStack) = 2
  def getTransferLimit(itemStack: ItemStack) = 2048
  def getManager(itemStack: ItemStack) = manager
}

class ItemPoweredEUManager(item: ItemPoweredEU) extends IElectricItemManager {
  private lazy val ratio = Tuning.getSection("Power").getFloat("EU_MJ_Ratio")

  def charge(itemStack: ItemStack, amount: Int, tier: Int, ignoreTransferLimit: Boolean, simulate: Boolean): Int = {
    val charge = item.getCharge(itemStack)
    val canCharge = Misc.clamp(item.maxCharge.toFloat - charge, 0F, amount / ratio).floor.toInt
    if (!simulate) item.setCharge(itemStack, charge + canCharge)
    return (canCharge * ratio).round
  }

  def discharge(itemStack: ItemStack, amount: Int, tier: Int, ignoreTransferLimit: Boolean, simulate: Boolean) = 0
  def getCharge(itemStack: ItemStack) = (item.getCharge(itemStack) * ratio).round.toInt

  def use(itemStack: ItemStack, amount: Int, entity: EntityLivingBase) =
    ElectricItem.rawManager.use(itemStack, amount, entity)

  def canUse(itemStack: ItemStack, amount: Int) =
    ElectricItem.rawManager.canUse(itemStack, amount)
  def chargeFromArmor(itemStack: ItemStack, entity: EntityLivingBase) =
    ElectricItem.rawManager.chargeFromArmor(itemStack, entity)
  def getToolTip(itemStack: ItemStack) =
    ElectricItem.rawManager.getToolTip(itemStack)
}