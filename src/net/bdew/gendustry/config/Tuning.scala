/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.config

import net.bdew.lib.recipes.gencfg._
import net.bdew.lib.recipes._
import net.bdew.gendustry.fluids.{LiquidDNASources, ProteinSources, MutagenSources}
import net.minecraftforge.oredict.OreDictionary
import buildcraft.api.recipes.AssemblyRecipe
import java.io._
import net.bdew.gendustry.Gendustry
import cpw.mods.fml.common.FMLCommonHandler
import net.bdew.lib.recipes.lootlist.{LootListParser, LootListLoader}
import net.bdew.lib.recipes.gencfg.ConfigSection
import net.bdew.lib.recipes.gencfg.CfgVal

object Tuning extends ConfigSection

object TuningLoader {

  // Modifiers for apiary upgrades

  abstract class EntryModifier extends CfgEntry

  case class EntryModifierAdd(v: Float) extends EntryModifier

  case class EntryModifierSub(v: Float) extends EntryModifier

  case class EntryModifierMul(v: Float) extends EntryModifier

  case class EntryModifierDiv(v: Float) extends EntryModifier

  // Statements

  case class StMutagen(st: StackRef, mb: Int) extends DelayedStatement

  case class StLiquidDNA(st: StackRef, mb: Int) extends DelayedStatement

  case class StProtein(st: StackRef, mb: Int) extends DelayedStatement

  case class StAssembly(rec: List[(Char, Int)], power: Int, result: StackRef, cnt: Int) extends CraftingStatement

  // Mutations

  abstract class MutationRequirement

  case class StMutation(parent1: String, parent2: String, result: String, chance: Float, secret: Boolean, requirements: List[MutationRequirement]) extends DelayedStatement

  case class MReqTemperature(temperature: String) extends MutationRequirement

  case class MReqHumidity(humidity: String) extends MutationRequirement

  class Parser extends RecipeParser with GenericConfigParser with LootListParser {
    override def delayedStatement = mutagen | dna | protein | assembly | stMutation | super.delayedStatement

    def mutagen = "mutagen" ~> ":" ~> spec ~ ("->" ~> int) ^^ {
      case sp ~ n => StMutagen(sp, n)
    }

    def dna = "dna" ~> ":" ~> spec ~ ("->" ~> int) ^^ {
      case sp ~ n => StLiquidDNA(sp, n)
    }

    def protein = "protein" ~> ":" ~> spec ~ ("->" ~> int) ^^ {
      case sp ~ n => StProtein(sp, n)
    }

    def charWithCount = recipeChar ~ ("*" ~> int).? ^^ {
      case ch ~ cnt => (ch, cnt.getOrElse(1))
    }

    def mrTemperature = "Req" ~> "Temperature" ~> ident ^^ MReqTemperature
    def mrHumidity = "Req" ~> "Humidity" ~> ident ^^ MReqHumidity

    def mutationReq = mrTemperature | mrHumidity

    def stMutation = "secret".? ~ ("mutation" ~> ":") ~ (decimalNumber <~ "%") ~ str ~ "+" ~ str ~ "=" ~ str ~ mutationReq.* ^^ {
      case secret ~ mutation ~ chance ~ p1 ~ plus ~ p2 ~ eq ~ res ~ req =>
        StMutation(parent1 = p1, parent2 = p2, result = res,
          chance = chance.toFloat, secret = secret.isDefined, requirements = req)
    }

    def assembly = "assembly" ~> ":" ~> (charWithCount <~ ",").+ ~ (int <~ "mj") ~ ("=>" ~> specWithCount) ^^ {
      case r ~ p ~ (s ~ n) => StAssembly(r, p, s, n.getOrElse(1))
    }

    override def cfgStatement = cfgAdd | cfgMul | cfgSub | cfgDiv | super.cfgStatement

    def cfgAdd = ident ~ ("+" ~> "=" ~> decimalNumber) ^^ { case id ~ n => CfgVal(id, EntryModifierAdd(n.toFloat)) }
    def cfgMul = ident ~ ("*" ~> "=" ~> decimalNumber) ^^ { case id ~ n => CfgVal(id, EntryModifierMul(n.toFloat)) }
    def cfgSub = ident ~ ("-" ~> "=" ~> decimalNumber) ^^ { case id ~ n => CfgVal(id, EntryModifierSub(n.toFloat)) }
    def cfgDiv = ident ~ ("/" ~> "=" ~> decimalNumber) ^^ { case id ~ n => CfgVal(id, EntryModifierDiv(n.toFloat)) }
  }

  // Mutations are collected here for later processing
  var mutations = List.empty[StMutation]

  class Loader extends RecipeLoader with GenericConfigLoader with LootListLoader {
    val cfgStore = Tuning

    override def newParser(): RecipeParser = new Parser()

    override def processDelayedStatement(s: DelayedStatement): Unit = s match {
      case StMutagen(st, mb) =>
        for (x <- getAllConcreteStacks(st)) {
          MutagenSources.register(x, mb)
          log.info("Added Mutagen source %s -> %d mb".format(x, mb))
        }

      case StLiquidDNA(st, mb) =>
        for (x <- getAllConcreteStacks(st)) {
          LiquidDNASources.register(x, mb)
          log.info("Added Liquid DNA source %s -> %d mb".format(x, mb))
        }

      case StProtein(st, mb) =>
        for (x <- getAllConcreteStacks(st)) {
          ProteinSources.register(x, mb)
          log.info("Added Protein source %s -> %d mb".format(x, mb))
        }

      case StAssembly(rec, power, out, cnt) =>
        log.info("Adding assembly recipe: %s + %d mj => %s * %d".format(rec, power, out, cnt))
        val outStack = getConcreteStack(out, cnt)
        val stacks = rec.map {
          case (c, n) =>
            val s = getConcreteStack(currCharMap(c), n)
            if (s.getItemDamage == OreDictionary.WILDCARD_VALUE) {
              s.setItemDamage(0)
              log.warning("%s added with wildcard metadata which is unsupported, assuming 0".format(s))
            }
            log.info("%s -> %s".format(c, s))
            s
        }
        log.info("Output: %s".format(outStack))
        AssemblyRecipe.assemblyRecipes.add(new AssemblyRecipe(stacks.toArray, power, outStack))
        log.info("Done")

      case x: StMutation => mutations +:= x

      case _ => super.processDelayedStatement(s)
    }
  }

  val loader = new Loader

  def loadDealayed() = loader.processDelayedStatements()

  def loadConfigFiles() {
    val listReader = new BufferedReader(new InputStreamReader(
      getClass.getResourceAsStream("/assets/gendustry/config/files.lst")))
    val list = Iterator.continually(listReader.readLine)
      .takeWhile(_ != null)
      .map(_.trim)
      .filterNot(_.startsWith("#"))
      .filterNot(_.isEmpty)
      .toList
    listReader.close()

    val configDir = new File(Gendustry.configDir, "gendustry")
    if (!configDir.exists()) {
      configDir.mkdir()
      val f = new FileWriter(new File(configDir, "readme.txt"))
      f.write("Any .cfg files in this directory will be loaded after the internal configuration, in alpahabetic order\n")
      f.write("Files in 'overrides' directory with matching names cab be used to override internal configuration\n")
      f.close()
    }

    val overrideDir = new File(configDir, "overrides")
    if (!overrideDir.exists()) overrideDir.mkdir()

    Gendustry.logInfo("Loading internal config files")

    for (fileName <- list) {
      val overrideFile = new File(overrideDir, fileName)
      if (overrideFile.exists()) {
        tryLoadConfig(new FileReader(overrideFile), overrideFile.getCanonicalPath)
      } else {
        val resname = "/assets/gendustry/config/" + fileName
        tryLoadConfig(new InputStreamReader(getClass.getResourceAsStream(resname)), getClass.getResource(resname).toString)
      }
    }

    Gendustry.logInfo("Loading user config files")

    for (fileName <- configDir.list().sorted if fileName.endsWith(".cfg")) {
      val file = new File(configDir, fileName)
      if (file.canRead) tryLoadConfig(new FileReader(file), file.getCanonicalPath)
    }
  }

  def tryLoadConfig(reader: Reader, path: String) {
    Gendustry.logInfo("Loading config: %s", path)
    try {
      loader.load(reader)
    } catch {
      case e: Throwable =>
        FMLCommonHandler.instance().raiseException(e, "Gendustry config loading failed in file %s: %s".format(path, e.getMessage), true)
    } finally {
      reader.close()
    }
  }
}

