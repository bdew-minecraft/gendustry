/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.config.loader

import net.bdew.lib.recipes.gencfg.CfgEntry
import net.bdew.lib.recipes.{CraftingStatement, DelayedStatement, StackRef}

case class FluidSpec(id: String, amount: Int)

// Register OreDict statement
case class StRegOredict(id: String, spec: StackRef, wildcard: Boolean) extends DelayedStatement

// === Modifiers for apiary upgrades ===

abstract class EntryModifier extends CfgEntry

case class EntryModifierAdd(v: Float) extends EntryModifier

case class EntryModifierSub(v: Float) extends EntryModifier

case class EntryModifierMul(v: Float) extends EntryModifier

case class EntryModifierDiv(v: Float) extends EntryModifier

// === Machine Recipes ===

case class StMutagen(st: StackRef, mb: Int) extends DelayedStatement

case class StLiquidDNA(st: StackRef, mb: Int) extends DelayedStatement

case class StProtein(st: StackRef, mb: Int) extends DelayedStatement

// BC Assembly Table
case class StAssembly(rec: List[(Char, Int)], power: Int, result: StackRef, cnt: Int) extends CraftingStatement

// Forestry Centrifuge
case class StCentrifuge(st: StackRef, out: List[(Int, StackRef)], time: Int) extends DelayedStatement

// Forestry Squeezer
case class StSqueezer(st: StackRef, fluid: FluidSpec, time: Int, remnants: StackRef, chance: Int) extends DelayedStatement

// === Mutations ===

abstract class MutationRequirement

case class StMutation(parent1: String, parent2: String, result: String, chance: Float, secret: Boolean, requirements: List[MutationRequirement]) extends DelayedStatement

case class MReqTemperature(temperature: String) extends MutationRequirement

case class MReqHumidity(humidity: String) extends MutationRequirement

