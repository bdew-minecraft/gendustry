/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry

import _root_.forestry.api.genetics.AlleleManager
import net.minecraft.command.{CommandBase, ICommandSender}
import cpw.mods.fml.relauncher.FMLInjectionData
import java.io.{FileWriter, BufferedWriter, File}

class CommandDumpAlleles extends CommandBase {
  def getCommandName = "dumpalleles"
  override def getRequiredPermissionLevel = 2
  def getCommandUsage(icommandsender: ICommandSender) = "dumpalleles"

  def processCommand(sender: ICommandSender, params: Array[String]) {
    val mcHome = FMLInjectionData.data()(6).asInstanceOf[File] //is there a better way to get this?
    val dumpFile = new File(mcHome, "alleles.dump")
    val dumpWriter = new BufferedWriter(new FileWriter(dumpFile))
    import scala.collection.JavaConversions._
    try {
      dumpWriter.write("==== ALLELES ====\n")
      dumpWriter.write(AlleleManager.alleleRegistry.getRegisteredAlleles.keySet().toList.sorted.mkString("\n"))
      dumpWriter.write("\n\n")
      CommandBase.notifyAdmins(sender, "Alleles dumped to " + dumpFile.getCanonicalPath)
    } catch {
      case e: Throwable =>
        CommandBase.notifyAdmins(sender, "Failed to save registry dump: " + e)
        Gendustry.logErrorException("Failed to save registry dump", e)
    } finally {
      dumpWriter.close()
    }
  }
}