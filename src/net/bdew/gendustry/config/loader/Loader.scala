/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.config.loader

import net.bdew.lib.recipes.{StackRef, DelayedStatement, RecipeParser, RecipeLoader}
import net.bdew.lib.recipes.gencfg.GenericConfigLoader
import net.bdew.lib.recipes.lootlist.LootListLoader
import net.bdew.gendustry.fluids.{ProteinSources, LiquidDNASources, MutagenSources}
import net.minecraftforge.oredict.OreDictionary
import buildcraft.api.recipes.AssemblyRecipe
import net.bdew.gendustry.config.Tuning
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.{FluidRegistry, FluidStack}
import forestry.api.recipes.RecipeManagers

class Loader extends RecipeLoader with GenericConfigLoader with LootListLoader {
  val cfgStore = Tuning

  var mutations = List.empty[StMutation]

  override def newParser(): RecipeParser = new Parser()

  def getConcreteStackNoWildcard(ref: StackRef, cnt: Int = 1) = {
    val resolved = getConcreteStack(ref, cnt)
    if (resolved.getItemDamage == OreDictionary.WILDCARD_VALUE) {
      resolved.setItemDamage(0)
      log.info("meta/damage is unset in %s, defaulting to 0".format(ref))
    }
    resolved
  }

  def resolveFluid(s: FluidSpec) =
    new FluidStack(Option(FluidRegistry.getFluid(s.id)).getOrElse(error("Fluid %s not found", s.id)), s.amount)

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
          val s = getConcreteStackNoWildcard(currCharMap(c), n)
          log.info("%s -> %s".format(c, s))
          s
      }
      log.info("Output: %s".format(outStack))
      AssemblyRecipe.assemblyRecipes.add(new AssemblyRecipe(stacks.toArray, power, outStack))
      log.info("Done")

    case StCentrifuge(stack, out, time) =>
      log.info("Adding centrifuge recipe: %s => %s".format(stack, out))

      // forestry API is stupid and requires a hashmap, build one for it
      val outStacks = new java.util.HashMap[ItemStack, Integer]
      resolveLootList(out).foreach(x => outStacks.put(x._2, x._1))

      val inStack = getConcreteStackNoWildcard(stack)

      RecipeManagers.centrifugeManager.addRecipe(time, inStack, outStacks)

      log.info("Done %s -> %s".format(inStack, outStacks))

    case StSqueezer(in, fluid, time, out, chance) =>
      log.info("Adding squeezer recipe: %s => %s + %s".format(in, fluid, out))

      val inStack = getConcreteStackNoWildcard(in)
      val outStack = if (out != null) getConcreteStackNoWildcard(out) else null
      val outFluid = resolveFluid(fluid)

      RecipeManagers.squeezerManager.addRecipe(time, Array(inStack), outFluid, outStack, chance)

      log.info("Done %s -> %s + %s".format(inStack, outStack, outFluid))

    case x: StMutation => mutations +:= x

    case _ => super.processDelayedStatement(s)
  }
}
