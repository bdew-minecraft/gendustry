/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.machines.apiary.upgrades

import cpw.mods.fml.common.registry.GameRegistry
import net.bdew.gendustry.api.ApiaryModifiers
import net.bdew.gendustry.config.Tuning
import net.bdew.gendustry.config.loader._
import net.bdew.lib.Misc
import net.bdew.lib.recipes.gencfg.{ConfigSection, EntryStr}
import net.minecraft.item.ItemStack

object Upgrades {
  val map = collection.mutable.Map.empty[Int, Upgrade]
  type ModFunc = (ApiaryModifiers, Int) => Unit

  case class Upgrade(id: Int, name: String, maxNum: Int, mods: Seq[(ApiaryModifiers, Int) => Unit]) {
    def mod(v: ApiaryModifiers, num: Int) {
      val n = if (num > maxNum) maxNum else num
      mods.foreach(_(v, n))
    }
  }

  def makeNumMod(upg: String, n: String, e: EntryModifier): ModFunc = {
    val calc: (Float, Int) => Float = e match {
      case EntryModifierAdd(v) => (x, n) => x + v * n
      case EntryModifierSub(v) => (x, n) => x - v * n
      case EntryModifierMul(v) => (x, n) => x * math.pow(v, n).toFloat
      case EntryModifierDiv(v) => (x, n) => x / math.pow(v, n).toFloat
    }
    n match {
      case "lifespan" => (a, n) => a.lifespan = calc(a.lifespan, n)
      case "territory" => (a, n) => a.territory = calc(a.territory, n)
      case "mutation" => (a, n) => a.mutation = calc(a.mutation, n)
      case "production" => (a, n) => a.production = calc(a.production, n)
      case "flowering" => (a, n) => a.flowering = calc(a.flowering, n)
      case "geneticDecay" => (a, n) => a.geneticDecay = calc(a.geneticDecay, n)
      case "energy" => (a, n) => a.energy = calc(a.energy, n)
      case "temperature" => (a, n) => a.temperature = calc(a.temperature, n)
      case "humidity" => (a, n) => a.humidity = calc(a.humidity, n)
      case x => sys.error("Unknown numeric upgrade modifier '%s' in upgrade '%s'".format(x, upg))
    }
  }

  def str2bool(s: String) = Tuning.trueVals.contains(s.toLowerCase)

  def makeStrMod(upg: String, n: String, v: String): ModFunc = n match {
    case "sealed" => (a, n) => a.isSealed = str2bool(v)
    case "selfLighted" => (a, n) => a.isSelfLighted = str2bool(v)
    case "sunlightSimulated" => (a, n) => a.isSunlightSimulated = str2bool(v)
    case "automated" => (a, n) => a.isAutomated = str2bool(v)
    case "collectingPollen" => (a, n) => a.isCollectingPollen = str2bool(v)
    case "biomeOverride" => (a, n) => a.biomeOverride = Misc.getBiomeByName(v)
    case x => sys.error("Unknown string upgrade modifier '%s' in upgrade '%s'".format(x, upg))
  }

  def init() {
    for ((name, sect) <- Tuning.getSection("Upgrades").filterType(classOf[ConfigSection])) {
      val id = sect.getInt("id")
      val max = sect.getInt("max")
      val mods = sect.flatMap({
        case (pName, EntryStr(v)) => Some(makeStrMod(name, pName, v))
        case (pName, x: EntryModifier) => Some(makeNumMod(name, pName, x))
        case ("id", _) | ("max", _) => None
        case (pName, v) => sys.error("Unknown upgrade modifier '%s' - %s in upgrade '%s'".format(pName, v, name))
      })
      map += id -> Upgrade(id, name, max, mods.toSeq)
      GameRegistry.registerCustomItemStack("upgrade." + name, new ItemStack(ItemApiaryUpgrade, 1, id))
    }
  }
}
