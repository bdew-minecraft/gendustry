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
import net.bdew.gendustry.config.Tuning
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.{FluidRegistry, FluidStack}
import forestry.api.recipes.RecipeManagers
import net.bdew.gendustry.Gendustry
import buildcraft.core.recipes.AssemblyRecipeManager.AssemblyRecipe
import buildcraft.core.recipes.AssemblyRecipeManager

class Loader extends RecipeLoader with GenericConfigLoader with LootListLoader {
  val cfgStore = Tuning

  var mutations = List.empty[StMutation]

  override def newParser(): RecipeParser = new Parser()

  def getConcreteStackNoWildcard(ref: StackRef, cnt: Int = 1) = {
    val resolved = getConcreteStack(ref, cnt)
    if (resolved.getItemDamage == OreDictionary.WILDCARD_VALUE) {
      resolved.setItemDamage(0)
      Gendustry.logInfo("meta/damage is unset in %s, defaulting to 0", ref)
    }
    resolved
  }

  def resolveFluid(s: FluidSpec) =
    new FluidStack(Option(FluidRegistry.getFluid(s.id)).getOrElse(error("Fluid %s not found", s.id)), s.amount)

  override def processDelayedStatement(s: DelayedStatement): Unit = s match {
    case StMutagen(st, mb) =>
      for (x <- getAllConcreteStacks(st)) {
        MutagenSources.register(x, mb)
        Gendustry.logInfo("Added Mutagen source %s -> %d mb", x, mb)
      }

    case StLiquidDNA(st, mb) =>
      for (x <- getAllConcreteStacks(st)) {
        LiquidDNASources.register(x, mb)
        Gendustry.logInfo("Added Liquid DNA source %s -> %d mb", x, mb)
      }

    case StProtein(st, mb) =>
      for (x <- getAllConcreteStacks(st)) {
        ProteinSources.register(x, mb)
        Gendustry.logInfo("Added Protein source %s -> %d mb", x, mb)
      }

    case StAssembly(rec, power, out, cnt) =>
      Gendustry.logInfo("Adding assembly recipe: %s + %d mj => %s * %d", rec, power, out, cnt)
      val outStack = getConcreteStack(out, cnt)
      val stacks = rec.map {
        case (c, n) =>
          val s = getConcreteStackNoWildcard(currCharMap(c), n)
          Gendustry.logInfo("%s -> %s", c, s)
          s
      }
      Gendustry.logInfo("Output: %s", outStack)
      AssemblyRecipeManager.INSTANCE.getRecipes.add(new AssemblyRecipe(outStack, power, stacks: _*))
      Gendustry.logInfo("Done")

    case StCentrifuge(stack, out, time) =>
      Gendustry.logInfo("Adding centrifuge recipe: %s => %s", stack, out)

      // forestry API is stupid and requires a hashmap, build one for it
      val outStacks = new java.util.HashMap[ItemStack, Integer]
      resolveLootList(out).foreach(x => outStacks.put(x._2, x._1))

      val inStack = getConcreteStackNoWildcard(stack)

      RecipeManagers.centrifugeManager.addRecipe(time, inStack, outStacks)

      Gendustry.logInfo("Done %s -> %s", inStack, outStacks)

    case StSqueezer(in, fluid, time, out, chance) =>
      Gendustry.logInfo("Adding squeezer recipe: %s => %s + %s", in, fluid, out)

      val inStack = getConcreteStackNoWildcard(in)
      val outStack = if (out != null) getConcreteStackNoWildcard(out) else null
      val outFluid = resolveFluid(fluid)

      RecipeManagers.squeezerManager.addRecipe(time, Array(inStack), outFluid, outStack, chance)

      Gendustry.logInfo("Done %s -> %s + %s", inStack, outStack, outFluid)

    case x: StMutation => mutations +:= x

    case StRegOredict(id, spec, wildcard) =>
      Gendustry.logInfo("Registering ore dictionary entry: %s -> %s", spec, id)
      val stack = getConcreteStack(spec)
      if (wildcard) {
        Gendustry.logInfo("Forcing wildcard damage (was %d)", stack.getItemDamage)
        stack.setItemDamage(OreDictionary.WILDCARD_VALUE)
      }
      Gendustry.logInfo("Actual stack: %s", stack)
      OreDictionary.registerOre(id, stack)

    case _ => super.processDelayedStatement(s)
  }
}
