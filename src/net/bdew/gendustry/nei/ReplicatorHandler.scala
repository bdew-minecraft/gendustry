/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.nei

import net.bdew.lib.gui.Rect
import net.bdew.gendustry.config.{Fluids, Items, Machines}
import net.minecraftforge.fluids.FluidStack
import net.bdew.gendustry.nei.helpers.{PowerComponent, FluidComponent}
import net.minecraft.item.ItemStack
import net.bdew.gendustry.Gendustry
import net.bdew.lib.Misc
import net.bdew.gendustry.forestry.GeneticsHelper

class ReplicatorHandler extends BaseRecipeHandler(5, 13) {
  val dnaRect = Rect(32, 19, 16, 58)
  val proteinRect = Rect(56, 19, 16, 58)
  val mjRect = new Rect(8, 19, 16, 58)

  import scala.collection.JavaConversions._

  class ReplicatorRecipe(template: ItemStack, out: ItemStack) extends CachedRecipeWithComponents {
    val getResult = position(out, 142, 41)
    val templateStack = position(template, 98, 17)

    components :+= new FluidComponent(dnaRect, new FluidStack(Fluids.dna, Machines.replicator.dnaPerItem), Machines.replicator.dnaTankSize)
    components :+= new FluidComponent(proteinRect, new FluidStack(Fluids.protein, Machines.replicator.proteinPerItem), Machines.replicator.proteinTankSize)
    components :+= new PowerComponent(mjRect, Machines.replicator.mjPerItem, Machines.replicator.maxStoredEnergy)

    override def getOtherStacks = List(templateStack)
  }

  def addRecipe(tpl: ItemStack) {
    arecipes.add(new ReplicatorRecipe(tpl, GeneticsHelper.individualFromTemplate(tpl, Machines.replicator.makePristineBees)))
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
      case ("item", Seq(x: ItemStack)) if x.itemID == Items.geneTemplate.itemID && Items.geneTemplate.isComplete(x) => addRecipe(x)
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
