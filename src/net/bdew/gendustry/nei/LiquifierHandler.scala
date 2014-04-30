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
import net.bdew.lib.gui.Rect
import net.bdew.gendustry.config.{Fluids, Machines}
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack
import net.bdew.gendustry.fluids.ProteinSources
import net.bdew.gendustry.nei.helpers.{PowerComponent, FluidComponent}
import net.bdew.lib.Misc

class LiquifierHandler extends BaseRecipeHandler(5, 13) {
  val proteinRect = new Rect(152, 19, 16, 58)
  val mjRect = new Rect(8, 19, 16, 58)

  class LiquifierRecipe(val in: ItemStack, val out: Int) extends CachedRecipeWithComponents {

    import scala.collection.JavaConversions._

    val inPositioned = position(in, 44, 41)
    val getResult = null

    components :+= new FluidComponent(proteinRect, new FluidStack(Fluids.protein, out), Machines.liquifier.tankSize)
    components :+= new PowerComponent(mjRect, Machines.liquifier.mjPerItem, Machines.liquifier.maxStoredEnergy)

    override def getOtherStacks = List(inPositioned)
  }

  def getRecipe(i: Int) = arecipes.get(i).asInstanceOf[LiquifierRecipe]

  override def loadTransferRects() {
    addTransferRect(Rect(79, 41, 53, 15), "Liquifier")
  }

  def addAllRecipes() {
    for ((id, vals) <- ProteinSources.values; (meta, out) <- vals)
      arecipes.add(new LiquifierRecipe(new ItemStack(id, 1, meta), out))
  }

  override def loadCraftingRecipes(outputId: String, results: AnyRef*): Unit = {
    Some(outputId, results) collect {
      case ("liquid", Seq(x: FluidStack)) if x.fluidID == Fluids.protein.getID => addAllRecipes()
      case ("item", Seq(x: ItemStack)) if x.itemID == Fluids.protein.getBlockID => addAllRecipes()
      case ("Liquifier", _) => addAllRecipes()
    }
  }

  override def loadUsageRecipes(stack: ItemStack) {
    val res = ProteinSources.getValue(stack)
    if (res > 0) {
      arecipes.add(new LiquifierRecipe(stack, res))
    }
  }

  def getGuiTexture = Gendustry.modId + ":textures/gui/liquifier.png"
  def getRecipeName = Misc.toLocal("tile.gendustry.liquifier.name")
}
