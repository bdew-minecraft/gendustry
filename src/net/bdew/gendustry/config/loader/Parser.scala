/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.config.loader

import net.bdew.lib.recipes.RecipeParser
import net.bdew.lib.recipes.gencfg.{CfgVal, GenericConfigParser}
import net.bdew.lib.recipes.lootlist.LootListParser

class Parser extends RecipeParser with GenericConfigParser with LootListParser {
  override def delayedStatement = mutagen | dna | protein | assembly | stMutation | centrifuge | squeezer | regOreDict | super.delayedStatement

  def regOreDict = "regOreDict" ~> ":" ~> spec ~ "@WILDCARD".? ~ ("->" ~> str) ^^ {
    case spec ~ wildcard ~ id => StRegOredict(id, spec, wildcard.isDefined)
  }

  // === Machine Recipes ===

  def mutagen = "mutagen" ~> ":" ~> spec ~ ("->" ~> int) ^^ {
    case sp ~ n => StMutagen(sp, n)
  }

  def dna = "dna" ~> ":" ~> spec ~ ("->" ~> int) ^^ {
    case sp ~ n => StLiquidDNA(sp, n)
  }

  def protein = "protein" ~> ":" ~> spec ~ ("->" ~> int) ^^ {
    case sp ~ n => StProtein(sp, n)
  }

  def charWithCount = recipeChar ~ ("*" ~> int).? ^^ {
    case ch ~ cnt => (ch, cnt.getOrElse(1))
  }

  def assembly = "assembly" ~> ":" ~> (charWithCount <~ ",").+ ~ (int <~ "mj") ~ ("=>" ~> specWithCount) ^^ {
    case r ~ p ~ (s ~ n) => StAssembly(r, p, s, n.getOrElse(1))
  }

  def oneOrManyDrops = (
    dropsEntry ^^ { case d => List(d) }
      | spec ^^ { case st => List((100, st)) }
      | ("{" ~> dropsEntry.+ <~ "}")
    )

  def fluidSpec = str ~ (wholeNumber <~ "mb") ^^ { case id ~ amount => FluidSpec(id, amount.toInt) }

  def squeezer = "squeezer" ~> ":" ~> spec ~ ("," ~> wholeNumber <~ "cycles") ~ ("=>" ~> fluidSpec) ~ ("+" ~> dropsEntry).? ^^ {
    case stack ~ ticks ~ fluid ~ Some((chance, res)) => StSqueezer(stack, fluid, ticks.toInt, res, chance)
    case stack ~ ticks ~ fluid ~ None => StSqueezer(stack, fluid, ticks.toInt, null, 0)
  }

  def centrifuge = "centrifuge" ~> ":" ~> spec ~ ("," ~> wholeNumber <~ "cycles") ~ ("=>" ~> oneOrManyDrops) ^^ {
    case stack ~ ticks ~ drops => StCentrifuge(stack, drops, ticks.toInt)
  }

  // === Mutations ===

  def mrTemperature = "Req" ~> "Temperature" ~> ident ^^ MReqTemperature
  def mrHumidity = "Req" ~> "Humidity" ~> ident ^^ MReqHumidity

  def mutationReq = mrTemperature | mrHumidity

  def stMutation = "secret".? ~ ("mutation" ~> ":") ~ (decimalNumber <~ "%") ~ str ~ "+" ~ str ~ "=" ~ str ~ mutationReq.* ^^ {
    case secret ~ mutation ~ chance ~ p1 ~ plus ~ p2 ~ eq ~ res ~ req =>
      StMutation(parent1 = p1, parent2 = p2, result = res,
        chance = chance.toFloat, secret = secret.isDefined, requirements = req)
  }

  // === Apiary Modifiers ===

  override def cfgStatement = cfgAdd | cfgMul | cfgSub | cfgDiv | super.cfgStatement

  def cfgAdd = ident ~ ("+" ~> "=" ~> decimalNumber) ^^ { case id ~ n => CfgVal(id, EntryModifierAdd(n.toFloat)) }
  def cfgMul = ident ~ ("*" ~> "=" ~> decimalNumber) ^^ { case id ~ n => CfgVal(id, EntryModifierMul(n.toFloat)) }
  def cfgSub = ident ~ ("-" ~> "=" ~> decimalNumber) ^^ { case id ~ n => CfgVal(id, EntryModifierSub(n.toFloat)) }
  def cfgDiv = ident ~ ("/" ~> "=" ~> decimalNumber) ^^ { case id ~ n => CfgVal(id, EntryModifierDiv(n.toFloat)) }
}
