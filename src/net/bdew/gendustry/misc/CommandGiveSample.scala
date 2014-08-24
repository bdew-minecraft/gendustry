/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.misc

import java.util.List

import _root_.forestry.api.genetics.{AlleleManager, IAllele, ISpeciesRoot}
import net.bdew.gendustry.forestry.{GeneSampleInfo, GeneticsHelper}
import net.bdew.gendustry.items.GeneSample
import net.minecraft.command.{CommandBase, CommandException, ICommandSender, WrongUsageException}

class CommandGiveSample extends CommandBase {
  def getCommandName = "givesample"
  override def getRequiredPermissionLevel = 2
  def getCommandUsage(icommandsender: ICommandSender) = "gendustry.givesample.usage"

  import scala.collection.JavaConversions._

  lazy val validRoots = AlleleManager.alleleRegistry.getSpeciesRoot.map(_._1).toList
  lazy val validAlleles = AlleleManager.alleleRegistry.getRegisteredAlleles.map(_._1).toList

  def validChromosomes(root: ISpeciesRoot) =
    (GeneticsHelper.getCleanKaryotype(root) map { case (n, c) => c.toString.toLowerCase -> c.ordinal() }).toSeq

  def isValidAllele(root: ISpeciesRoot, chromosome: Int, allele: IAllele) =
    GeneticsHelper.getCleanKaryotype(root) get chromosome exists { chr =>
      chr.getAlleleClass.isInstance(allele)
    }

  def processCommand(sender: ICommandSender, params: Array[String]) {
    if (params.size != 3)
      throw new WrongUsageException("gendustry.givesample.usage")

    val player = CommandBase.getCommandSenderAsPlayer(sender)
    val rootUid = params(0)
    val chromosomeName = params(1)
    val alleleUid = params(2)

    if (!validRoots.contains(rootUid)) throw new CommandException("gendustry.givesample.error.root", rootUid)
    if (!validAlleles.contains(alleleUid)) throw new CommandException("gendustry.givesample.error.allele", alleleUid)

    val root = AlleleManager.alleleRegistry.getSpeciesRoot(rootUid)
    val chromosome = validChromosomes(root).toMap.getOrElse(chromosomeName, throw new CommandException("gendustry.givesample.error.chromosome", chromosomeName))
    val allele = AlleleManager.alleleRegistry.getAllele(alleleUid)

    if (!isValidAllele(root, chromosome, allele)) throw new CommandException("gendustry.givesample.error.invalid")

    val sample = GeneSample.newStack(GeneSampleInfo(root, chromosome, allele))
    val entityitem = player.entityDropItem(sample, 0)
    entityitem.delayBeforeCanPickup = 0

    CommandBase.func_152373_a(sender, this, "gendustry.givesample.success", rootUid, chromosomeName, alleleUid, player.getDisplayName)
  }

  override def addTabCompletionOptions(sender: ICommandSender, params: Array[String]): List[_] = {
    params.toSeq match {
      case Seq(rootUid) => CommandBase.getListOfStringsMatchingLastWord(params, validRoots: _*)
      case Seq(rootUid, chromosomeName) =>
        if (validRoots.contains(rootUid)) {
          val root = AlleleManager.alleleRegistry.getSpeciesRoot(rootUid)
          CommandBase.getListOfStringsMatchingLastWord(params, validChromosomes(root).map(_._1): _*)
        } else null
      case Seq(rootUid, chromosomeName, alleleUid) =>
        if (validRoots.contains(rootUid)) {
          val root = AlleleManager.alleleRegistry.getSpeciesRoot(rootUid)
          val chromosome = validChromosomes(root).toMap.getOrElse(chromosomeName, return null)
          val valid = AlleleManager.alleleRegistry.getRegisteredAlleles
            .filter({ case (n, a) => isValidAllele(root, chromosome, a) }).map(_._1).toSeq
          CommandBase.getListOfStringsMatchingLastWord(params, valid: _*)
        } else null
      case _ => null
    }
  }
}