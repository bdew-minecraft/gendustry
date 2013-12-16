/*
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.nei

import forestry.api.genetics._
import net.bdew.lib.Misc
import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.forestry.GeneSampleInfo

object NEICache {
  var speciesResultMutations = collection.mutable.Map.empty[IAlleleSpecies, List[IMutation]].withDefault(x => List.empty[IMutation])
  var speciesUsedMutations = collection.mutable.Map.empty[IAlleleSpecies, List[IMutation]].withDefault(x => List.empty[IMutation])
  var speciesChromosomes = collection.mutable.Map.empty[GeneSampleInfo, List[IAlleleSpecies]].withDefault(x => List.empty[IAlleleSpecies])

  def load() {
    import scala.collection.JavaConversions._

    Gendustry.logInfo("Preparing genetics cache ...")

    for ((_, root) <- AlleleManager.alleleRegistry.getSpeciesRoot; mutation <- root.getMutations(false)) {
      speciesResultMutations(mutation.getTemplate.apply(0).asInstanceOf[IAlleleSpecies]) :+= mutation
      speciesUsedMutations(mutation.getAllele0.asInstanceOf[IAlleleSpecies]) :+= mutation
      speciesUsedMutations(mutation.getAllele1.asInstanceOf[IAlleleSpecies]) :+= mutation
    }

    Gendustry.logInfo("%d mutation outputs", speciesResultMutations.size)
    Gendustry.logInfo("%d mutation inputs", speciesUsedMutations.size)

    for (species <- Misc.filterType(AlleleManager.alleleRegistry.getRegisteredAlleles.values(), classOf[IAlleleSpecies])) {
      for ((allele, chromosome) <- species.getRoot.getTemplate(species.getUID).zipWithIndex) {
        if (allele != null && !AlleleManager.alleleRegistry.isBlacklisted(allele.getUID))
          speciesChromosomes(GeneSampleInfo(species.getRoot, chromosome, allele)) :+= species
      }
    }

    Gendustry.logInfo("%d chromosomes", speciesChromosomes.size)

    Gendustry.logInfo("Done")

  }
}
