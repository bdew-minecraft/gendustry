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
import java.io.{InputStreamReader, FileReader, File}
import net.bdew.gendustry.Gendustry
import net.bdew.lib.recipes.gencfg.ConfigSection
import net.bdew.lib.recipes.gencfg.CfgVal

object Tuning extends ConfigSection

object TuningLoader {

  abstract class EntryModifier extends CfgEntry

  case class EntryModifierAdd(v: Float) extends EntryModifier

  case class EntryModifierSub(v: Float) extends EntryModifier

  case class EntryModifierMul(v: Float) extends EntryModifier

  case class EntryModifierDiv(v: Float) extends EntryModifier

  case class StMutagen(st: StackRef, mb: Int) extends DelayedStatement

  case class StLiquidDNA(st: StackRef, mb: Int) extends DelayedStatement

  case class StProtein(st: StackRef, mb: Int) extends DelayedStatement

  case class StAssembly(rec: List[(Char, Int)], power: Int, result: StackRef, cnt: Int) extends CraftingStatement

  class Parser extends RecipeParser with GenericConfigParser {
    override def delayedStatement = mutagen | dna | protein | assembly | super.delayedStatement

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

    def assembly = "assembly" ~> ":" ~> (charWithCount <~ ",").+ ~ (int <~ "mj") ~ ("=>" ~> specWithCount) ^^ {
      case r ~ p ~ (s ~ n) => StAssembly(r, p, s, n.getOrElse(1))
    }

    override def cfgStatement = cfgAdd | cfgMul | cfgSub | cfgDiv | super.cfgStatement

    def cfgAdd = ident ~ ("+" ~> "=" ~> decimalNumber) ^^ { case id ~ n => CfgVal(id, EntryModifierAdd(n.toFloat)) }
    def cfgMul = ident ~ ("*" ~> "=" ~> decimalNumber) ^^ { case id ~ n => CfgVal(id, EntryModifierMul(n.toFloat)) }
    def cfgSub = ident ~ ("-" ~> "=" ~> decimalNumber) ^^ { case id ~ n => CfgVal(id, EntryModifierSub(n.toFloat)) }
    def cfgDiv = ident ~ ("/" ~> "=" ~> decimalNumber) ^^ { case id ~ n => CfgVal(id, EntryModifierDiv(n.toFloat)) }
  }

  class Loader extends RecipeLoader with GenericConfigLoader {
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

      case _ => super.processDelayedStatement(s)
    }
  }

  val loader = new Loader

  def loadDealayed() = loader.processDelayedStatements()

  def load(part: String, checkJar: Boolean = true) {
    val f = new File(Gendustry.configDir, "%s-%s.cfg".format(Gendustry.modId, part))
    val r = if (f.exists() && f.canRead) {
      Gendustry.logInfo("Loading configuration from %s", f.toString)
      new FileReader(f)
    } else if (checkJar) {
      val res = "/assets/%s/%s-%s.cfg".format(Gendustry.modId, Gendustry.modId, part)
      val stream = this.getClass.getResourceAsStream(res)
      Gendustry.logInfo("Loading configuration from JAR - %s", this.getClass.getResource(res))
      new InputStreamReader(this.getClass.getResourceAsStream("/assets/%s/%s-%s.cfg".format(Gendustry.modId, Gendustry.modId, part)))
    } else {
      return
    }
    try {
      loader.load(r)
    } finally {
      r.close()
    }
  }
}

