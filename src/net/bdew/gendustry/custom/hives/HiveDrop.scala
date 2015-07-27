/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.custom.hives

import java.util
import java.util.Collections

import forestry.api.apiculture._
import net.bdew.gendustry.compat.ForestryHelper
import net.minecraft.item.ItemStack
import net.minecraft.world.World

case class HiveDrop(chance: Int, species: IAlleleBeeSpecies, ignobleShare: Float, additional: List[ItemStack]) extends IHiveDrop {
  def getMember(kind: EnumBeeType, natural: Boolean) = {
    val beeRoot = ForestryHelper.getRoot("Bees").asInstanceOf[IBeeRoot]
    val tpl = beeRoot.getTemplate(species.getUID)
    val individual = beeRoot.templateAsIndividual(tpl)
    individual.setIsNatural(natural)
    beeRoot.getMemberStack(individual, kind.ordinal())
  }

  override def getPrincess(world: World, x: Int, y: Int, z: Int, fortune: Int): ItemStack = {
    getMember(EnumBeeType.PRINCESS, world.rand.nextFloat() >= ignobleShare)
  }

  override def getDrones(world: World, x: Int, y: Int, z: Int, fortune: Int): util.Collection[ItemStack] = {
    return Collections.singletonList(getMember(EnumBeeType.DRONE, true))
  }

  override def getAdditional(world: World, x: Int, y: Int, z: Int, fortune: Int): util.Collection[ItemStack] = {
    import scala.collection.JavaConversions._
    additional
  }

  override def getChance(world: World, x: Int, y: Int, z: Int): Int = chance
}