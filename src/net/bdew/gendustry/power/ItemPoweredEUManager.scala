/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.power

import ic2.api.item.{ElectricItem, IElectricItemManager}
import net.bdew.gendustry.config.Tuning
import net.bdew.lib.{DecFormat, Misc}
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack

class ItemPoweredEUManager(item: ItemPoweredEU) extends IElectricItemManager {
  private lazy val ratio = Tuning.getSection("Power").getFloat("EU_MJ_Ratio")

  override def charge(itemStack: ItemStack, amount: Double, tier: Int, ignoreTransferLimit: Boolean, simulate: Boolean): Double = {
    if (amount == 1D && simulate && item.getCharge(itemStack) < item.maxCharge) return 1D // Workaround for IC2Classic slot checking
    val charge = item.getCharge(itemStack)
    val canCharge = Misc.clamp(item.maxCharge.toDouble - charge, 0.0, amount / ratio).floor.toInt
    if (!simulate) item.setCharge(itemStack, charge + canCharge)
    return (canCharge * ratio).round
  }

  override def discharge(itemStack: ItemStack, amount: Double, tier: Int, ignoreTransferLimit: Boolean, externally: Boolean, simulate: Boolean) = 0
  override def getCharge(itemStack: ItemStack) = (item.getCharge(itemStack) * ratio).round

  override def getTier(itemStack: ItemStack): Int = 2

  override def getMaxCharge(itemStack: ItemStack): Double =
    item.maxCharge.toDouble * ratio

  override def use(itemStack: ItemStack, amount: Double, entity: EntityLivingBase) = false
  override def canUse(itemStack: ItemStack, amount: Double) = false

  override def chargeFromArmor(itemStack: ItemStack, entity: EntityLivingBase): Unit = {
    import scala.collection.JavaConversions._
    if (!entity.isInstanceOf[EntityPlayer]) return
    val player = entity.asInstanceOf[EntityPlayer]
    var charged = false
    for (stack <- player.getArmorInventoryList if stack != null) {
      val tier = ElectricItem.manager.getTier(stack)
      val needCharge = (item.maxCharge - item.getCharge(itemStack)).toDouble * ratio
      val hasCharge = Misc.clamp(ElectricItem.manager.discharge(stack, needCharge, tier, true, true, true), 0D, needCharge)
      if (hasCharge > 0) {
        ElectricItem.manager.discharge(stack, hasCharge, tier, true, true, false)
        item.setCharge(itemStack, item.getCharge(itemStack) + (hasCharge / ratio).round.toInt)
        charged = true
      }
    }
    if (charged)
      entity.asInstanceOf[EntityPlayer].inventoryContainer.detectAndSendChanges()
  }

  override def getToolTip(itemStack: ItemStack) =
    DecFormat.round(getCharge(itemStack)) + " / " + DecFormat.round(getMaxCharge(itemStack)) + " EU"
}
