/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.config.loader

import net.bdew.lib.recipes._
import net.bdew.lib.recipes.gencfg.ConfigEntry

case class FluidSpec(id: String, amount: Int)

// === Modifiers for apiary upgrades ===

abstract class EntryModifier extends ConfigEntry

case class EntryModifierAdd(v: Float) extends EntryModifier

case class EntryModifierSub(v: Float) extends EntryModifier

case class EntryModifierMul(v: Float) extends EntryModifier

case class EntryModifierDiv(v: Float) extends EntryModifier

// === Machine Recipes ===

case class RsMutagen(st: StackRef, mb: Int) extends RecipeStatement

case class RsLiquidDNA(st: StackRef, mb: Int) extends RecipeStatement

case class RsProtein(st: StackRef, mb: Int) extends RecipeStatement

sealed trait RecipeMode

case object RecipeModeNew extends RecipeMode

case object RecipeModeReplace extends RecipeMode

case object RecipeModeExtend extends RecipeMode

// BC Assembly Table
case class RsAssembly(rec: List[(Char, Int)], id: String, power: Int, result: StackRef, cnt: Int) extends CraftingStatement

// Forestry Centrifuge
case class RsCentrifuge(st: StackRef, out: List[(Double, StackRef)], time: Int, mode: RecipeMode) extends RecipeStatement

// Forestry Squeezer
case class RsSqueezer(st: StackRef, fluid: FluidSpec, time: Int, remnants: StackRef, chance: Int, mode: RecipeMode) extends RecipeStatement

// === Mutations ===

abstract class MutationRequirement

case class RsMutation(parent1: String, parent2: String, result: String, chance: Float, secret: Boolean, requirements: List[MutationRequirement]) extends RecipeStatement

case class MReqTemperature(temperature: String) extends MutationRequirement

case class MReqHumidity(humidity: String) extends MutationRequirement

case class MReqBlock(block: StackBlock) extends MutationRequirement

case class MReqBiome(biome: String) extends MutationRequirement

// === Conditions ===

case class CndHaveRoot(name: String) extends Condition

// === StackRefs ===

case class StackApiaryUpgrade(name: String) extends StackRef

case class StackHoneyComb(name: String) extends StackRef

case class StackHoneyDrop(name: String) extends StackRef
