/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.config.loader

import net.bdew.lib.recipes.{DelayedStatement, RecipeParser, RecipeLoader}
import net.bdew.lib.recipes.gencfg.GenericConfigLoader
import net.bdew.lib.recipes.lootlist.LootListLoader
import net.bdew.gendustry.fluids.{ProteinSources, LiquidDNASources, MutagenSources}
import net.minecraftforge.oredict.OreDictionary
import buildcraft.api.recipes.AssemblyRecipe
import net.bdew.gendustry.config.Tuning

class Loader extends RecipeLoader with GenericConfigLoader with LootListLoader {
  val cfgStore = Tuning

  var mutations = List.empty[StMutation]

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
