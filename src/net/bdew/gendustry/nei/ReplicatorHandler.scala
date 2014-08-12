/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.nei

import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.config.Fluids
import net.bdew.gendustry.forestry.GeneticsHelper
import net.bdew.gendustry.items.GeneTemplate
import net.bdew.gendustry.machines.replicator.MachineReplicator
import net.bdew.gendustry.nei.helpers.{FluidComponent, PowerComponent}
import net.bdew.lib.Misc
import net.bdew.lib.gui.Rect
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack

class ReplicatorHandler extends BaseRecipeHandler(5, 13) {
  val dnaRect = Rect(32, 19, 16, 58)
  val proteinRect = Rect(56, 19, 16, 58)
  val mjRect = new Rect(8, 19, 16, 58)

  import scala.collection.JavaConversions._

  class ReplicatorRecipe(template: ItemStack, out: ItemStack) extends CachedRecipeWithComponents {
    val getResult = position(out, 142, 41)
    val templateStack = position(template, 98, 17)

    components :+= new FluidComponent(dnaRect, new FluidStack(Fluids.dna, MachineReplicator.dnaPerItem), MachineReplicator.dnaTankSize)
    components :+= new FluidComponent(proteinRect, new FluidStack(Fluids.protein, MachineReplicator.proteinPerItem), MachineReplicator.proteinTankSize)
    components :+= new PowerComponent(mjRect, MachineReplicator.mjPerItem, MachineReplicator.maxStoredEnergy)

    override def getOtherStacks = List(templateStack)
  }

  def addRecipe(tpl: ItemStack) {
    arecipes.add(new ReplicatorRecipe(tpl, GeneticsHelper.individualFromTemplate(tpl, MachineReplicator.makePristineBees)))
  }

  def addRecipe(uid: String) {
    addRecipe(GeneticsHelper.templateFromSpeciesUID(uid))
  }

  def getRecipe(i: Int) = arecipes.get(i).asInstanceOf[ReplicatorRecipe]

  override def loadTransferRects() {
    addTransferRect(Rect(89, 41, 40, 15), "Replicator")
  }

  def addAllRecipes() {
    addRecipe("forestry.speciesForest")
    addRecipe("forestry.treeOak")
    addRecipe("forestry.lepiCabbageWhite")
  }

  override def loadUsageRecipes(outputId: String, results: AnyRef*): Unit = {
    Some(outputId, results) collect {
      case ("liquid", Seq(x: FluidStack)) if x.fluidID == Fluids.dna.getID => addAllRecipes()
      case ("liquid", Seq(x: FluidStack)) if x.fluidID == Fluids.protein.getID => addAllRecipes()
      case ("item", Seq(x: ItemStack)) if x.getItem == GeneTemplate && GeneTemplate.isComplete(x) => addRecipe(x)
      case ("Replicator", _) => addAllRecipes()
    }
  }

  override def loadCraftingRecipes(outputId: String, results: AnyRef*): Unit = {
    Some(outputId, results) collect {
      case ("Replicator", _) => addAllRecipes()
    }
  }

  def getGuiTexture = Gendustry.modId + ":textures/gui/replicator.png"
  def getRecipeName = Misc.toLocal("tile.gendustry.replicator.name")
}
