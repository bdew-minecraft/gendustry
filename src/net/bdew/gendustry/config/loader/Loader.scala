/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.config.loader

import buildcraft.core.recipes.AssemblyRecipeManager
import buildcraft.core.recipes.AssemblyRecipeManager.AssemblyRecipe
import forestry.api.recipes.RecipeManagers
import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.compat.ForestryHelper
import net.bdew.gendustry.config.Tuning
import net.bdew.gendustry.fluids.{LiquidDNASources, MutagenSources, ProteinSources}
import net.bdew.lib.recipes._
import net.bdew.lib.recipes.gencfg.GenericConfigLoader
import net.bdew.lib.recipes.lootlist.LootListLoader
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.{FluidRegistry, FluidStack}
import net.minecraftforge.oredict.OreDictionary

class Loader extends RecipeLoader with GenericConfigLoader with LootListLoader {
  // Mutations are collected here for later processing
  val cfgStore = Tuning

  var mutations = List.empty[RsMutation]

  override def newParser(): RecipeParser = new Parser()

  def getConcreteStackNoWildcard(ref: StackRef, cnt: Int = 1) = {
    val resolved = getConcreteStack(ref, cnt)
    if (resolved.getItemDamage == OreDictionary.WILDCARD_VALUE) {
      resolved.setItemDamage(0)
      Gendustry.logInfo("meta/damage is unset in %s, defaulting to 0", ref)
    }
    resolved
  }

  override def resolveCondition(cond: Condition) = cond match {
    case CndHaveRoot(root) => ForestryHelper.haveRoot(root)
    case _ => super.resolveCondition(cond)
  }

  def resolveFluid(s: FluidSpec) =
    new FluidStack(Option(FluidRegistry.getFluid(s.id)).getOrElse(error("Fluid %s not found", s.id)), s.amount)

  override def processRecipeStatement(s: RecipeStatement): Unit = s match {
    case RsMutagen(st, mb) =>
      for (x <- getAllConcreteStacks(st)) {
        MutagenSources.register(x, mb)
        Gendustry.logInfo("Added Mutagen source %s -> %d mb", x, mb)
      }

    case RsLiquidDNA(st, mb) =>
      for (x <- getAllConcreteStacks(st)) {
        LiquidDNASources.register(x, mb)
        Gendustry.logInfo("Added Liquid DNA source %s -> %d mb", x, mb)
      }

    case RsProtein(st, mb) =>
      for (x <- getAllConcreteStacks(st)) {
        ProteinSources.register(x, mb)
        Gendustry.logInfo("Added Protein source %s -> %d mb", x, mb)
      }

    case RsAssembly(rec, power, out, cnt) =>
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

    case RsCentrifuge(stack, out, time) =>
      Gendustry.logInfo("Adding centrifuge recipe: %s => %s", stack, out)

      // forestry API is stupid and requires a hashmap, build one for it
      val outStacks = new java.util.HashMap[ItemStack, Integer]
      resolveLootList(out).foreach(x => outStacks.put(x._2, x._1))

      val inStack = getConcreteStackNoWildcard(stack)

      RecipeManagers.centrifugeManager.addRecipe(time, inStack, outStacks)

      Gendustry.logInfo("Done %s -> %s", inStack, outStacks)

    case RsSqueezer(in, fluid, time, out, chance) =>
      Gendustry.logInfo("Adding squeezer recipe: %s => %s + %s", in, fluid, out)

      val inStack = getConcreteStackNoWildcard(in)
      val outStack = if (out != null) getConcreteStackNoWildcard(out) else null
      val outFluid = resolveFluid(fluid)

      RecipeManagers.squeezerManager.addRecipe(time, Array(inStack), outFluid, outStack, chance)

      Gendustry.logInfo("Done %s -> %s + %s", inStack, outStack, outFluid)

    case x: RsMutation => mutations +:= x

    case _ => super.processRecipeStatement(s)
  }
}
