/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.custom

import forestry.api.apiculture._
import forestry.api.core.{EnumHumidity, EnumTemperature}
import forestry.api.genetics.{AlleleManager, IAllele}
import net.bdew.gendustry.forestry.BeeModifiers
import net.bdew.lib.Misc
import net.minecraft.block.Block
import net.minecraft.item.ItemStack
import net.minecraft.world.biome.BiomeGenBase

class BeeMutation(parent1: IAlleleBeeSpecies, parent2: IAlleleBeeSpecies, result: IAlleleBeeSpecies, chance: Float) extends IBeeMutation {

  var reqTemperature = Option[EnumTemperature](null)
  var reqHumidity = Option[EnumHumidity](null)
  var reqBlock = Option[Block](null)
  var reqBlockMeta: Option[Int] = None
  var reqBiome = Option[BiomeGenBase](null)

  def reqBiomeId = reqBiome map (_.biomeID)

  // === IBeeMutation ===

  def getBlockUnderHousing(h: IBeeHousing) = {
    val c = h.getCoordinates
    if (c.posY > 0)
      h.getWorld.getBlock(c.posX, c.posY - 1, c.posZ)
    else null
  }

  def getBlockMetaUnderHousing(h: IBeeHousing) = {
    val c = h.getCoordinates
    if (c.posY > 0)
      h.getWorld.getBlockMetadata(c.posX, c.posY - 1, c.posZ)
    else -1
  }

  def testReq[T](req: Option[T], v: T) = req.isEmpty || req.get == v

  override def getChance(housing: IBeeHousing, allele0: IAlleleBeeSpecies, allele1: IAlleleBeeSpecies, genome0: IBeeGenome, genome1: IBeeGenome) =
    if (!((allele0 == parent1 && allele1 == parent2) || (allele0 == parent2 && allele1 == parent1))) 0
    else if (!testReq(reqTemperature, housing.getTemperature)) 0
    else if (!testReq(reqHumidity, housing.getHumidity)) 0
    else if (!testReq(reqBiomeId, housing.getBiome.biomeID)) 0
    else if (!testReq(reqBlock, getBlockUnderHousing(housing))) 0
    else if (!testReq(reqBlockMeta, getBlockMetaUnderHousing(housing))) 0
    else {
      val bkm = getRoot.getBeekeepingMode(housing.getWorld)
      var processedChance = chance
      processedChance *= BeeModifiers.from(housing).getMutationModifier(genome0, genome1, processedChance)
      processedChance *= bkm.getBeeModifier.getMutationModifier(genome0, genome1, processedChance)
      processedChance
    }

  override val getRoot = AlleleManager.alleleRegistry.getSpeciesRoot("rootBees").asInstanceOf[IBeeRoot]

  // === IMutation ===

  var isSecret = false

  override def getPartner(allele: IAllele) =
    if (allele == parent1) parent2
    else if (allele == parent2) parent1
    else null

  override def isPartner(allele: IAllele) =
    allele == parent1 || allele == parent2

  override def getSpecialConditions = {
    import scala.collection.JavaConversions._
    reqTemperature.map(x => Misc.toLocalF("gendustry.req.temperature", x)) ++
      reqHumidity.map(x => Misc.toLocalF("gendustry.req.humidity", x)) ++
      reqBiome.map(x => Misc.toLocalF("gendustry.req.biome", x.biomeName)) ++
      reqBlock.map(x => Misc.toLocalF("gendustry.req.block", new ItemStack(x, 1, reqBlockMeta.getOrElse(0)).getDisplayName))
  }

  override def getBaseChance = chance

  override val getTemplate = getRoot.getTemplate(result.getUID)
  override def getAllele1 = parent1
  override def getAllele0 = parent2
}
