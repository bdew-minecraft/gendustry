/*
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.nei

import codechicken.nei.api.{API, IConfigureNEI}
import forestry.api.genetics.{IAlleleSpecies, AlleleManager}
import net.bdew.gendustry.config.{Config, Items}
import net.bdew.gendustry.forestry.GeneSampleInfo

class NEIGendustryConfig extends IConfigureNEI {
  def getName: String = "Gendustry"
  def getVersion: String = "@@VERSION@@"

  def addSamples() {
    import scala.collection.JavaConverters._
    val species = AlleleManager.alleleRegistry.getRegisteredAlleles.asScala.values.filter(_.isInstanceOf[IAlleleSpecies])
    val combos = species.map({
      case x: IAlleleSpecies =>
        val root = x.getRoot
        val tpl = root.getTemplate(x.getUID)
        tpl.toIterable.zipWithIndex.filter(x => x._1 != null && !AlleleManager.alleleRegistry.isBlacklisted(x._1.getUID)).map(x => GeneSampleInfo(root, x._2, x._1))
    }).flatten.toSet
    combos.foreach(x => API.addNBTItem(Items.geneSample.newStack(x)))
  }

  def addRecipeHandler(h: BaseRecipeHandler) {
    API.registerRecipeHandler(h)
    API.registerUsageHandler(h)
  }

  def loadConfig() {
    if (Config.neiAddSamples) addSamples()
    addRecipeHandler(new MutagenProducerHandler)
  }
}
