/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.custom.hives

import java.util

import forestry.api.apiculture._
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

case class HiveDrop(chance: Double, species: IAlleleBeeSpecies, ignobleShare: Double, additional: List[ItemStack]) extends IHiveDrop {
  val beeRoot = BeeManager.beeRoot
  val tpl = beeRoot.getTemplate(species.getUID)
  val individual = beeRoot.templateAsIndividual(tpl)

  override def getBeeType(world: IBlockAccess, pos: BlockPos): IBee = individual
  override def getIgnobleChance(world: IBlockAccess, pos: BlockPos, fortune: Int): Double = ignobleShare
  override def getChance(world: IBlockAccess, pos: BlockPos, fortune: Int): Double = chance

  override def getExtraItems(world: IBlockAccess, pos: BlockPos, fortune: Int): util.Collection[ItemStack] = {
    import scala.collection.JavaConversions._
    additional.map(_.copy())
  }
}