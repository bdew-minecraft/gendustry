/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.power

import net.bdew.gendustry.config.Tuning
import net.bdew.lib.Misc
import net.bdew.lib.capabilities.{CapabilityProvider, CapabilityProviderItem}
import net.bdew.lib.power.{ItemPoweredBase, TilePoweredBase}
import net.darkhax.tesla.api.{ITeslaConsumer, ITeslaHolder}
import net.minecraft.item.ItemStack
import net.minecraftforge.common.capabilities.{Capability, CapabilityInject}

import scala.annotation.meta.setter

class TeslaConsumerTile(tile: TilePoweredBase) extends ITeslaConsumer with ITeslaHolder {
  override def getStoredPower: Long = (tile.power.stored * Tesla.ratio).floor.toLong
  override def getCapacity: Long = (tile.power.capacity * Tesla.ratio).floor.toLong
  override def givePower(amount: Long, simulated: Boolean): Long =
    (tile.power.inject(amount / Tesla.ratio, simulated) * Tesla.ratio).floor.toLong
}

class TeslaConsumerItem(item: ItemPoweredBase, stack: ItemStack) extends ITeslaConsumer with ITeslaHolder {
  override def getStoredPower: Long = (item.getCharge(stack) * Tesla.ratio).toLong
  override def getCapacity: Long = (item.maxCharge * Tesla.ratio).floor.toLong
  override def givePower(amount: Long, simulated: Boolean): Long = {
    val charge = item.getCharge(stack)
    val canCharge = Misc.clamp(item.maxCharge.toFloat - charge, 0F, amount.toFloat / Tesla.ratio).floor.toInt
    if (!simulated) item.setCharge(stack, charge + canCharge)
    return (canCharge * Tesla.ratio).floor.toLong
  }
}

object Tesla {
  @(CapabilityInject@setter)(classOf[ITeslaConsumer])
  var CONSUMER: Capability[ITeslaConsumer] = null

  @(CapabilityInject@setter)(classOf[ITeslaHolder])
  var HOLDER: Capability[ITeslaHolder] = null

  lazy val ratio = Tuning.getSection("Power").getFloat("T_MJ_Ratio")

  def injectTesla(tile: TilePoweredBase with CapabilityProvider): Unit = {
    val cap = new TeslaConsumerTile(tile)
    tile.addCapability(CONSUMER, cap)
    tile.addCapability(HOLDER, cap)
  }

  def injectTesla(item: ItemPoweredBase with CapabilityProviderItem): Unit = {
    item.addCapability(CONSUMER, s => new TeslaConsumerItem(item, s))
    item.addCapability(HOLDER, s => new TeslaConsumerItem(item, s))
  }
}
