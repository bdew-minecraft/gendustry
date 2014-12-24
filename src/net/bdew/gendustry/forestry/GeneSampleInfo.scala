/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.forestry

import forestry.api.apiculture.IAlleleBeeSpecies
import forestry.api.arboriculture._
import forestry.api.genetics._
import net.bdew.gendustry.api.items.IGeneSample
import net.bdew.lib.Misc
import net.minecraft.nbt.NBTTagCompound

case class GeneSampleInfo(root: ISpeciesRoot, chromosome: Int, allele: IAllele) extends IGeneSample {
  def writeToNBT(t: NBTTagCompound) {
    t.setString("species", root.getUID)
    t.setInteger("chromosome", chromosome)
    t.setString("allele", allele.getUID)
  }

  @Override
  def getLocalizedName: String = {
    import scala.collection.JavaConverters._
    val chr = GeneSampleInfo.getChromosomeName(root, chromosome)
    val str = allele match {
      // Custom localized names, see https://github.com/bdew/gendustry/issues/41
      case a: IAlleleTreeSpecies =>
        val k = "for.trees.custom.treealyzer.sapling." + a.getUnlocalizedName.replace("trees.species.", "")
        if (Misc.hasLocal(k))
          Misc.toLocal(k)
        else
          a.getName
      case a: IAlleleBeeSpecies =>
        val k = "for.bees.custom.beealyzer.drone." + a.getUnlocalizedName.replace("bees.species.", "")
        if (Misc.hasLocal(k))
          Misc.toLocal(k)
        else
          a.getName
      case i: IAlleleInteger => chr match {
        case "GIRTH" => "%d x %d".format(i.getValue, i.getValue)
        case "FERTILITY" if !root.isInstanceOf[ITreeRoot] => i.getValue.toString
        case "METABOLISM" => i.getValue.toString
        case _ => i.getName
      }
      case p: IAllelePlantType => if (p.getPlantTypes.isEmpty) "-" else p.getPlantTypes.asScala.mkString(", ")
      case b: IAlleleBoolean => if (b.getValue) Misc.toLocal("gendustry.allele.true") else Misc.toLocal("gendustry.allele.false")
      case x => x.getName
    }
    if (str == "")
      return "%s: %s".format(Misc.toLocal("gendustry.chromosome." + chr), allele.getUID)
    else if (str.startsWith("for."))
      return "%s: %s".format(Misc.toLocal("gendustry.chromosome." + chr), str.replace("for.", ""))
    else
      return "%s: %s".format(Misc.toLocal("gendustry.chromosome." + chr), str)
  }
}

object GeneSampleInfo {
  def fromNBT(t: NBTTagCompound): GeneSampleInfo = {
    if (t == null) return null
    val species = AlleleManager.alleleRegistry.getSpeciesRoot(t.getString("species"))
    val allele = AlleleManager.alleleRegistry.getAllele(t.getString("allele"))
    return GeneSampleInfo(species, t.getInteger("chromosome"), allele)
  }

  def getChromosomeName(root: ISpeciesRoot, chromosome: Int) =
    GeneticsHelper.getCleanKaryotype(root).get(chromosome).map(_.toString.toUpperCase).getOrElse("Invalid")
}
