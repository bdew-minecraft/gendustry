/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.nei

import codechicken.nei.forge.IContainerTooltipHandler
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.item.ItemStack
import java.util
import codechicken.nei.recipe.{FurnaceRecipeHandler, GuiRecipe}
import net.bdew.gendustry.config.Items
import net.bdew.lib.Misc

class SmeltingTooltipHandler extends IContainerTooltipHandler {
  def handleTooltipFirst(gui: GuiContainer, mousex: Int, mousey: Int, currenttip: util.List[String]) = currenttip
  def handleItemTooltip(gui: GuiContainer, itemstack: ItemStack, currenttip: util.List[String]) = {
    if (gui.isInstanceOf[GuiRecipe]) {
      val gr = gui.asInstanceOf[GuiRecipe]
      val handler = gr.currenthandlers.get(gr.recipetype)
      if (handler.isInstanceOf[FurnaceRecipeHandler]) {
        if (itemstack.itemID == Items.geneSample.itemID)
          currenttip.add(Misc.toLocal("gendustry.label.erase"))
        if (itemstack.itemID == Items.geneTemplate.itemID)
          currenttip.add(Misc.toLocal("gendustry.label.erase"))
      }
    }
    currenttip
  }
}
