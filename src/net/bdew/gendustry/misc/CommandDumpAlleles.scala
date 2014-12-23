/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.misc

import java.io.{BufferedWriter, File, FileWriter}

import _root_.forestry.api.genetics.AlleleManager
import cpw.mods.fml.relauncher.FMLInjectionData
import net.bdew.gendustry.Gendustry
import net.minecraft.command.{CommandBase, ICommandSender}

class CommandDumpAlleles extends CommandBase {
  def getCommandName = "dumpalleles"
  override def getRequiredPermissionLevel = 2
  def getCommandUsage(c: ICommandSender) = "dumpalleles"

  def processCommand(sender: ICommandSender, params: Array[String]) {
    val mcHome = FMLInjectionData.data()(6).asInstanceOf[File] //is there a better way to get this?
    val dumpFile = new File(mcHome, "alleles.dump")
    val dumpWriter = new BufferedWriter(new FileWriter(dumpFile))
    import scala.collection.JavaConversions._
    try {
      dumpWriter.write("==== ALLELES ====\n")
      dumpWriter.write((AlleleManager.alleleRegistry.getRegisteredAlleles map { case (id, allele) =>
        "%s (%s)".format(id, allele.getName)
      }).toList.sorted.mkString("\n"))
      dumpWriter.write("\n\n")
      CommandBase.func_152373_a(sender, this, "Alleles dumped to " + dumpFile.getCanonicalPath)
    } catch {
      case e: Throwable =>
        CommandBase.func_152373_a(sender, this, "Failed to save registry dump: " + e)
        Gendustry.logErrorException("Failed to save registry dump", e)
    } finally {
      dumpWriter.close()
    }
  }
}