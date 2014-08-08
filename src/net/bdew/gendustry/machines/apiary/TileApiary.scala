/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.machines.apiary

import forestry.api.apiculture._
import forestry.api.arboriculture.EnumGermlingType
import forestry.api.core.{EnumHumidity, EnumTemperature}
import forestry.api.genetics.{AlleleManager, IIndividual}
import net.bdew.gendustry.api.ApiaryModifiers
import net.bdew.gendustry.api.blocks.IIndustrialApiary
import net.bdew.gendustry.api.items.IApiaryUpgrade
import net.bdew.gendustry.compat.triggers.ForestryErrorSource
import net.bdew.gendustry.config.Config
import net.bdew.gendustry.gui.rscontrol.TileRSContollable
import net.bdew.gendustry.power.TilePowered
import net.bdew.lib.Misc
import net.bdew.lib.covers.TileCoverable
import net.bdew.lib.data.{DataSlotBoolean, DataSlotFloat, DataSlotInt, _}
import net.bdew.lib.data.base.{TileDataSlots, UpdateKind}
import net.bdew.lib.items.ItemUtils
import net.bdew.lib.power.DataSlotPower
import net.bdew.lib.tile.TileExtended
import net.bdew.lib.tile.inventory.{BreakableInventoryTile, PersistentInventoryTile, SidedInventory}
import net.minecraft.item.ItemStack
import net.minecraft.world.biome.BiomeGenBase
import net.minecraftforge.common.util.ForgeDirection

class TileApiary extends TileExtended
with TileDataSlots
with PersistentInventoryTile
with SidedInventory
with BreakableInventoryTile
with TilePowered
with ForestryErrorSource
with TileRSContollable
with TileCoverable
with IIndustrialApiary {

  object slots {
    val queen = 0
    val drone = 1
    val bees = 0 to 1
    val upgrades = 2 to 5
    val output = 6 to 14
  }

  var mods = new ApiaryModifiers
  var movePrincess = false

  val beeRoot = AlleleManager.alleleRegistry.getSpeciesRoot("rootBees").asInstanceOf[IBeeRoot]
  val logic = beeRoot.createBeekeepingLogic(this)
  lazy val cfg = MachineApiary

  val power = DataSlotPower("power", this)
  val errorState = DataSlotInt("error", this).setUpdate(UpdateKind.GUI, UpdateKind.SAVE, UpdateKind.WORLD)
  val owner = DataSlotGameProfile("owner", this).setUpdate(UpdateKind.SAVE)
  val guiProgress = DataSlotFloat("progress", this)
  val guiBreeding = DataSlotFloat("breeding", this)

  configurePower(cfg)

  // for client rendering and fx
  val hasLight = DataSlotBoolean("haslight", this).setUpdate(UpdateKind.WORLD)
  val queen = DataSlotItemStack("queen", this).setUpdate(UpdateKind.WORLD)

  persistLoad.listen(x => {
    updateModifiers()
    queen := getStackInSlot(slots.queen)
  })

  def updateModifiers() {
    mods = new ApiaryModifiers
    for (upgrade <- Misc.iterSome(inv, slots.upgrades).filter(isUpgrade))
      getUpgrade(upgrade).applyModifiers(mods, upgrade)
    hasLight := mods.isSelfLighted
  }

  override def setInventorySlotContents(slot: Int, stack: ItemStack) = {
    if (slot == slots.queen) queen := stack
    super.setInventorySlotContents(slot, stack)
  }

  override def markDirty() {
    updateModifiers()
    super.markDirty()
  }

  def doMovePrincess() {
    for ((slot, stack) <- Misc.iterSomeEnum(inv, slots.output) if stack != null && beeRoot.isMember(stack, EnumBeeType.PRINCESS.ordinal())) {
      setInventorySlotContents(slots.queen, stack)
      setInventorySlotContents(slot, null)
      return
    }
  }

  clientTick.listen(() =>
    if (beeRoot.isMated(queen) && (errorState :== 1)) {
      beeRoot.getMember(queen).doFX(logic.getEffectData, this)
    }
  )

  serverTick.listen(() => {
    movePrincess = false

    if (!canWork) {
      setErrorState(-2)
    } else if (power.stored >= cfg.baseMjPerTick * mods.energy) {
      logic.update()
      if ((logic.getQueen != null || logic.getBreedingTime > 0) && (errorState :== 1))
        power.extract(cfg.baseMjPerTick * mods.energy, false)
    } else {
      setErrorState(-1)
    }

    if (movePrincess && getStackInSlot(slots.queen) == null)
      doMovePrincess()

    if (logic.getQueen != null)
      guiProgress := 1 - (logic.getQueen.getHealth.toFloat / logic.getQueen.getMaxHealth)
    else
      guiProgress := -1

    guiBreeding := logic.getBreedingTime.toFloat / logic.getTotalBreedingTime
  })

  def getUpgrade(stack: ItemStack) = stack.getItem.asInstanceOf[IApiaryUpgrade]
  def isUpgrade(stack: ItemStack) = stack != null && stack.getItem != null && stack.getItem.isInstanceOf[IApiaryUpgrade]

  def getMaxAdditionalUpgrades(stack: ItemStack): Int = {
    if (!isUpgrade(stack)) return 0
    var existing = 0
    val thisId = getUpgrade(stack).getStackingId(stack)
    for (upgrade <- Misc.iterSome(inv, slots.upgrades).filter(isUpgrade)) {
      if (getUpgrade(upgrade).getStackingId(upgrade) == thisId)
        existing += upgrade.stackSize
    }
    return getUpgrade(stack).getMaxNumber(stack) - existing
  }

  override def isItemValidForSlot(slot: Int, stack: ItemStack): Boolean = {
    if (stack == null || stack.getItem == null) return false
    if (slots.upgrades.contains(slot)) {
      return getMaxAdditionalUpgrades(stack) >= stack.stackSize
    } else if (slot == slots.queen) {
      return beeRoot.isMember(stack, EnumBeeType.QUEEN.ordinal()) || beeRoot.isMember(stack, EnumBeeType.PRINCESS.ordinal())
    } else if (slot == slots.drone) {
      return beeRoot.isMember(stack, EnumBeeType.DRONE.ordinal())
    } else
      return false
  }

  def getStats = {
    var strings = List.empty[String]

    strings :+= Misc.toLocalF("gendustry.label.energy", "%.1f".format(cfg.baseMjPerTick * mods.energy * Config.powerShowMultiplier), Config.powerShowUnits)
    strings :+= Misc.toLocalF("gendustry.label.temperature", Misc.toLocal(getTemperature.getName))
    strings :+= Misc.toLocalF("gendustry.label.humidity", Misc.toLocal(getHumidity.getName))

    if (queen.cval != null) {
      val bee = beeRoot.getMember(queen)
      if (bee != null && bee.isAnalyzed) {
        val genome = bee.getGenome
        strings :+= Misc.toLocalF("gendustry.label.production", "%.0f%%".format(100F * mods.production * genome.getSpeed))
        strings :+= Misc.toLocalF("gendustry.label.flowering", "%.0f%%".format(mods.flowering * genome.getFlowering))
        strings :+= Misc.toLocalF("gendustry.label.lifespan", "%.0f%%".format(mods.lifespan * genome.getLifespan))
        val t = genome.getTerritory.toSeq.map(mods.territory * _)
        strings :+= Misc.toLocalF("gendustry.label.territory", "%.0f x %.0f x %.0f".format(t: _*))
      }
    }

    strings
  }

  // Misc
  def getBiome = worldObj.getBiomeGenForCoordsBody(xCoord, zCoord)
  def getPowerDataslot(from: ForgeDirection) = power
  def getSizeInventory = 15

  override def canInsertItem(slot: Int, stack: ItemStack, side: Int) = slots.bees.contains(slot) && isItemValidForSlot(slot, stack)
  override def canExtractItem(slot: Int, stack: ItemStack, side: Int) = slots.output.contains(slot)

  //IIndustrialApiary
  override def getUpgrades = {
    import scala.collection.JavaConversions._
    slots.upgrades map inv filterNot (_ == null)
  }

  override def getModifiers = mods

  // IBeeListener
  def onPostQueenDeath(queen: IBee) {
    if (mods.isAutomated) movePrincess = true
  }

  def onQueenDeath(queen: IBee) {}
  def wearOutEquipment(amount: Int) {}
  def onQueenChange(stack: ItemStack) {}
  def onEggLaid(queen: IBee) = false
  def onPollenRetrieved(queen: IBee, pollen: IIndividual, isHandled: Boolean): Boolean = {
    if (isHandled) return true
    if (!mods.isCollectingPollen) return false
    val sproot = pollen.getGenome.getSpeciesRoot
    val stack = sproot.getMemberStack(pollen, EnumGermlingType.POLLEN.ordinal())
    addProduct(stack, true)
    return true
  }

  // IBeeModifier
  def getTerritoryModifier(genome: IBeeGenome, currentModifier: Float) = Misc.min(mods.territory, 5)
  def getMutationModifier(genome: IBeeGenome, mate: IBeeGenome, currentModifier: Float) = mods.mutation
  def getLifespanModifier(genome: IBeeGenome, mate: IBeeGenome, currentModifier: Float) = mods.lifespan
  def getProductionModifier(genome: IBeeGenome, currentModifier: Float) = mods.production
  def getFloweringModifier(genome: IBeeGenome, currentModifier: Float) = mods.flowering
  def getGeneticDecay(genome: IBeeGenome, currentModifier: Float) = mods.geneticDecay
  def isSealed = mods.isSealed
  def isSelfLighted = mods.isSelfLighted
  def isSunlightSimulated = mods.isSunlightSimulated
  def isHellish = getModifiedBiome == BiomeGenBase.hell

  // IBeeHousing
  def setQueen(itemstack: ItemStack) = setInventorySlotContents(0, itemstack)
  def setDrone(itemstack: ItemStack) = setInventorySlotContents(1, itemstack)
  def getQueen = getStackInSlot(slots.queen)
  def getDrone = getStackInSlot(slots.drone)
  def canBreed = true

  def getModifiedBiome = if (mods.biomeOverride == null) getBiome else mods.biomeOverride

  // IHousing
  def setErrorState(state: Int) = errorState := state
  def getOwnerName = owner
  def getXCoord = xCoord
  def getYCoord = yCoord
  def getZCoord = zCoord
  def getBiomeId = getModifiedBiome.biomeID
  def getTemperature =
    if (EnumTemperature.isBiomeHellish(getModifiedBiome))
      EnumTemperature.HELLISH
    else
      EnumTemperature.getFromValue(getModifiedBiome.temperature + mods.temperature)
  def getHumidity = EnumHumidity.getFromValue(getModifiedBiome.rainfall + mods.humidity)
  def getErrorOrdinal = errorState
  def addProduct(product: ItemStack, all: Boolean): Boolean = {
    var p = product
    if (mods.isAutomated && beeRoot.isMember(product))
      p = ItemUtils.addStackToSlots(p, this, slots.bees, false)
    p = ItemUtils.addStackToSlots(p, this, slots.output, false)
    return p == null
  }

  override def validate() {
    super.validate()
    if (worldObj != null && !worldObj.isRemote)
      Sanity.check(this)
  }

  override def isValidCover(side: ForgeDirection, cover: ItemStack) = true
}
