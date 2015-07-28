/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.config.loader

import net.bdew.lib.recipes.RecipeParser
import net.bdew.lib.recipes.gencfg.GenericConfigParser

trait ParserHives extends RecipeParser with GenericConfigParser {
  private def blocks = spec ~ ("," ~> spec).* ^^ { case sp1 ~ spl => List(sp1) ++ spl }
  private def strings = str ~ ("," ~> str).* ^^ { case s1 ~ sl => List(s1) ++ sl }
  private def blockFilter = (
    blocks ^^ BlockFilterRef
      | "Air" ^^^ BlockFilterDefAir
      | "Leaves" ^^^ BlockFilterDefLeaves
      | "Replaceable" ^^^ BlockFilterDefReplaceable
    )
  private def intRange = int ~ ("-" ~> int) ^^ { case a ~ b => (a, b) }

  private def hiveDropEntry =
    (int <~ "%") ~ str ~ ("(" ~> int <~ "%" <~ "ignoble" <~ ")").? ~ ("+" ~> spec).* ^^ { case chance ~ uid ~ ignoble ~ drops => HiveDropEntry(chance, uid, ignoble.getOrElse(0) / 100F, drops) }

  private def hiveDefStatement = (
    "YLevel" ~> int ~ "-" ~ int ^^ { case min ~ a ~ max => HDYRange(min, max) }
      | "SpawnIf" ~> condition ^^ HDSpawnIf
      | "SpawnChance" ~> decimalNumber ^^ { x => HDSpawnChance(x.toFloat) }
      | "Biome" ~> strings ^^ HDBiomes
      | "Temperature" ~> strings ^^ HDTemperature
      | "Humidity" ~> strings ^^ HDHumidity
      | "Under" ~> blockFilter ^^ HDLocationUnder
      | "Above" ~> blockFilter ^^ HDLocationAbove
      | "NextTo" ~> blockFilter ^^ HDLocationNextTo
      | "Replace" ~> blockFilter ^^ HDReplace
      | "TopTexture" ~> unescapeStr ^^ HDTopTexture
      | "BottomTexture" ~> unescapeStr ^^ HDBottomTexture
      | "SideTexture" ~> unescapeStr ^^ HDSideTexture
      | "Color" ~> signedNumber ^^ { x => HDColor(x.toInt) }
      | "LightLevel" ~> int ^^ HDLight
      | "Drops" ~> "{" ~> hiveDropEntry.+ <~ "}" ^^ HDDrops
      | "SpawnDebug" ^^^ HDSpawnDebug(true)
    )

  private def hiveDef = "HiveGen" ~> str ~ ("{" ~> hiveDefStatement.* <~ "}") ^^ { case id ~ statements => CSHiveDefinition(id, statements) }

  override def configStatement = super.configStatement | hiveDef
}
