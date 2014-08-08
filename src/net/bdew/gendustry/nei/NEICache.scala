/*
 * Copyright (c) bdew, 2013 - 2014
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

    Gendustry.logInfo("Mutations with multiple results from a single combination:")
    Gendustry.logInfo("(This is not an error, no need to report it to anybody)")

    speciesUsedMutations foreach { case (sp1, mutations) =>
      // First make a list of partner -> result
      val pairs = mutations.toList.map(mutation => mutation.getPartner(sp1) -> mutation)
      // Select distinct partners
      pairs.map(_._1).distinct map { dsp =>
        // Find all results
        dsp -> pairs.filter(_._1 == dsp).map(_._2)
      } filter { case (partner, results) =>
        // Filter for >1 results
        results.size > 1
      } foreach { case (partner, results) =>
        // Combine result names
        val names = results map { mutation =>
          mutation.getTemplate()(0).getName +
            // Add * if there are special requirements
            (if (Option(mutation.getSpecialConditions).map(_.size).getOrElse(0) > 0) "*" else "")
        } mkString ", "
        // And print it out
        Gendustry.logInfo("%s + %s => [%s]", sp1.getName, partner.getName, names)
      }
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
