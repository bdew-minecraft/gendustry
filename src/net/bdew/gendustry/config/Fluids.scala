/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.config

import java.util.Locale

import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.fluids.{BlockFluid, ItemFluidBucket}
import net.bdew.gendustry.forestry.ForestryItems
import net.bdew.lib.Misc
import net.bdew.lib.config.FluidManager
import net.bdew.lib.render.FluidModelUtils
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.init
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fluids.{Fluid, FluidContainerRegistry, FluidRegistry, FluidStack}
import net.minecraftforge.fml.common.FMLCommonHandler

object Fluids extends FluidManager {
  val emptyBucket = new ItemStack(init.Items.BUCKET)
  val emptyCan = new ItemStack(ForestryItems.canEmpty)

  def registerFluid(id: String,
                    luminosity: Int = 0,
                    density: Int = 1000,
                    temperature: Int = 295,
                    viscosity: Int = 1000,
                    isGaseous: Boolean = false): Fluid = {

    val ownFluid = if (FluidRegistry.isFluidRegistered(id.toLowerCase(Locale.US))) {
      Gendustry.logDebug("Fluid %s already registered, using existing (%s)", id, FluidRegistry.getFluid(id))
      false
    } else {
      val textures = "fluids/" + id.toLowerCase(Locale.US)
      Gendustry.logDebug("Registering fluid %s", id)
      val newFluid = new Fluid(id, new ResourceLocation(Gendustry.modId, textures + "/still"), new ResourceLocation(Gendustry.modId, textures + "/flowing"))
      newFluid.setUnlocalizedName((Misc.getActiveModId + "." + id).toLowerCase(Locale.US))
      newFluid.setLuminosity(luminosity)
      newFluid.setDensity(density)
      newFluid.setTemperature(temperature)
      newFluid.setViscosity(viscosity)
      newFluid.setGaseous(isGaseous)
      FluidRegistry.registerFluid(newFluid)
      true
    }
    val fluid = FluidRegistry.getFluid(id.toLowerCase(Locale.US))
    if (fluid.getBlock == null) {
      val block = Blocks.regBlock(new BlockFluid(fluid, ownFluid))
      fluid.setBlock(block)
      if (FMLCommonHandler.instance().getSide.isClient)
        FluidModelUtils.registerFluidModel(block, Gendustry.modId + ":fluids")
    }
    if (FluidRegistry.isUniversalBucketEnabled) {
      FluidRegistry.addBucketForFluid(fluid)
    } else {
      if (FluidContainerRegistry.fillFluidContainer(new FluidStack(fluid, 1000), emptyBucket) == null) {
        val bucket = Items.regItem(new ItemFluidBucket(fluid))
        FluidContainerRegistry.registerFluidContainer(fluid, new ItemStack(bucket), emptyBucket)
        if (FMLCommonHandler.instance().getSide.isClient)
          ModelLoader.setCustomModelResourceLocation(bucket, 0, new ModelResourceLocation(bucket.getRegistryName, "inventory"))
      }
    }
    return fluid
  }

  val mutagen = registerFluid("Mutagen")
  val dna = registerFluid("LiquidDNA")
  val protein = registerFluid("Protein")

  Gendustry.logInfo("Fluids loaded")
}
