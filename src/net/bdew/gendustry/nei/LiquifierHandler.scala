/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.nei

import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.config.Fluids
import net.bdew.gendustry.fluids.ProteinSources
import net.bdew.gendustry.machines.liquifier.MachineLiquifier
import net.bdew.gendustry.nei.helpers.{FluidComponent, PowerComponent}
import net.bdew.lib.Misc
import net.bdew.lib.gui.Rect
import net.bdew.lib.items.IStackBlock
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack

class LiquifierHandler extends BaseRecipeHandler(5, 13) {
  val proteinRect = new Rect(152, 19, 16, 58)
  val mjRect = new Rect(8, 19, 16, 58)

  class LiquifierRecipe(val in: ItemStack, val out: Int) extends CachedRecipeWithComponents {

    import scala.collection.JavaConversions._

    val inPositioned = position(in, 44, 41)
    val getResult = null

    components :+= new FluidComponent(proteinRect, new FluidStack(Fluids.protein, out), MachineLiquifier.tankSize)
    components :+= new PowerComponent(mjRect, MachineLiquifier.mjPerItem, MachineLiquifier.maxStoredEnergy)

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
      case ("liquid", Seq(x: FluidStack)) if x.getFluid == Fluids.protein => addAllRecipes()
      case ("item", Seq(IStackBlock(x))) if x == Fluids.protein.getBlock => addAllRecipes()
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
