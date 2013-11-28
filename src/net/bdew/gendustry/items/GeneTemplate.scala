/*
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.items

import net.minecraft.item.ItemStack
import net.bdew.gendustry.forestry.{GeneRecipe, GeneSampleInfo}
import net.bdew.lib.Misc
import net.minecraft.nbt.{NBTTagList, NBTTagCompound}
import net.minecraft.entity.player.EntityPlayer
import java.util
import cpw.mods.fml.common.registry.GameRegistry
import forestry.api.genetics.{ISpeciesRoot, AlleleManager}

class GeneTemplate(id: Int) extends SimpleItem(id, "GeneTemplate") {
  setMaxStackSize(1)

  GameRegistry.addRecipe(new GeneRecipe)

  override def getUnlocalizedName: String = super.getUnlocalizedName
  override def getUnlocalizedName(par1ItemStack: ItemStack): String = super.getUnlocalizedName

  def getSpecies(stack: ItemStack): ISpeciesRoot =
    if (stack.hasTagCompound) AlleleManager.alleleRegistry.getSpeciesRoot(stack.getTagCompound.getString("species")) else null

  def getSamples(stack: ItemStack): Iterable[GeneSampleInfo] = {
    val tag = stack.getTagCompound
    if (tag != null)
      return Misc.iterNbtList[NBTTagCompound](tag.getTagList("samples")).map(x => GeneSampleInfo.fromNBT(x))
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

  override def addInformation(stack: ItemStack, player: EntityPlayer, l: util.List[_], par4: Boolean) = {
    import scala.collection.JavaConverters._
    val tip = l.asInstanceOf[util.List[String]].asScala
    val tag = stack.getTagCompound
    if (tag != null && tag.hasKey("species")) {
      try {
        tip += Misc.toLocal("gendustry.label.template." + tag.getString("species"))
        tip ++= getSamples(stack).map(_.getText)
      } catch {
        case e: Throwable =>
          e.printStackTrace()
          tip += "Error"
      }
    } else {
      tip += Misc.toLocal("gendustry.label.template.blank")
    }
  }
}
