/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.power

import ic2.api.item.{ElectricItem, IElectricItemManager, ISpecialElectricItem}
import net.bdew.gendustry.compat.PowerProxy
import net.bdew.gendustry.config.Tuning
import net.bdew.lib.power.ItemPoweredBase
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.Optional

@Optional.Interface(modid = PowerProxy.IC2_MOD_ID, iface = "ic2.api.item.ISpecialElectricItem")
trait ItemPoweredEU extends ItemPoweredBase with ISpecialElectricItem {
  private lazy val ratio = Tuning.getSection("Power").getFloat("EU_MJ_Ratio")
  private lazy val manager = new ItemPoweredEUManager(this)

  override def useCharge(stack: ItemStack, uses: Int, player: EntityLivingBase) = {
    if (PowerProxy.haveIC2)
      ElectricItem.rawManager.chargeFromArmor(stack, player)
    super.useCharge(stack, uses, player)
  }

  def canProvideEnergy(itemStack: ItemStack) = false
  override def getMaxCharge(itemStack: ItemStack) = (maxCharge * ratio).round
  override def getTier(itemStack: ItemStack) = 2
  override def getTransferLimit(itemStack: ItemStack) = 2048

  @Optional.Method(modid = PowerProxy.IC2_MOD_ID)
  override def getManager(itemStack: ItemStack): IElectricItemManager = manager
}

