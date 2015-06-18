/*
 * Copyright (c) bdew, 2013 - 2015
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.compat

import com.mojang.authlib.GameProfile
import forestry.api.apiculture.{IBee, IBeeGenome, IBeeHousing}
import forestry.api.core.{EnumHumidity, EnumTemperature, ErrorStateRegistry, IErrorState}
import forestry.api.genetics.IIndividual
import net.bdew.gendustry.api.blocks.IMutatron
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

class FakeMutatronBeeHousing(tile: TileEntity with IMutatron) extends IBeeHousing {
  override def getWorld: World = tile.getWorldObj
  override def getXCoord: Int = tile.xCoord
  override def getYCoord: Int = tile.yCoord
  override def getZCoord: Int = tile.zCoord

  override def getOwnerName: GameProfile = tile.getOwner

  override def getDrone: ItemStack = tile.getParent1
  override def getQueen: ItemStack = tile.getParent2

  override def setQueen(itemStack: ItemStack): Unit = throw new UnsupportedOperationException("Not supported in mutatron")
  override def setDrone(itemStack: ItemStack): Unit = throw new UnsupportedOperationException("Not supported in mutatron")
  override def canBreed: Boolean = true

  override def onQueenDeath(iBee: IBee): Unit = {}
  override def wearOutEquipment(i: Int): Unit = {}
  override def onPostQueenDeath(iBee: IBee): Unit = {}
  override def onQueenChange(itemStack: ItemStack): Unit = {}
  override def onEggLaid(iBee: IBee): Boolean = false
  override def onPollenRetrieved(iBee: IBee, iIndividual: IIndividual, b: Boolean): Boolean = false
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

  override def getErrorOrdinal: Int = 0
  override def setErrorState(state: Int): Unit = {}
  override def setErrorState(state: IErrorState): Unit = {}
  override def getErrorState: IErrorState = ErrorStateRegistry.getErrorState("Forestry:ok")

  override def getBiome = tile.getWorldObj.getBiomeGenForCoordsBody(tile.xCoord, tile.zCoord)
  override def getBiomeId: Int = getBiome.biomeID
  override def getHumidity: EnumHumidity = EnumHumidity.getFromValue(getBiome.rainfall)
  override def getTemperature: EnumTemperature = EnumTemperature.getFromValue(getBiome.temperature)
}
