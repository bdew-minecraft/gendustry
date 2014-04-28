/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.custom

import forestry.api.apiculture._
import forestry.api.core.{EnumTemperature, EnumHumidity}
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import forestry.api.genetics.{AlleleManager, IIndividual}
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.lib.recipes.gencfg.{EntryStr, ConfigSection}
import net.bdew.lib.Misc
import net.bdew.gendustry.Gendustry
import net.bdew.lib.recipes.lootlist.EntryLootList
import net.bdew.gendustry.config.TuningLoader

class BeeSpecies(cfg: ConfigSection, ident: String) extends IAlleleBeeSpecies {
  // IAllele
  override val getName = Misc.toLocal("gendustry.bee.species." + ident)
  override val isDominant = cfg.getBoolean("Dominant")
  override val getUID = "gendustry.bee." + ident

  // IAlleleSpecies
  override val getIconProvider = BeeIconProvider

  val primaryColour = cfg.getColor("PrimaryColor").asRGB
  val secondaryColour = cfg.getColor("SecondaryColor").asRGB

  override def getIconColour(renderPass: Int) = renderPass match {
    case 0 => primaryColour
    case 1 => secondaryColour
    case _ => 0xFFFFFF
  }

  //FIXME
  override val isSecret = cfg.getBoolean("Secret")
  override val isCounted = !isSecret
  override val hasEffect = cfg.getBoolean("Glowing")
  override val getHumidity = EnumHumidity.valueOf(cfg.getString("Humidity").toUpperCase)
  override val getTemperature = EnumTemperature.valueOf(cfg.getString("Temperature").toUpperCase)
  override def getResearchBounty(world: World, researcher: String, individual: IIndividual, bountyLevel: Int) = ???
  override def getResearchSuitability(itemstack: ItemStack) = ???
  override def getComplexity = ???
  override val getBranch = AlleleManager.alleleRegistry.getClassification("genus." + cfg.getString("Branch"))
  override val getAuthority = cfg.getString("Authority")
  override val getBinomial = cfg.getString("Binominal")
  override val getDescription = Misc.toLocal("gendustry.bee.species." + ident + ".description")

  // IAlleleBeeSpecies
  override val getEntityTexture = "textures/entity/bees/honeyBee.png"
  @SideOnly(Side.CLIENT)
  override def getIcon(kind: EnumBeeType, renderPass: Int) =
    BeeIconProvider.icons(kind.ordinal())(renderPass)

  // no jubilance for now
  override def isJubilant(genome: IBeeGenome, housing: IBeeHousing) = true

  import scala.collection.JavaConverters._

  def prepareLootList(name: String) =
    TuningLoader.loader.resolveLootList(cfg.rawget(name, classOf[EntryLootList]))
      .toMap.map(x => x._2 -> new Integer(x._1)).asJava

  Gendustry.logInfo("Resolving products list for bee '%s'...", ident)
  override val getProducts = prepareLootList("Products")
  getProducts.asScala.foreach(x => Gendustry.logInfo("  [%d%%] %s", x._2, x._1))

  Gendustry.logInfo("Resolving specialty list for bee '%s'...", ident)
  override val getSpecialty = prepareLootList("Specialty")
  getSpecialty.asScala.foreach(x => Gendustry.logInfo("  [%d%%] %s", x._2, x._1))

  override val isNocturnal = cfg.getBoolean("Nocturnal")
  override val getRoot = AlleleManager.alleleRegistry.getSpeciesRoot("rootBees").asInstanceOf[IBeeRoot]

  // Internal stuff

  def getTemplate = {
    val traits = cfg.getSection("Traits")
    val tpl = (
      if (traits.hasValue("Base")) {
        Option(getRoot.getTemplate(traits.getString("Base")))
          .getOrElse(sys.error("Template %s not found".format(traits.getString("Base"))))
      } else {
        getRoot.getDefaultTemplate
      }).clone()

    tpl(0) = this

    for ((chrName, entry) <- traits.filterType(classOf[EntryStr]) if chrName != "Base") {
      val chromosome = EnumBeeChromosome.valueOf(chrName.toUpperCase)
      val allele = Option(AlleleManager.alleleRegistry.getAllele(entry.v))
        .getOrElse(sys.error("Allele %s not found".format(entry.v)))
      if (!chromosome.getAlleleClass.isInstance(allele))
        sys.error("Allele %s is not valid for chromosome %s".format(allele.getUID, chromosome))
      tpl(chromosome.ordinal()) = allele
    }

    tpl
  }
}


