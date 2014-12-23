/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.nei

import java.util

import codechicken.nei.guihook.IContainerTooltipHandler
import codechicken.nei.recipe.{FurnaceRecipeHandler, GuiRecipe}
import net.bdew.gendustry.items.{GeneSample, GeneTemplate}
import net.bdew.lib.Misc
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.item.ItemStack

class SmeltingTooltipHandler extends IContainerTooltipHandler {
  override def handleItemDisplayName(gui: GuiContainer, stack: ItemStack, tip: util.List[String]) = tip
  override def handleTooltip(gui: GuiContainer, mouseX: Int, mouseY: Int, tip: util.List[String]) = tip
  override def handleItemTooltip(gui: GuiContainer, itemStack: ItemStack, mouseX: Int, mouseY: Int, tip: util.List[String]) = {
    if (gui.isInstanceOf[GuiRecipe] && itemStack != null) {
      val gr = gui.asInstanceOf[GuiRecipe]
      val handler = gr.currenthandlers.get(gr.recipetype)
      if (handler.isInstanceOf[FurnaceRecipeHandler]) {
        if (itemStack.getItem == GeneSample)
          tip.add(Misc.toLocal("gendustry.label.erase"))
        if (itemStack.getItem == GeneTemplate)
          tip.add(Misc.toLocal("gendustry.label.erase"))
      }
    }
    tip
  }
}
