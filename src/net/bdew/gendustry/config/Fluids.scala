/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.config

import cpw.mods.fml.common.registry.GameRegistry
import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.fluids.{BlockFluid, ItemFluidBucket, ItemFluidCan}
import net.bdew.gendustry.forestry.ForestryItems
import net.bdew.gendustry.misc.GendustryCreativeTabs
import net.bdew.lib.Misc
import net.bdew.lib.config.FluidManager
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.{Fluid, FluidContainerRegistry, FluidRegistry, FluidStack}

object Fluids extends FluidManager {
  val emptyBucket = new ItemStack(GameRegistry.findItem("minecraft", "bucket"))
  val emptyCan = new ItemStack(ForestryItems.canEmpty)

  def registerFluid(id: String,
                    luminosity: Int = 0,
                    density: Int = 1000,
                    temperature: Int = 295,
                    viscosity: Int = 1000,
                    isGaseous: Boolean = false): Fluid = {

    val ownFluid = if (FluidRegistry.isFluidRegistered(id)) {
      Gendustry.logInfo("Fluid %s already registered, using existing (%s)", id, FluidRegistry.getFluid(id))
      false
    } else {
      Gendustry.logInfo("Registering fluid %s", id)
      val newFluid = new Fluid(id)
      newFluid.setUnlocalizedName((Misc.getActiveModId + "." + id).toLowerCase)
      newFluid.setLuminosity(luminosity)
      newFluid.setDensity(density)
      newFluid.setTemperature(temperature)
      newFluid.setViscosity(viscosity)
      newFluid.setGaseous(isGaseous)
      FluidRegistry.registerFluid(newFluid)
      true
    }
    val fluid = FluidRegistry.getFluid(id.toLowerCase)
    if (fluid.getBlock == null) {
      val block = new BlockFluid(fluid, ownFluid)
      GameRegistry.registerBlock(block, "fluid." + id)
      block.setCreativeTab(GendustryCreativeTabs.main)
      fluid.setBlock(block)
    }
    if (FluidContainerRegistry.fillFluidContainer(new FluidStack(fluid, 1000), emptyBucket) == null) {
      val bucket = Items.regItem(new ItemFluidBucket(fluid), id + "Bucket")
      FluidContainerRegistry.registerFluidContainer(fluid, new ItemStack(bucket), emptyBucket)
    }
    if (FluidContainerRegistry.fillFluidContainer(new FluidStack(fluid, 1000), emptyCan) == null) {
      val can = Items.regItem(new ItemFluidCan(fluid), id + "Can")
      FluidContainerRegistry.registerFluidContainer(fluid, new ItemStack(can), emptyCan)
    }
    return fluid
  }

  val mutagen = registerFluid("Mutagen")
  val dna = registerFluid("LiquidDNA")
  val protein = registerFluid("Protein")

  Gendustry.logInfo("Fluids loaded")
}
