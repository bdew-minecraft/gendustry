/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.config

import net.bdew.lib.config.FluidManager
import net.bdew.gendustry.fluids.{ItemFluidCan, ItemFluidBucket, BlockFluid}
import net.bdew.gendustry.Gendustry
import net.minecraftforge.fluids.{FluidStack, FluidContainerRegistry, FluidRegistry, Fluid}
import net.bdew.lib.Misc
import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.item.{Item, ItemStack}
import net.bdew.gendustry.forestry.ForestryItems

object Fluids extends FluidManager(Config.IDs) {
  val emptyBucket = new ItemStack(Item.bucketEmpty)

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
      val newfluid = new Fluid(id)
      newfluid.setUnlocalizedName((Misc.getActiveModId + "." + id).toLowerCase)
      newfluid.setLuminosity(luminosity)
      newfluid.setDensity(density)
      newfluid.setTemperature(temperature)
      newfluid.setViscosity(viscosity)
      newfluid.setGaseous(isGaseous)
      FluidRegistry.registerFluid(newfluid)
      true
    }
    val fluid = FluidRegistry.getFluid(id.toLowerCase)
    if (fluid.getBlockID == -1) {
      val block = new BlockFluid(ids.getBlockId(id), fluid, ownFluid)
      GameRegistry.registerBlock(block, "fluid." + id)
      fluid.setBlockID(block)
    }
    if (FluidContainerRegistry.fillFluidContainer(new FluidStack(fluid, 1000), emptyBucket) == null) {
      val bucket = Items.regItem(new ItemFluidBucket(ids.getItemId(id + "Bucket"), fluid), id + "Bucket")
      FluidContainerRegistry.registerFluidContainer(fluid, new ItemStack(bucket), emptyBucket)
    }
    if (FluidContainerRegistry.fillFluidContainer(new FluidStack(fluid, 1000), ForestryItems.canEmpty) == null) {
      val can = Items.regItem(new ItemFluidCan(ids.getItemId(id + "Can"), fluid), id + "Can")
      FluidContainerRegistry.registerFluidContainer(fluid, new ItemStack(can), ForestryItems.canEmpty)
    }
    return fluid
  }

  val mutagen = registerFluid("Mutagen")
  val dna = registerFluid("LiquidDNA")
  val protein = registerFluid("Protein")

  Gendustry.logInfo("Fluids loaded")
}
