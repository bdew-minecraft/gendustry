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

  implicit object MutationOrdering extends Ordering[IMutation] {
    def compare(x: IMutation, y: IMutation): Int =
      if (x.getRoot != y.getRoot)
        x.getRoot.getUID.compareTo(y.getRoot.getUID)
      else if (x.getTemplate.apply(0).getUID != y.getTemplate.apply(0).getUID)
        x.getTemplate.apply(0).getUID.compareTo(y.getTemplate.apply(0).getUID)
      else if (x.getAllele0 != y.getAllele0)
        x.getAllele0.getUID.compareTo(y.getAllele0.getUID)
      else
        x.getAllele1.getUID.compareTo(y.getAllele1.getUID)
  }

  implicit object SpeciesOrdering extends Ordering[IAlleleSpecies] {
    def compare(x: IAlleleSpecies, y: IAlleleSpecies): Int =
      if (x.getRoot != y.getRoot)
        x.getRoot.getUID.compareTo(y.getRoot.getUID)
      else
        x.getUID.compareTo(y.getUID)
  }

  implicit object SampleOrdering extends Ordering[GeneSampleInfo] {
    def compare(x: GeneSampleInfo, y: GeneSampleInfo): Int =
      if (x.root != y.root)
        x.root.getUID.compareTo(y.root.getUID)
      else if (x.chromosome != y.chromosome)
        x.chromosome.compareTo(y.chromosome)
      else
        x.allele.getUID.compareTo(y.allele.getUID)
  }

  val speciesResultMutations = collection.mutable.Map.empty[IAlleleSpecies, collection.SortedSet[IMutation]].withDefault(x => collection.SortedSet.empty[IMutation])
  val speciesUsedMutations = collection.mutable.Map.empty[IAlleleSpecies, collection.SortedSet[IMutation]].withDefault(x => collection.SortedSet.empty[IMutation])
  val speciesChromosomes = collection.mutable.Map.empty[GeneSampleInfo, collection.SortedSet[IAlleleSpecies]].withDefault(x => collection.SortedSet.empty[IAlleleSpecies])
  var geneSamples = collection.SortedSet.empty[GeneSampleInfo]

  def load() {
    import scala.collection.JavaConversions._

    Gendustry.logInfo("Preparing genetics cache ...")

    for ((_, root) <- AlleleManager.alleleRegistry.getSpeciesRoot; mutation <- root.getMutations(false)) {
      speciesResultMutations(mutation.getTemplate.apply(0).asInstanceOf[IAlleleSpecies]) += mutation
      speciesUsedMutations(mutation.getAllele0.asInstanceOf[IAlleleSpecies]) += mutation
      speciesUsedMutations(mutation.getAllele1.asInstanceOf[IAlleleSpecies]) += mutation
    }

    Gendustry.logInfo("%d mutation outputs", speciesResultMutations.size)
    Gendustry.logInfo("%d mutation inputs", speciesUsedMutations.size)

    for (species <- Misc.filterType(AlleleManager.alleleRegistry.getRegisteredAlleles.values(), classOf[IAlleleSpecies])) {
      for ((allele, chromosome) <- species.getRoot.getTemplate(species.getUID).zipWithIndex) {
        if (allele != null && !AlleleManager.alleleRegistry.isBlacklisted(allele.getUID))
          speciesChromosomes(GeneSampleInfo(species.getRoot, chromosome, allele)) += species
      }
    }

    geneSamples ++= speciesChromosomes.keys

    Gendustry.logInfo("%d chromosomes", speciesChromosomes.size)
    Gendustry.logInfo("Done")
  }
}
