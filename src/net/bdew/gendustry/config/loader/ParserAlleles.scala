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

trait ParserAlleles extends RecipeParser with GenericConfigParser {
  private def blocks = spec ~ ("," ~> spec).* ^^ { case sp1 ~ spl => List(sp1) ++ spl }

  private def flowerStatement = (
    "Accepts" ~> blocks ^^ FADAccepts
      | "Spread" ~> spec ~ decimalNumber ^^ { case spec ~ chance => FADSpread(spec, chance.toDouble) }
      | "Dominant" ^^^ FADDominant(true)
      | "Recessive" ^^^ FADDominant(false)
    )

  private def flowerDef = "FlowerAllele" ~> str ~ ("{" ~> flowerStatement.* <~ "}") ^^ { case id ~ statements => CSFlowerAllele(id, statements) }

  override def configStatement = super.configStatement | flowerDef
}
