/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.nei

import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.item.ItemStack
import java.util
import codechicken.nei.recipe.{FurnaceRecipeHandler, GuiRecipe}
import net.bdew.lib.Misc
import codechicken.nei.guihook.IContainerTooltipHandler
import net.bdew.gendustry.items.{GeneTemplate, GeneSample}

class SmeltingTooltipHandler extends IContainerTooltipHandler {
  override def handleItemDisplayName(gui: GuiContainer, itemstack: ItemStack, currenttip: util.List[String]) = currenttip
  override def handleTooltip(gui: GuiContainer, mousex: Int, mousey: Int, currenttip: util.List[String]) = currenttip
  override def handleItemTooltip(gui: GuiContainer, itemstack: ItemStack, mousex: Int, mousey: Int, currenttip: util.List[String]) = {
    if (gui.isInstanceOf[GuiRecipe] && itemstack != null) {
      val gr = gui.asInstanceOf[GuiRecipe]
      val handler = gr.currenthandlers.get(gr.recipetype)
      if (handler.isInstanceOf[FurnaceRecipeHandler]) {
        if (itemstack.getItem == GeneSample)
          currenttip.add(Misc.toLocal("gendustry.label.erase"))
        if (itemstack.getItem == GeneTemplate)
          currenttip.add(Misc.toLocal("gendustry.label.erase"))
      }
    }
    currenttip
  }
}
