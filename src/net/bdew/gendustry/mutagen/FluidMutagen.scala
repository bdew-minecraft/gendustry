/*
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.mutagen

import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry

class FluidMutagen extends Fluid("Mutagen") {
  setDensity(1000)
  setViscosity(1000)
  setUnlocalizedName("gendustry.mutagen")
  FluidRegistry.registerFluid(this)
}