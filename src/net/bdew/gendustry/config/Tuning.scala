/*
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.config

import net.bdew.lib.recipes.gencfg._
import net.bdew.lib.recipes.{RecipeLoader, RecipeParser, Statement, StackRef}
import net.bdew.gendustry.mutagen.MutagenRegistry
import net.minecraftforge.oredict.OreDictionary
import buildcraft.api.recipes.AssemblyRecipe
import java.io.{InputStreamReader, FileReader, File}
import net.bdew.gendustry.Gendustry

object Tuning extends ConfigSection

object TuningLoader {

  abstract class EntryModifier extends CfgEntry

  case class EntryModifierAdd(v: Float) extends EntryModifier

  case class EntryModifierSub(v: Float) extends EntryModifier

  case class EntryModifierMul(v: Float) extends EntryModifier

  case class EntryModifierDiv(v: Float) extends EntryModifier

  case class StMutagen(st: StackRef, mb: Int) extends Statement

  case class StAssembly(rec: List[(Char, Int)], power: Int, out: StackRef, cnt: Int) extends Statement

  class Parser extends RecipeParser with GenericConfigParser {
    override def statement = mutagen | assembly | super.statement
    def mutagen = "mutagen" ~> ":" ~> spec ~ ("->" ~> int) ^^ {
      case sp ~ n => StMutagen(sp, n)
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

    override def processStatement(s: Statement): Unit = s match {
      case StMutagen(st, mb) =>
        val in = getConcreteStack(st)
        MutagenRegistry.register(in, mb)
        log.info("Added mutagen source %s -> %d mb".format(in, mb))

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
      case _ => super.processStatement(s)
    }
  }

  def load(part: String) {
    val f = new File(Gendustry.configDir, "%s-%s.cfg".format(Gendustry.modId, part))
    val r = if (f.exists() && f.canRead) {
      new FileReader(f)
    } else {
      new InputStreamReader(this.getClass.getResourceAsStream("/assets/%s/%s-%s.cfg".format(Gendustry.modId, Gendustry.modId, part)))
    }
    try {
      new Loader().load(r)
    } finally {
      r.close()
    }
  }
}

