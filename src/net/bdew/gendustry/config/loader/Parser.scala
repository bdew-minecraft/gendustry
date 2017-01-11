/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.config.loader

import net.bdew.lib.recipes.RecipeParser
import net.bdew.lib.recipes.gencfg.{CfgVal, GenericConfigParser}
import net.bdew.lib.recipes.lootlist.LootListParser

class Parser extends RecipeParser with GenericConfigParser with LootListParser with ParserHives with ParserAlleles {
  override def recipeStatement = mutagen | dna | protein | assembly | stMutation | centrifuge | centrifugeExtend | squeezer | super.recipeStatement

  // === Machine Recipes ===

  def mutagen = "mutagen" ~> ":" ~> spec ~ ("=>" ~> int <~ "mb") ^^ {
    case sp ~ n => RsMutagen(sp, n)
  }

  def dna = "dna" ~> ":" ~> spec ~ ("=>" ~> int <~ "mb") ^^ {
    case sp ~ n => RsLiquidDNA(sp, n)
  }

  def protein = "protein" ~> ":" ~> spec ~ ("=>" ~> int <~ "mb") ^^ {
    case sp ~ n => RsProtein(sp, n)
  }

  def charWithCount = recipeChar ~ ("*" ~> int).? ^^ {
    case ch ~ cnt => (ch, cnt.getOrElse(1))
  }

  def assembly = "assembly" ~> ":" ~> ("id" ~> "=" ~> str <~ ";") ~ (charWithCount <~ ",").+ ~ (int <~ "mj") ~ ("=>" ~> specWithCount) ^^ {
    case id ~ r ~ p ~ (s ~ n) => RsAssembly(r, id, p, s, n.getOrElse(1))
  }

  def oneOrManyDrops = (
    dropsEntry ^^ (d => List(d))
      | spec ^^ (st => List((100D, st)))
      | ("{" ~> dropsEntry.+ <~ "}")
    )

  def fluidSpec = str ~ (wholeNumber <~ "mb") ^^ { case id ~ amount => FluidSpec(id, amount.toInt) }

  def maybeReplace = "REPLACE".? ^^ { x => if (x.isDefined) RecipeModeReplace else RecipeModeNew }

  def squeezer = "squeezer" ~> ":" ~> maybeReplace ~ spec ~ ("," ~> wholeNumber <~ "cycles") ~ ("=>" ~> fluidSpec) ~ ("+" ~> dropsEntry).? ^^ {
    case mode ~ stack ~ ticks ~ fluid ~ Some((chance, res)) => RsSqueezer(stack, fluid, ticks.toInt, res, chance.round.toInt, mode)
    case mode ~ stack ~ ticks ~ fluid ~ None => RsSqueezer(stack, fluid, ticks.toInt, null, 0, mode)
  }

  def centrifuge = "centrifuge" ~> ":" ~> maybeReplace ~ spec ~ ("," ~> wholeNumber <~ "cycles") ~ ("=>" ~> oneOrManyDrops) ^^ {
    case mode ~ stack ~ ticks ~ drops => RsCentrifuge(stack, drops, ticks.toInt, mode)
  }

  def centrifugeExtend = "centrifuge" ~> ":" ~> "ADD" ~> spec ~ ("=>" ~> oneOrManyDrops) ^^ {
    case stack ~ drops => RsCentrifuge(stack, drops, -1, RecipeModeExtend)
  }

  // === Mutations ===

  def mrTemperature = "Req" ~> "Temperature" ~> ident ^^ MReqTemperature
  def mrHumidity = "Req" ~> "Humidity" ~> ident ^^ MReqHumidity
  def mrBiome = "Req" ~> "Biome" ~> str ^^ MReqBiome
  def mrBlock = "Req" ~> "Block" ~> specBlock ^^ MReqBlock

  def mutationReq = mrTemperature | mrHumidity | mrBiome | mrBlock

  def stMutation = "secret".? ~ ("mutation" ~> ":") ~ (decimalNumber <~ "%") ~ str ~ "+" ~ str ~ "=>" ~ str ~ mutationReq.* ^^ {
    case secret ~ mutation ~ chance ~ p1 ~ plus ~ p2 ~ eq ~ res ~ req =>
      RsMutation(parent1 = p1, parent2 = p2, result = res,
        chance = chance.toFloat, secret = secret.isDefined, requirements = req)
  }

  // === Apiary Modifiers ===

  override def cfgEntry = cfgAdd | cfgMul | cfgSub | cfgDiv | super.cfgEntry

  def cfgAdd = ident ~ ("+" ~> "=" ~> decimalNumber) ^^ { case id ~ n => CfgVal(id, EntryModifierAdd(n.toFloat)) }
  def cfgMul = ident ~ ("*" ~> "=" ~> decimalNumber) ^^ { case id ~ n => CfgVal(id, EntryModifierMul(n.toFloat)) }
  def cfgSub = ident ~ ("-" ~> "=" ~> decimalNumber) ^^ { case id ~ n => CfgVal(id, EntryModifierSub(n.toFloat)) }
  def cfgDiv = ident ~ ("/" ~> "=" ~> decimalNumber) ^^ { case id ~ n => CfgVal(id, EntryModifierDiv(n.toFloat)) }

  // === Conditions ===

  def cndHaveRoot = "HaveForestryModule" ~> str ^^ CndHaveRoot
  override def condition = cndHaveRoot | super.condition

  // === StackRef ===

  def specApiaryUpgrade = "ApiaryUpgrade" ~> ":" ~> str ^^ StackApiaryUpgrade
  def specHoneyComb = "HoneyComb" ~> ":" ~> str ^^ StackHoneyComb
  def specHoneyDrop = "HoneyDrop" ~> ":" ~> str ^^ StackHoneyDrop

  override def spec = specApiaryUpgrade | specHoneyComb | specHoneyDrop | super.spec
}
