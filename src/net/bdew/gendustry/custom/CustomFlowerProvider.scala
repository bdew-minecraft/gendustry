/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.custom

import forestry.api.apiculture.FlowerManager
import forestry.api.genetics.{IFlowerProvider, IIndividual, IPollinatable}
import net.bdew.lib.Misc
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.common.EnumPlantType

case class CustomFlowerProvider(flowerType: String, name: String) extends IFlowerProvider {
  def getFlowerType = flowerType

  def isAcceptedPollinatable(world: World, pollinatable: IPollinatable) = {
    val plantTypes = pollinatable.getPlantType
    plantTypes.size > 1 || !plantTypes.contains(EnumPlantType.Nether)
  }

  def growFlower(world: World, individual: IIndividual, x: Int, y: Int, z: Int) =
    FlowerManager.flowerRegistry.growFlower(flowerType, world, individual, x, y, z)

  def getDescription: String =
    Misc.toLocal("gendustry.allele.flowers." + name)

  def affectProducts(world: World, individual: IIndividual, x: Int, y: Int, z: Int, products: Array[ItemStack]): Array[ItemStack] =
    products

  def getFlowers = FlowerManager.flowerRegistry.getAcceptableFlowers(flowerType)
}