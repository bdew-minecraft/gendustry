/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.forestry

import forestry.api.apiculture.{EnumBeeChromosome, IBeeRoot}
import forestry.api.arboriculture.{EnumTreeChromosome, IAlleleFruit, IAlleleGrowth, ITreeRoot}
import forestry.api.genetics._
import forestry.api.lepidopterology.{EnumButterflyChromosome, IButterflyRoot}
import net.bdew.gendustry.api.items.IGeneSample
import net.bdew.gendustry.compat.EnumFlowerChromosome
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
    val alstr = allele match {
      case i: IAlleleInteger => chr match {
        case "GIRTH" => "%d x %d".format(i.getValue, i.getValue)
        case "FERTILITY" if !root.isInstanceOf[ITreeRoot] => i.getValue.toString
        case "METABOLISM" => i.getValue.toString
        case _ => i.getName
      }
      case f: IAlleleFlowers => f.getProvider.getDescription
      case f: IAlleleFruit => f.getProvider.getDescription
      case p: IAllelePlantType => if (p.getPlantTypes.isEmpty) "-" else p.getPlantTypes.asScala.mkString(", ")
      case b: IAlleleBoolean => if (b.getValue) Misc.toLocal("gendustry.allele.true") else Misc.toLocal("gendustry.allele.false")
      case g: IAlleleGrowth => g.getProvider.getDescription
      case x => x.getName
    }
    if (alstr == "")
      return "%s: %s".format(Misc.toLocal("gendustry.chromosome." + chr), allele.getUID)
    else if (alstr.startsWith("for."))
      return "%s: %s".format(Misc.toLocal("gendustry.chromosome." + chr), alstr.replace("for.", ""))
    else
      return "%s: %s".format(Misc.toLocal("gendustry.chromosome." + chr), alstr)
  }
}

object GeneSampleInfo {
  def fromNBT(t: NBTTagCompound): GeneSampleInfo = {
    if (t == null) return null
    val species = AlleleManager.alleleRegistry.getSpeciesRoot(t.getString("species"))
    val allele = AlleleManager.alleleRegistry.getAllele(t.getString("allele"))
    return GeneSampleInfo(species, t.getInteger("chromosome"), allele)
  }
  def getChromosomeName(root: ISpeciesRoot, chromosome: Int) = root match {
    case x: IBeeRoot => EnumBeeChromosome.values()(chromosome).toString
    case x: ITreeRoot => EnumTreeChromosome.values()(chromosome).toString
    case x: IButterflyRoot => EnumButterflyChromosome.values()(chromosome).toString
    case x: ISpeciesRoot if x.getUID == "rootFlowers" => EnumFlowerChromosome.values()(chromosome).toString
    case _ => "Invalid"
  }
}
