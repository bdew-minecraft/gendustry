/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.machines.apiary.upgrades

import net.minecraft.item.{ItemStack, Item}
import net.bdew.gendustry.api.{ApiaryModifiers, IApiaryUpgrade}
import net.minecraft.client.renderer.texture.IIconRegister
import net.bdew.gendustry.Gendustry
import net.minecraft.util.IIcon
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.creativetab.CreativeTabs
import java.util
import net.minecraft.entity.player.EntityPlayer
import net.bdew.lib.Misc
import net.bdew.lib.items.NamedItem

object ItemApiaryUpgrade extends Item with IApiaryUpgrade with NamedItem {
  var icons = Map.empty[Int, IIcon]
  def name = "ApiaryUpgrade"

  setHasSubtypes(true)
  setMaxDamage(-1)
  setUnlocalizedName(Gendustry.modId + ".apiary.upgrade")

  def formatModifier(f: Float, base: Float) = (if (f > base) "+" else "") + "%.0f".format((f - base) * 100) + "%"

  override def addInformation(stack: ItemStack, player: EntityPlayer, list: util.List[_], par4: Boolean) {
    if (!Upgrades.map.contains(stack.getItemDamage)) return
    val upgrade = Upgrades.map(stack.getItemDamage)
    val l = list.asInstanceOf[util.List[String]]
    val mods = new ApiaryModifiers

    upgrade.mod(mods, 1)

    l.add(Misc.toLocal(Gendustry.modId + ".label.maxinstall") + " " + upgrade.maxNum.toString)

    if (mods.isAutomated)
      l.add(Misc.toLocal(Gendustry.modId + ".label.mod.automated"))
    if (mods.biomeOverride != null)
      l.add(Misc.toLocalF(Gendustry.modId + ".label.mod.biome", mods.biomeOverride.biomeName))
    if (mods.isSealed)
      l.add(Misc.toLocal(Gendustry.modId + ".label.mod.sealed"))
    if (mods.isSelfLighted)
      l.add(Misc.toLocal(Gendustry.modId + ".label.mod.selflighted"))
    if (mods.isSunlightSimulated)
      l.add(Misc.toLocal(Gendustry.modId + ".label.mod.sky"))
    if (mods.isCollectingPollen)
      l.add(Misc.toLocal(Gendustry.modId + ".label.mod.sieve"))

    if (mods.lifespan != 1)
      l.add(Misc.toLocal(Gendustry.modId + ".label.mod.lifespan") + " " + formatModifier(mods.lifespan, 1))
    if (mods.flowering != 1)
      l.add(Misc.toLocal(Gendustry.modId + ".label.mod.flowering") + " " + formatModifier(mods.flowering, 1))
    if (mods.geneticDecay != 1)
      l.add(Misc.toLocal(Gendustry.modId + ".label.mod.geneticDecay") + " " + formatModifier(mods.geneticDecay, 1))
    if (mods.mutation != 1)
      l.add(Misc.toLocal(Gendustry.modId + ".label.mod.mutation") + " " + formatModifier(mods.mutation, 1))
    if (mods.production != 1)
      l.add(Misc.toLocal(Gendustry.modId + ".label.mod.production") + " " + formatModifier(mods.production, 1))
    if (mods.territory != 1)
      l.add(Misc.toLocal(Gendustry.modId + ".label.mod.territory") + " " + formatModifier(mods.territory, 1))

    if (mods.humidity != 0)
      l.add(Misc.toLocal(Gendustry.modId + ".label.mod.humidity") + " " + formatModifier(mods.humidity, 0))
    if (mods.temperature != 0)
      l.add(Misc.toLocal(Gendustry.modId + ".label.mod.temperature") + " " + formatModifier(mods.temperature, 0))

    if (mods.energy != 1)
      l.add(Misc.toLocal(Gendustry.modId + ".label.mod.energy") + " " + formatModifier(mods.energy, 1))

  }

  def getStackingId(stack: ItemStack) = Item.getIdFromItem(this) * Int.MaxValue + stack.getItemDamage
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

  override def getIconFromDamage(meta: Int): IIcon = {
    if (icons.contains(meta))
      return icons(meta)
    return null
  }

  override def getUnlocalizedName(stack: ItemStack): String = {
    if (Upgrades.map.contains(stack.getItemDamage))
      return "%s.upgrades.%s".format(Gendustry.modId, Upgrades.map(stack.getItemDamage).name)
    return "invalid"
  }

  override def getSubItems(par1: Item, par2CreativeTabs: CreativeTabs, list: util.List[_]) {
    val l = list.asInstanceOf[util.List[ItemStack]]
    for ((id, name) <- Upgrades.map)
      l.add(new ItemStack(this, 1, id))
  }

  @SideOnly(Side.CLIENT)
  override def registerIcons(reg: IIconRegister) {
    icons = Upgrades.map.map({
      case (id, upgrade) => id -> reg.registerIcon("%s:upgrades/%s".format(Gendustry.modId, upgrade.name))
    }).toMap
  }
}
