/*
 * Copyright (c) bdew, 2013
 * https://github.com/bdew/teleporter
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/teleporter/master/MMPL-1.0.txt
 */

package net.bdew.gendustry

import _root_.forestry.api.genetics.{IAlleleSpecies, AlleleManager}
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.command.WrongUsageException
import net.minecraft.item.ItemStack
import java.util.List
import net.bdew.gendustry.config.Items
import net.bdew.gendustry.forestry.GeneSampleInfo

class CommandGiveTemplate extends CommandBase {
  def getCommandName = "givetemplate"
  override def getRequiredPermissionLevel = 2
  def getCommandUsage(icommandsender: ICommandSender) = "gendustry.givetemplate.usage"

  import scala.collection.JavaConversions._

  lazy val validSpecies =
    AlleleManager.alleleRegistry.getRegisteredAlleles
      .filter(_._2.isInstanceOf[IAlleleSpecies])
      .map(_._2.getUID)
      .toList

  def processCommand(sender: ICommandSender, params: Array[String]) {
    if (params.size != 1)
      throw new WrongUsageException("gendustry.givetemplate.usage")

    val player = CommandBase.getCommandSenderAsPlayer(sender)
    val uid = params(0)

    if (!validSpecies.contains(uid))
      throw new WrongUsageException("gendustry.givetemplate.usage")

    val root = AlleleManager.alleleRegistry.getAllele(uid).asInstanceOf[IAlleleSpecies].getRoot
    val template = root.getTemplate(uid)
    val item = new ItemStack(Items.geneTemplate)

    for ((allele, chromosome) <- template.zipWithIndex if allele != null)
      Items.geneTemplate.addSample(item, GeneSampleInfo(root, chromosome, allele))

    val entityitem = player.dropPlayerItem(item)
    entityitem.delayBeforeCanPickup = 0

    CommandBase.notifyAdmins(sender, "gendustry.givetemplate.success", uid, player.username)
  }

  override def addTabCompletionOptions(sender: ICommandSender, params: Array[String]): List[_] = {
    if (params.length == 1)
      return CommandBase.getListOfStringsMatchingLastWord(params, validSpecies: _*)
    return null
  }
}