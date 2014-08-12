/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.custom

import forestry.api.apiculture.{IAlleleBeeSpecies, IBeeHousing, IBeeMutation, IBeeRoot}
import forestry.api.core.{EnumHumidity, EnumTemperature}
import forestry.api.genetics.{AlleleManager, IAllele, IGenome}
import net.bdew.lib.Misc

class BeeMutation(parent1: IAlleleBeeSpecies, parent2: IAlleleBeeSpecies, result: IAlleleBeeSpecies, chance: Float) extends IBeeMutation {

  var reqTemperature = Option[EnumTemperature](null)
  var reqHumidity = Option[EnumHumidity](null)

  // === IBeeMutation ===

  override def getChance(housing: IBeeHousing, allele0: IAllele, allele1: IAllele, genome0: IGenome, genome1: IGenome) =
    if (!((allele0 == parent1 && allele1 == parent2) || (allele0 == parent2 && allele1 == parent1))) 0
    else if (reqTemperature.isDefined && reqTemperature.get != housing.getTemperature) 0
    else if (reqHumidity.isDefined && reqHumidity.get != housing.getHumidity) 0
    else chance

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
      reqHumidity.map(x => Misc.toLocalF("gendustry.req.humidity", x))
  }

  override def getBaseChance = chance

  override val getTemplate = getRoot.getTemplate(result.getUID)
  override def getAllele1 = parent1
  override def getAllele0 = parent2
}
