/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.misc

import java.io.{BufferedWriter, File, FileWriter}

import forestry.api.genetics.AlleleManager
import net.bdew.gendustry.Gendustry
import net.minecraft.command.{CommandBase, ICommandSender}
import net.minecraft.server.MinecraftServer
import net.minecraftforge.fml.relauncher.FMLInjectionData

class CommandDumpAlleles extends CommandBase {
  override def getName = "dumpalleles"
  override def getRequiredPermissionLevel = 2
  override def getUsage(c: ICommandSender) = "dumpalleles"

  override def execute(server: MinecraftServer, sender: ICommandSender, params: Array[String]): Unit = {
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
      CommandBase.notifyCommandListener(sender, this, "Alleles dumped to " + dumpFile.getCanonicalPath)
    } catch {
      case e: Throwable =>
        CommandBase.notifyCommandListener(sender, this, "Failed to save registry dump: " + e)
        Gendustry.logErrorException("Failed to save registry dump", e)
    } finally {
      dumpWriter.close()
    }
  }
}