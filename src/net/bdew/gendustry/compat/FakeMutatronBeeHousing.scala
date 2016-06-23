/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.compat

import java.io.{DataInputStream, DataOutputStream}
import java.lang.Iterable
import java.util.Collections

import com.google.common.collect.ImmutableSet
import com.mojang.authlib.GameProfile
import forestry.api.apiculture._
import forestry.api.core._
import forestry.api.genetics.{AlleleManager, IIndividual}
import net.bdew.gendustry.api.blocks.IMutatron
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util._
import net.minecraft.util.math.{BlockPos, Vec3d}
import net.minecraft.world.World

class FakeMutatronBeeHousing(tile: TileEntity with IMutatron) extends IBeeHousing with IBeeModifier with IBeeListener with IBeeHousingInventory with IErrorLogic {
  val beeRoot = AlleleManager.alleleRegistry.getSpeciesRoot("rootBees").asInstanceOf[IBeeRoot]

  override def getWorldObj: World = tile.getWorld
  override def getCoordinates: BlockPos = tile.getPos

  override def getBlockLightValue: Int = getWorldObj.getLightFromNeighbors(getCoordinates.offset(EnumFacing.UP))
  override def canBlockSeeTheSky: Boolean = getWorldObj.canBlockSeeSky(getCoordinates.offset(EnumFacing.UP, 2))

  override def getBeeListeners: Iterable[IBeeListener] = Collections.singletonList(this)
  override def getBeeInventory: IBeeHousingInventory = this
  override def getBeeModifiers: Iterable[IBeeModifier] = Collections.singletonList(this)
  override def getBeekeepingLogic: IBeekeepingLogic = beeRoot.createBeekeepingLogic(this)
  override def getErrorLogic: IErrorLogic = this

  override def getOwner: GameProfile = tile.getOwner

  override def getDrone: ItemStack = tile.getParent1
  override def getQueen: ItemStack = tile.getParent2
  override def setQueen(itemStack: ItemStack): Unit = throw new UnsupportedOperationException("Not supported in mutatron")
  override def setDrone(itemStack: ItemStack): Unit = throw new UnsupportedOperationException("Not supported in mutatron")

  override def onQueenDeath(): Unit = {}
  override def wearOutEquipment(i: Int): Unit = {}
  override def onPollenRetrieved(iIndividual: IIndividual): Boolean = false
  override def addProduct(product: ItemStack, all: Boolean): Boolean = false

  override def isSunlightSimulated: Boolean = false
  override def isSelfLighted: Boolean = false
  override def isHellish: Boolean = false
  override def isSealed: Boolean = false
  override def getProductionModifier(iBeeGenome: IBeeGenome, v: Float): Float = v
  override def getMutationModifier(iBeeGenome: IBeeGenome, iBeeGenome1: IBeeGenome, v: Float): Float = v
  override def getLifespanModifier(iBeeGenome: IBeeGenome, iBeeGenome1: IBeeGenome, v: Float): Float = v
  override def getGeneticDecay(iBeeGenome: IBeeGenome, v: Float): Float = v
  override def getFloweringModifier(iBeeGenome: IBeeGenome, v: Float): Float = v
  override def getTerritoryModifier(iBeeGenome: IBeeGenome, v: Float): Float = v

  override def setCondition(condition: Boolean, errorState: IErrorState): Boolean = false
  override def hasErrors: Boolean = false
  override def writeData(data: DataOutputStream): Unit = {}
  override def readData(data: DataInputStream): Unit = {}
  override def contains(state: IErrorState): Boolean = false
  override def clearErrors(): Unit = {}
  override def getErrorStates: ImmutableSet[IErrorState] = ImmutableSet.of()

  override def getBiome = getWorldObj.getBiomeGenForCoords(getCoordinates)
  override def getHumidity: EnumHumidity = EnumHumidity.getFromValue(getBiome.getRainfall)
  override def getTemperature: EnumTemperature = EnumTemperature.getFromValue(getBiome.getTemperature)

  override def getBeeFXCoordinates: Vec3d = {
    val coord = getCoordinates
    new Vec3d(coord.getX + 0.5, coord.getY + 1.5, coord.getZ + 0.5)
  }
}
