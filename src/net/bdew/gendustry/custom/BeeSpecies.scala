/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.custom

import java.util.Locale

import com.mojang.authlib.GameProfile
import cpw.mods.fml.relauncher.{Side, SideOnly}
import forestry.api.apiculture._
import forestry.api.core.{EnumHumidity, EnumTemperature}
import forestry.api.genetics.{AlleleManager, IAllele, IIndividual}
import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.config.loader.TuningLoader
import net.bdew.gendustry.forestry.ForestryItems
import net.bdew.lib.Misc
import net.bdew.lib.items.ItemUtils
import net.bdew.lib.recipes.gencfg.{ConfigSection, EntryStr}
import net.bdew.lib.recipes.lootlist.EntryLootList
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.oredict.OreDictionary

import scala.collection.mutable

class BeeSpecies(cfg: ConfigSection, ident: String) extends IAlleleBeeSpecies {
  // IAllele
  override val getName = Misc.toLocal("gendustry.bees.species." + ident)
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

  override def getUnlocalizedName = "gendustry.bees.species." + ident

  override val isSecret = cfg.getBoolean("Secret")
  override val isCounted = !isSecret
  override val hasEffect = cfg.getBoolean("Glowing")
  override val getHumidity = EnumHumidity.valueOf(cfg.getString("Humidity").toUpperCase(Locale.US))
  override val getTemperature = EnumTemperature.valueOf(cfg.getString("Temperature").toUpperCase(Locale.US))
  override val getBranch = AlleleManager.alleleRegistry.getClassification("genus." + cfg.getString("Branch"))
  override val getAuthority = cfg.getString("Authority")
  override val getBinomial = cfg.getString("Binominal")
  override val getDescription =
    if (Misc.hasLocal("gendustry.bees.species." + ident + ".description"))
      Misc.toLocal("gendustry.bees.species." + ident + ".description")
    else ""

  // IAlleleBeeSpecies
  override val getEntityTexture = "textures/entity/bees/honeyBee.png"
  @SideOnly(Side.CLIENT)
  override def getIcon(kind: EnumBeeType, renderPass: Int) =
    BeeIconProvider.icons(kind.ordinal())(renderPass)

  // no jubilance for now
  override def isJubilant(genome: IBeeGenome, housing: IBeeHousing) = true

  import scala.collection.JavaConverters._

  def prepareLootList(name: String): Map[ItemStack, Float] =
    TuningLoader.loader.resolveLootList(cfg.getRaw(name, classOf[EntryLootList]))
      .map(x => x._2 -> x._1.toFloat / 100F).toMap

  Gendustry.logDebug("Resolving products list for bee '%s'...", ident)
  val products = prepareLootList("Products")
  products.foreach(x => Gendustry.logDebug("  [%.1f%%] %s", x._2, x._1))

  Gendustry.logDebug("Resolving specialty list for bee '%s'...", ident)
  val specialty = prepareLootList("Specialty")
  specialty.foreach(x => Gendustry.logDebug("  [%.1f%%] %s", x._2, x._1))

  // Old product/specialty maps - will be removed at some point. Manual boxing because java maps can't hold primitive values.
  override val getProducts = products.map(x => x._1 -> Int.box((x._2 * 100).round)).asJava
  override val getSpecialty = specialty.map(x => x._1 -> Int.box((x._2 * 100).round)).asJava

  // New maps with floats. Ditto about boxing.
  override def getProductChances = products.map(x => x._1 -> Float.box(x._2)).asJava
  override def getSpecialtyChances = specialty.map(x => x._1 -> Float.box(x._2)).asJava

  override val isNocturnal = cfg.getBoolean("Nocturnal")
  override val getRoot = AlleleManager.alleleRegistry.getSpeciesRoot("rootBees").asInstanceOf[IBeeRoot]

  // ==== RESEARCH SYSTEM

  override lazy val getComplexity: Int = 1 + getMutationPathLength(this, mutable.Set.empty[IAllele])

  private def getMutationPathLength(species: IAllele, excludeSpecies: mutable.Set[IAllele]): Int = {
    var highest = 0
    excludeSpecies += species
    import scala.collection.JavaConversions._
    getRoot.getPaths(species, getRoot.getKaryotypeKey) foreach { mutation =>
      if (!excludeSpecies.contains(mutation.getAllele0)) {
        val otherAdvance = getMutationPathLength(mutation.getAllele0, excludeSpecies)
        if (otherAdvance > highest) highest = otherAdvance
      }
      if (!excludeSpecies.contains(mutation.getAllele1)) {
        val otherAdvance = getMutationPathLength(mutation.getAllele1, excludeSpecies)
        if (otherAdvance > highest) {
          highest = otherAdvance
        }
      }
    }
    return 1 + (if (highest > 0) highest else 0)
  }

  override def getResearchSuitability(itemStack: ItemStack): Float = {
    import scala.collection.JavaConversions._
    if (itemStack == null || itemStack.getItem == null)
      return 0
    else if (products.keys.exists(itemStack.isItemEqual))
      return 1
    else if (specialty.keys.exists(itemStack.isItemEqual))
      return 1
    else if (OreDictionary.getOres("beeComb").exists(OreDictionary.itemMatches(_, itemStack, false)))
      return 0.4F
    else if (OreDictionary.getOres("dropHoney").exists(OreDictionary.itemMatches(_, itemStack, false)))
      return 0.5F
    else if (itemStack.getItem == ForestryItems.honeydew)
      return 0.7F
    getRoot.getResearchCatalysts.find(x => ItemUtils.isSameItem(x._1, itemStack)).foreach(x => return x._2)
    return 0
  }

  override def getResearchBounty(world: World, researcher: GameProfile, individual: IIndividual, bountyLevel: Int): Array[ItemStack] = {
    import scala.collection.JavaConversions._
    var res = List.empty[ItemStack]
    if (world.rand.nextFloat() < 10F / bountyLevel) {
      val combinations = getRoot.getCombinations(this).toList
      if (combinations.nonEmpty)
        res :+= AlleleManager.alleleRegistry.getMutationNoteStack(researcher, combinations(world.rand.nextInt(combinations.size)))
    }

    if (bountyLevel > 10)
      res ++= specialty.keys.map(x => ItemUtils.copyWithRandomSize(x, (bountyLevel / 2F).toInt, world.rand))

    res ++= products.keys.map(x => ItemUtils.copyWithRandomSize(x, (bountyLevel / 2F).toInt, world.rand))

    return res.toArray
  }

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
      val chromosome = EnumBeeChromosome.valueOf(chrName.toUpperCase(Locale.US))
      val allele = Option(AlleleManager.alleleRegistry.getAllele(entry.v))
        .getOrElse(sys.error("Allele %s not found".format(entry.v)))
      if (!chromosome.getAlleleClass.isInstance(allele))
        sys.error("Allele %s is not valid for chromosome %s".format(allele.getUID, chromosome))
      tpl(chromosome.ordinal()) = allele
    }

    tpl
  }
}


