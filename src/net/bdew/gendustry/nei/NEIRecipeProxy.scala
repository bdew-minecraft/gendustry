/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.nei

import codechicken.nei.recipe.GuiCraftingRecipe
import net.bdew.lib.Misc

object NEIRecipeProxy {
  val hasNei = Misc.haveModVersion("NotEnoughItems")

  def openRecipes(id: String) {
    if (hasNei)
      GuiCraftingRecipe.openRecipeGui(id)
  }
}
