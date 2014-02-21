/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.forestry

import forestry.api.genetics._
import net.minecraft.nbt.NBTTagCompound
import forestry.api.apiculture.{EnumBeeChromosome, IBeeRoot}
import forestry.api.arboriculture.{IAlleleGrowth, IAlleleFruit, EnumTreeChromosome, ITreeRoot}
import forestry.api.lepidopterology.{EnumButterflyChromosome, IButterflyRoot}
import net.bdew.lib.Misc

case class GeneSampleInfo(root: ISpeciesRoot, chromosome: Int, allele: IAllele) {
  def writeToNBT(t: NBTTagCompound) {
    t.setString("species", root.getUID)
    t.setInteger("chromosome", chromosome)
    t.setString("allele", allele.getUID)
  }

  def getText: String = {
    import scala.collection.JavaConverters._
    val chr = root match {
      case x: IBeeRoot => EnumBeeChromosome.values()(chromosome).toString
      case x: ITreeRoot => EnumTreeChromosome.values()(chromosome).toString
      case x: IButterflyRoot => EnumButterflyChromosome.values()(chromosome).toString
      case _ => "Invalid"
    }
    val alstr = allele match {
      case i: IAlleleInteger => chr match {
        case "GIRTH" => "%d x %d".format(i.getValue, i.getValue)
        case "FERTILITY" if !root.isInstanceOf[ITreeRoot] => i.getValue.toString
        case "METABOLISM" => i.getValue.toString
        case _ => i.getName
      }
      case f: IAlleleFlowers => f.getProvider.getDescription
      case f: IAlleleFruit => StringUtil.localize(f.getProvider.getDescription)
      case p: IAllelePlantType => if (p.getPlantTypes.isEmpty) "-" else p.getPlantTypes.asScala.mkString(", ")
      case b: IAlleleBoolean => if (b.getValue) Misc.toLocal("gendustry.allele.true") else Misc.toLocal("gendustry.allele.false")
      case g: IAlleleGrowth => g.getProvider.getDescription
      case x => StringUtil.localize(x.getName)
    }
    if (alstr == "")
      return "%s: %s".format(Misc.toLocal("gendustry.chromosome." + chr), allele.getUID)
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
}
