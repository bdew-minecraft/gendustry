/*
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.machines.apiary.upgrades

import net.bdew.gendustry.api.ApiaryModifiers
import net.bdew.gendustry.config.TuningLoader._
import net.bdew.gendustry.config.{Items, Tuning}
import net.bdew.lib.recipes.gencfg.{EntryStr, ConfigSection}
import cpw.mods.fml.common.registry.GameRegistry
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

  def makeMod(upg: String, n: String, e: EntryModifier): ModFunc = {
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

  def makeBoolMod(upg: String, n: String, b: Boolean): ModFunc = n match {
    case "sealed" => (a, n) => a.isSealed = b
    case "selfLighted" => (a, n) => a.isSelfLighted = b
    case "sunlightSimulated" => (a, n) => a.isSunlightSimulated = b
    case "hellish" => (a, n) => a.isHellish = b
    case "automated" => (a, n) => a.isAutomated = b
    case "collectingPollen" => (a, n) => a.isCollectingPollen = b
    case x => sys.error("Unknown boolean upgrade modifier '%s' in upgrade '%s'".format(x, upg))
  }

  def init() {
    for ((name, sect) <- Tuning.getSection("Upgrades").filterType(classOf[ConfigSection])) {
      val id = sect.getInt("id")
      val max = sect.getInt("max")
      val mods = sect.flatMap({
        case (pname, EntryStr(_)) => Some(makeBoolMod(name, pname, sect.getBoolean(pname)))
        case (pname, x: EntryModifier) => Some(makeMod(name, pname, x))
        case ("id", _) | ("max", _) => None
        case (pname, v) => sys.error("Unknown upgrade modifier '%s' - %s in upgrade '%s'".format(pname, v, name))
      })
      map += id -> Upgrade(id, name, max, mods.toSeq)
      GameRegistry.registerCustomItemStack("upgrade." + name, new ItemStack(Items.upgradeItem, 1, id))
    }
  }
}
