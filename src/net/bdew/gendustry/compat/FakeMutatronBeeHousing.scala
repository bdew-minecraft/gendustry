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
import net.minecraft.util.{ChunkCoordinates, Vec3}
import net.minecraft.world.World

class FakeMutatronBeeHousing(tile: TileEntity with IMutatron) extends IBeeHousing with IBeeModifier with IBeeListener with IBeeHousingInventory with IErrorLogic {
  val beeRoot = AlleleManager.alleleRegistry.getSpeciesRoot("rootBees").asInstanceOf[IBeeRoot]

  override def getWorld: World = tile.getWorldObj
  override def getCoordinates: ChunkCoordinates = new ChunkCoordinates(tile.xCoord, tile.yCoord, tile.zCoord)

  override def getBlockLightValue: Int = getWorld.getBlockLightValue(tile.xCoord, tile.yCoord, tile.zCoord)
  override def canBlockSeeTheSky: Boolean = getWorld.canBlockSeeTheSky(tile.xCoord, tile.yCoord, tile.zCoord)

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

  override def getBiome = tile.getWorldObj.getBiomeGenForCoordsBody(tile.xCoord, tile.zCoord)
  override def getHumidity: EnumHumidity = EnumHumidity.getFromValue(getBiome.rainfall)
  override def getTemperature: EnumTemperature = EnumTemperature.getFromValue(getBiome.temperature)

  override def getBeeFXCoordinates: Vec3 = Vec3.createVectorHelper(tile.xCoord + 0.5, tile.yCoord + 0.5, tile.zCoord + 0.5)
}
