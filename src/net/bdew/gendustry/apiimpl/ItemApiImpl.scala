/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.apiimpl

import net.minecraft.item.ItemStack
import net.bdew.gendustry.config.Items
import net.bdew.gendustry.api.items.IItemAPI
import net.bdew.gendustry.items.{GeneTemplate, GeneSample}

object ItemApiImpl extends IItemAPI {

  import scala.collection.JavaConversions._

  override def isEmptySample(stack: ItemStack) =
    stack.getItem == Items.geneSampleBlank

  override def isFullSample(stack: ItemStack) =
    stack.getItem == GeneSample

  override def isEmptyTemplate(stack: ItemStack) =
    stack.getItem == GeneTemplate && GeneTemplate.getSamples(stack).isEmpty

  override def isFullTemplate(stack: ItemStack) =
    stack.getItem == GeneTemplate && !GeneTemplate.getSamples(stack).isEmpty

  override def isCompleteTemplate(stack: ItemStack) =
    stack.getItem == GeneTemplate && GeneTemplate.isComplete(stack)

  override def getTemplateGenome(stack: ItemStack) = {
    val root = GeneTemplate.getSpecies(stack)
    val samples = GeneTemplate.getSamples(stack)
    val template = root.getDefaultTemplate
    samples.foreach(x => template(x.chromosome) = x.allele)
    root.templateAsGenome(template)
  }

  override def getTemplateSamples(stack: ItemStack) =
    GeneTemplate.getSamples(stack).toList

  override def getSampleInfo(stack: ItemStack) =
    GeneSample.getInfo(stack)

}
