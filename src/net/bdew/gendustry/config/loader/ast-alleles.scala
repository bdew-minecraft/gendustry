/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.config.loader

import net.bdew.lib.recipes.{ConfigStatement, StackRef}

case class CSFlowerAllele(id: String, definition: List[FlowerAlleleDefStatement]) extends ConfigStatement

trait FlowerAlleleDefStatement

case class FADAccepts(accepts: List[StackRef]) extends FlowerAlleleDefStatement

case class FADSpread(block: StackRef, weight: Double) extends FlowerAlleleDefStatement

case class FADDominant(dominant: Boolean) extends FlowerAlleleDefStatement

sealed trait FAPlantType extends FlowerAlleleDefStatement

case object FAPlantTypeAny extends FAPlantType

case object FAPlantTypeNormal extends FAPlantType

case class FAPlantTypeCustom(kinds: Set[String]) extends FAPlantType