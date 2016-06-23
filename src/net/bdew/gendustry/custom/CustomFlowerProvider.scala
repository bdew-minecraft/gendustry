/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.custom

import forestry.api.genetics.{ICheckPollinatable, IFlowerProvider, IIndividual}
import net.bdew.lib.Misc
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.EnumPlantType

case class CustomFlowerProvider(flowerType: String, name: String) extends IFlowerProvider {
  override def getFlowerType = flowerType

  override def isAcceptedPollinatable(world: World, pollinatable: ICheckPollinatable): Boolean = {
    val plantTypes = pollinatable.getPlantType
    plantTypes.size > 1 || !plantTypes.contains(EnumPlantType.Nether)
  }

  override def getDescription: String =
    Misc.toLocal("gendustry.allele.flowers." + name)

  override def affectProducts(world: World, individual: IIndividual, pos: BlockPos, products: Array[ItemStack]): Array[ItemStack] =
    products
}