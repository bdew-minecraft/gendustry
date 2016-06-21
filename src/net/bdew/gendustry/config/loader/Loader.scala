/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.config.loader

import forestry.api.recipes.RecipeManagers
import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.compat.ForestryHelper
import net.bdew.gendustry.config.Tuning
import net.bdew.gendustry.custom.{CustomFlowerAlleles, CustomHives, CustomHoneyComb, CustomHoneyDrop}
import net.bdew.gendustry.fluids.{LiquidDNASources, MutagenSources, ProteinSources}
import net.bdew.gendustry.machines.apiary.upgrades.{ItemApiaryUpgrade, Upgrades}
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
      Gendustry.logDebug("meta/damage is unset in %s, defaulting to 0", ref)
    }
    resolved
  }

  override def getConcreteStack(s: StackRef, cnt: Int): ItemStack = s match {
    case StackApiaryUpgrade(name: String) =>
      Upgrades.map.find(_._2.name == name) map { case (id, upgrade) =>
        new ItemStack(ItemApiaryUpgrade, cnt, id)
      } getOrElse error("Apiary upgrade not found: ", name)

    case StackHoneyComb(name: String) =>
      CustomHoneyComb.data.find(_._2.name == name) map { case (id, info) =>
        new ItemStack(CustomHoneyComb, cnt, id)
      } getOrElse error("Honey comb not found: ", name)

    case StackHoneyDrop(name: String) =>
      CustomHoneyDrop.data.find(_._2.name == name) map { case (id, info) =>
        new ItemStack(CustomHoneyDrop, cnt, id)
      } getOrElse error("Honey drop not found: ", name)

    case _ => super.getConcreteStack(s, cnt)
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
        Gendustry.logDebug("Added Mutagen source %s -> %d mb", x, mb)
      }

    case RsLiquidDNA(st, mb) =>
      for (x <- getAllConcreteStacks(st)) {
        LiquidDNASources.register(x, mb)
        Gendustry.logDebug("Added Liquid DNA source %s -> %d mb", x, mb)
      }

    case RsProtein(st, mb) =>
      for (x <- getAllConcreteStacks(st)) {
        ProteinSources.register(x, mb)
        Gendustry.logDebug("Added Protein source %s -> %d mb", x, mb)
      }

    // TODO: Reenable when BC is available
    //    case RsAssembly(rec, id, power, out, cnt) =>
    //      Gendustry.logDebug("Adding assembly recipe: %s + %d mj => %s * %d", rec, power, out, cnt)
    //      val outStack = getConcreteStack(out, cnt)
    //      val stacks = rec.map {
    //        case (c, n) =>
    //          val s = getConcreteStackNoWildcard(currCharMap(c), n)
    //          Gendustry.logDebug("%s -> %s", c, s)
    //          s
    //      }
    //      Gendustry.logDebug("Output: %s", outStack)
    //      AssemblyRecipeManager.INSTANCE.addRecipe(id, power, outStack, stacks: _*)
    //      Gendustry.logDebug("Done")

    case RsCentrifuge(stack, out, time) =>
      Gendustry.logDebug("Adding centrifuge recipe: %s => %s", stack, out)

      // forestry API is stupid and requires a hashmap, build one for it
      val outStacks = new java.util.HashMap[ItemStack, java.lang.Float]
      resolveLootList(out).foreach(x => outStacks.put(x._2, x._1.toFloat / 100F))

      val inStack = getConcreteStackNoWildcard(stack)

      RecipeManagers.centrifugeManager.addRecipe(time, inStack, outStacks)

      Gendustry.logDebug("Done %s -> %s", inStack, outStacks)

    case RsSqueezer(in, fluid, time, out, chance) =>
      Gendustry.logDebug("Adding squeezer recipe: %s => %s + %s", in, fluid, out)

      val inStack = getConcreteStackNoWildcard(in)
      val outStack = if (out != null) getConcreteStackNoWildcard(out) else null
      val outFluid = resolveFluid(fluid)

      RecipeManagers.squeezerManager.addRecipe(time, Array(inStack), outFluid, outStack, chance)

      Gendustry.logDebug("Done %s -> %s + %s", inStack, outStack, outFluid)

    case x: RsMutation => mutations +:= x

    case _ => super.processRecipeStatement(s)
  }

  override def processConfigStatement(s: ConfigStatement): Unit = s match {
    case x: CSHiveDefinition =>
      CustomHives.registerHiveDefinition(x)
    case x: CSFlowerAllele =>
      CustomFlowerAlleles.addDefinition(x)
    case _ => super.processConfigStatement(s)
  }
}
