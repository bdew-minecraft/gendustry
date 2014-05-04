/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.custom

import net.bdew.lib.items.SimpleItem
import net.minecraft.client.renderer.texture.IconRegister
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.item.ItemStack
import net.bdew.gendustry.Gendustry
import net.minecraft.util.Icon
import net.bdew.gendustry.config.Tuning
import net.bdew.lib.recipes.gencfg.ConfigSection
import net.minecraft.creativetab.CreativeTabs
import java.util
import cpw.mods.fml.common.registry.GameRegistry

class CustomHoneyComb(id: Int) extends SimpleItem(id, "HoneyComb") {

  case class CombInfo(name: String, color1: Int, color2: Int)

  var icons: Array[Icon] = null

  setHasSubtypes(true)
  setMaxDamage(-1)

  override def requiresMultipleRenderPasses() = true

  val data = (Tuning.getOrAddSection("HoneyCombs").filterType(classOf[ConfigSection]) map {
    case (ident, cfg) => cfg.getInt("ID") -> CombInfo(
      ident,
      cfg.getColor("PrimaryColor").asRGB,
      cfg.getColor("SecondaryColor").asRGB
    )
  }).toMap

  for ((id, comb) <- data)
    GameRegistry.registerCustomItemStack("HoneyComb." + comb.name, new ItemStack(this, 1, id))

  def getData(stack: ItemStack) = data.get(stack.getItemDamage)

  override def getIconFromDamageForRenderPass(damage: Int, pass: Int) = pass match {
    case 0 => icons(1)
    case _ => icons(0)
  }

  override def getColorFromItemStack(stack: ItemStack, pass: Int): Int = {
    val data = getData(stack).getOrElse(return 0)
    pass match {
      case 0 => data.color1
      case _ => data.color2
    }
  }

  override def getSubItems(par1: Int, par2CreativeTabs: CreativeTabs, list: util.List[_]) {
    val l = list.asInstanceOf[util.List[ItemStack]]
    for ((id, name) <- data)
      l.add(new ItemStack(this, 1, id))
  }

  override def getUnlocalizedName(stack: ItemStack) =
    getData(stack).map(x => "%s.honeycomb.%s".format(Gendustry.modId, x.name)).getOrElse("invalid")

  @SideOnly(Side.CLIENT)
  override def registerIcons(reg: IconRegister) {
    icons = Array(
      reg.registerIcon("forestry:beeCombs.0"),
      reg.registerIcon("forestry:beeCombs.1")
    )
  }
}