/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry

import _root_.forestry.api.apiculture.{EnumBeeChromosome, IBeeRoot}
import _root_.forestry.api.arboriculture.{ITreeRoot, EnumTreeChromosome}
import _root_.forestry.api.genetics.{IAllele, ISpeciesRoot, AlleleManager}
import _root_.forestry.api.lepidopterology.{EnumButterflyChromosome, IButterflyRoot}
import net.minecraft.command.{CommandException, CommandBase, ICommandSender, WrongUsageException}
import java.util.List
import net.bdew.gendustry.forestry.GeneSampleInfo
import net.bdew.gendustry.config.Items

class CommandGiveSample extends CommandBase {
  def getCommandName = "givesample"
  override def getRequiredPermissionLevel = 2
  def getCommandUsage(icommandsender: ICommandSender) = "gendustry.givesample.usage"

  import scala.collection.JavaConversions._

  lazy val validRoots = AlleleManager.alleleRegistry.getSpeciesRoot.map(_._1).toList
  lazy val validAlleles = AlleleManager.alleleRegistry.getRegisteredAlleles.map(_._1).toList

  def validChromosomes(root: ISpeciesRoot) = root match {
    case x: IBeeRoot => EnumBeeChromosome.values().zipWithIndex.map({ case (c, n) => c.getName -> n })
    case x: ITreeRoot => EnumTreeChromosome.values().zipWithIndex.map({ case (c, n) => c.getName -> n })
    case x: IButterflyRoot => EnumButterflyChromosome.values().zipWithIndex.map({ case (c, n) => c.getName -> n })
  }

  def isValidAllele(root: ISpeciesRoot, chromosome: Int, allele: IAllele) = root match {
    case x: IBeeRoot => EnumBeeChromosome.values()(chromosome).getAlleleClass.isInstance(allele)
    case x: ITreeRoot => EnumTreeChromosome.values()(chromosome).getAlleleClass.isInstance(allele)
    case x: IButterflyRoot => EnumButterflyChromosome.values()(chromosome).getAlleleClass.isInstance(allele)
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

    val sample = Items.geneSample.newStack(GeneSampleInfo(root, chromosome, allele))
    val entityitem = player.dropPlayerItem(sample)
    entityitem.delayBeforeCanPickup = 0

    CommandBase.notifyAdmins(sender, "gendustry.givesample.success", rootUid, chromosomeName, alleleUid, player.username)
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