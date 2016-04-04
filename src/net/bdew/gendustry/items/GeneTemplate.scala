/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.items

import java.util

import forestry.api.genetics.{AlleleManager, IAlleleSpecies, ISpeciesRoot}
import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.forestry.{GeneSampleInfo, GeneticsHelper}
import net.bdew.gendustry.misc.GendustryCreativeTabs
import net.bdew.lib.PimpVanilla._
import net.bdew.lib.items.BaseItem
import net.bdew.lib.{Client, Misc}
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.nbt.{NBTTagCompound, NBTTagList}
import net.minecraft.util.EnumChatFormatting

object GeneTemplate extends BaseItem("GeneTemplate") {
  setMaxStackSize(1)

  override def getCreativeTabs = Array(GendustryCreativeTabs.main, GendustryCreativeTabs.templates)

  override def getSubItems(item: Item, tab: CreativeTabs, list: util.List[ItemStack]) {
    import scala.collection.JavaConversions._
    tab match {
      case GendustryCreativeTabs.main => list.add(new ItemStack(this))
      case GendustryCreativeTabs.templates =>
        list.addAll(
          Misc.filterType(AlleleManager.alleleRegistry.getRegisteredAlleles.values(), classOf[IAlleleSpecies])
            .filter(sp => sp.getRoot.getTemplate(sp.getUID) != null)
            .toList.sortBy(_.getUID)
            .map(sp => GeneticsHelper.templateFromSpeciesUID(sp.getUID)))
      case _ =>
    }
  }

  def getRequiredChromosomes(sp: ISpeciesRoot) = GeneticsHelper.getCleanKaryotype(sp).keys

  def isComplete(stack: ItemStack) = {
    val sp = getSpecies(stack)
    if (sp == null)
      false
    else
      (getRequiredChromosomes(sp).toSet -- getSamples(stack).map(_.chromosome).toSet).isEmpty
  }

  def getSpecies(stack: ItemStack): ISpeciesRoot =
    if (stack.hasTagCompound) AlleleManager.alleleRegistry.getSpeciesRoot(stack.getTagCompound.getString("species")) else null

  def getSamples(stack: ItemStack): Iterable[GeneSampleInfo] = {
    val tag = stack.getTagCompound
    if (tag != null)
      return tag.getList[NBTTagCompound]("samples") map GeneSampleInfo.fromNBT
    else
      return Seq.empty[GeneSampleInfo]
  }

  def addSample(stack: ItemStack, sample: GeneSampleInfo): Boolean = {
    val tag = if (stack.hasTagCompound) {
      stack.getTagCompound
    } else {
      val newTag = new NBTTagCompound()
      newTag.setString("species", sample.root.getUID)
      newTag.setTag("samples", new NBTTagList())
      stack.setTagCompound(newTag)
      newTag
    }
    if (tag.getString("species") != sample.root.getUID) return false
    val samples = new NBTTagList()
    for (s <- getSamples(stack) if s.chromosome != sample.chromosome) {
      val t = new NBTTagCompound()
      s.writeToNBT(t)
      samples.appendTag(t)
    }
    val stag = new NBTTagCompound()
    sample.writeToNBT(stag)
    samples.appendTag(stag)
    tag.setTag("samples", samples)
    return true
  }

  override def addInformation(stack: ItemStack, playerIn: EntityPlayer, tooltip: util.List[String], advanced: Boolean): Unit = {
    import scala.collection.JavaConversions._
    val tag = stack.getTagCompound
    if (tag != null && tag.hasKey("species")) {
      try {
        tooltip += Misc.toLocal("gendustry.label.template." + tag.getString("species"))
        val root = getSpecies(stack)
        val samples = getSamples(stack).map(x => x.chromosome -> x.getLocalizedName).toMap
        val required = getRequiredChromosomes(root)

        tooltip +=
          (if (isComplete(stack)) EnumChatFormatting.GREEN else EnumChatFormatting.RED) +
            Misc.toLocalF("gendustry.label.template.chromosomes", samples.size, required.size) +
            EnumChatFormatting.RESET

        for (chr <- required)
          if (samples.contains(chr))
            tooltip += samples(chr)
          else if (Client.shiftDown)
            tooltip += "%s%s: %s%s".format(
              EnumChatFormatting.RED,
              Misc.toLocal("gendustry.chromosome." + GeneSampleInfo.getChromosomeName(root, chr)),
              Misc.toLocal("gendustry.label.template.missing"),
              EnumChatFormatting.RESET
            )

      } catch {
        case e: Throwable =>
          Gendustry.logWarnException("Exception while generating template tooltip", e)
          tooltip += "Error"
      }
    } else {
      tooltip += Misc.toLocal("gendustry.label.template.blank")
    }
  }
}
