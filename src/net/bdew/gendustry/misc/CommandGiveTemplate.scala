/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.misc

import java.util
import java.util.List

import _root_.forestry.api.genetics.{AlleleManager, IAlleleSpecies}
import net.bdew.gendustry.forestry.GeneticsHelper
import net.minecraft.command.{CommandBase, ICommandSender, WrongUsageException}

class CommandGiveTemplate extends CommandBase {
  def getCommandName = "givetemplate"
  override def getRequiredPermissionLevel = 2
  def getCommandUsage(c: ICommandSender) = "gendustry.givetemplate.usage"

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

    val entity = player.entityDropItem(GeneticsHelper.templateFromSpeciesUID(uid), 0)
    entity.delayBeforeCanPickup = 0

    CommandBase.func_152373_a(sender, this, "gendustry.givetemplate.success", uid, player.getDisplayName)
  }

  override def addTabCompletionOptions(sender: ICommandSender, params: Array[String]): util.List[_] = {
    if (params.length == 1)
      return CommandBase.getListOfStringsMatchingLastWord(params, validSpecies: _*)
    return null
  }
}