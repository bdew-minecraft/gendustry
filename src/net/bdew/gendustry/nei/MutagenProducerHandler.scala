/*
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.nei

import net.bdew.gendustry.Gendustry
import net.bdew.lib.gui.{Rect, Point}
import net.bdew.gendustry.config.{Fluids, Blocks, Machines}
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidStack
import net.bdew.gendustry.mutagen.MutagenRegistry
import codechicken.nei.recipe.TemplateRecipeHandler.RecipeTransferRect
import java.awt.Rectangle
import net.bdew.gendustry.nei.helpers.{PowerComponent, FluidComponent}
import net.bdew.lib.Misc

class MutagenProducerHandler extends BaseRecipeHandler {
  lazy val offset = new Point(5, 13)
  val mutagenRect = new Rect(152, 19, 16, 58)
  val mjRect = new Rect(8, 19, 16, 58)

  class MutagenProducerRecipe(val in: ItemStack, val out: Int) extends CachedRecipeWithComponents {

    import scala.collection.JavaConversions._

    val inPositioned = position(in, 44, 41)
    val getResult = null

    components :+= new FluidComponent(mutagenRect, new FluidStack(Fluids.mutagen, out), Machines.mutagenProducer.tankSize)
    components :+= new PowerComponent(mjRect, Machines.mutagenProducer.mjPerItem, Machines.mutagenProducer.maxStoredEnergy)

    override def getOtherStacks = List(inPositioned)
  }

  def getRecipe(i: Int) = arecipes.get(i).asInstanceOf[MutagenProducerRecipe]

  override def loadTransferRects() {
    transferRects.add(new RecipeTransferRect(new Rectangle(79 - offset.x, 41 - offset.y, 53, 15), "MutagenProducer"))
  }

  def addAllRecipes() {
    for ((id, vals) <- MutagenRegistry.values; (meta, out) <- vals)
      arecipes.add(new MutagenProducerRecipe(new ItemStack(id, 1, meta), out))
  }

  override def loadCraftingRecipes(outputId: String, results: AnyRef*): Unit = {
    Some(outputId, results) collect {
      case ("liquid", Seq(x: FluidStack)) if x.fluidID == Fluids.mutagen.getID => addAllRecipes()
      case ("item", Seq(x: ItemStack)) if x.itemID == Blocks.mutagen.blockID => addAllRecipes()
      case ("MutagenProducer", _) => addAllRecipes()
    }
  }

  override def loadUsageRecipes(stack: ItemStack) {
    val res = MutagenRegistry.getValue(stack)
    if (res > 0) {
      arecipes.add(new MutagenProducerRecipe(stack, res))
    }
  }

  def getGuiTexture = Gendustry.modId + ":textures/gui/mutagenproducer.png"
  def getRecipeName = Misc.toLocal("tile.gendustry.mutagen.producer.name")
}
