/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.machines.apiary.upgrades

import java.util

import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.api.ApiaryModifiers
import net.bdew.gendustry.api.items.IApiaryUpgrade
import net.bdew.lib.items.BaseItem
import net.bdew.lib.{Client, Misc}
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack}
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

object ItemApiaryUpgrade extends BaseItem("apiary.upgrade") with IApiaryUpgrade {
  setHasSubtypes(true)
  setMaxDamage(-1)

  def formatModifier(f: Float, base: Float) = (if (f > base) "+" else "") + "%.0f".format((f - base) * 100) + "%"

  override def getDisplayName(stack: ItemStack) = Misc.toLocal(getUnlocalizedName(stack))

  override def getDisplayDetails(stack: ItemStack): util.ArrayList[String] = {
    val list = new util.ArrayList[String]()
    if (!Upgrades.map.contains(stack.getItemDamage)) return list
    val upgrade = Upgrades.map(stack.getItemDamage)
    val mods = new ApiaryModifiers

    upgrade.mod(mods, 1)

    list.add(Misc.toLocal(Gendustry.modId + ".label.maxinstall") + " " + upgrade.maxNum.toString)

    if (mods.isAutomated)
      list.add(Misc.toLocal(Gendustry.modId + ".label.mod.automated"))
    if (mods.biomeOverride != null)
      list.add(Misc.toLocalF(Gendustry.modId + ".label.mod.biome", mods.biomeOverride.getBiomeName))
    if (mods.isSealed)
      list.add(Misc.toLocal(Gendustry.modId + ".label.mod.sealed"))
    if (mods.isSelfLighted)
      list.add(Misc.toLocal(Gendustry.modId + ".label.mod.selflighted"))
    if (mods.isSunlightSimulated)
      list.add(Misc.toLocal(Gendustry.modId + ".label.mod.sky"))
    if (mods.isCollectingPollen)
      list.add(Misc.toLocal(Gendustry.modId + ".label.mod.sieve"))

    if (mods.lifespan != 1)
      list.add(Misc.toLocal(Gendustry.modId + ".label.mod.lifespan") + " " + formatModifier(mods.lifespan, 1))
    if (mods.flowering != 1)
      list.add(Misc.toLocal(Gendustry.modId + ".label.mod.flowering") + " " + formatModifier(mods.flowering, 1))
    if (mods.geneticDecay != 1)
      list.add(Misc.toLocal(Gendustry.modId + ".label.mod.geneticDecay") + " " + formatModifier(mods.geneticDecay, 1))
    if (mods.mutation != 1)
      list.add(Misc.toLocal(Gendustry.modId + ".label.mod.mutation") + " " + formatModifier(mods.mutation, 1))
    if (mods.production != 1)
      list.add(Misc.toLocal(Gendustry.modId + ".label.mod.production") + " " + formatModifier(mods.production, 1))
    if (mods.territory != 1)
      list.add(Misc.toLocal(Gendustry.modId + ".label.mod.territory") + " " + formatModifier(mods.territory, 1))

    if (mods.humidity != 0)
      list.add(Misc.toLocal(Gendustry.modId + ".label.mod.humidity") + " " + formatModifier(mods.humidity, 0))
    if (mods.temperature != 0)
      list.add(Misc.toLocal(Gendustry.modId + ".label.mod.temperature") + " " + formatModifier(mods.temperature, 0))

    if (mods.energy != 1)
      list.add(Misc.toLocal(Gendustry.modId + ".label.mod.energy") + " " + formatModifier(mods.energy, 1))

    return list
  }

  def getStackingId(stack: ItemStack) = Item.getIdFromItem(this) * Int.MaxValue + stack.getItemDamage

  override def addInformation(stack: ItemStack, playerIn: EntityPlayer, tooltip: util.List[String], advanced: Boolean): Unit = {
    tooltip.addAll(getDisplayDetails(stack))
  }

  def getMaxNumber(stack: ItemStack): Int = {
    if (Upgrades.map.contains(stack.getItemDamage))
      return Upgrades.map(stack.getItemDamage).maxNum
    return 0
  }

  def applyModifiers(mods: ApiaryModifiers, stack: ItemStack) {
    if (Upgrades.map.contains(stack.getItemDamage)) {
      val upg = Upgrades.map(stack.getItemDamage)
      upg.mod(mods, Misc.min(upg.maxNum, stack.stackSize))
    }
  }

  override def getUnlocalizedName(stack: ItemStack): String = {
    if (Upgrades.map.contains(stack.getItemDamage))
      return "%s.upgrades.%s".format(Gendustry.modId, Upgrades.map(stack.getItemDamage).name)
    return "invalid"
  }

  override def getSubItems(par1: Item, par2CreativeTabs: CreativeTabs, list: util.List[ItemStack]) {
    for ((id, name) <- Upgrades.map)
      list.add(new ItemStack(this, 1, id))
  }

  @SideOnly(Side.CLIENT)
  override def registerItemModels(): Unit = {
    for ((id, upgrade) <- Upgrades.map) {
      val model = "%s:%s/%s".format(Gendustry.modId, "upgrades", upgrade.name)
      ModelLoader.setCustomModelResourceLocation(this, id, new ModelResourceLocation(model, "inventory"))
      Client.minecraft.getRenderItem.getItemModelMesher.register(this, id, new ModelResourceLocation(model, "inventory"))
    }
  }
}
