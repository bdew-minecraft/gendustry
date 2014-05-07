/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.waila

import mcp.mobius.waila.api.{IWailaConfigHandler, IWailaDataAccessor, IWailaDataProvider}
import net.minecraft.item.ItemStack
import net.bdew.gendustry.Gendustry
import net.minecraft.util.EnumChatFormatting
import java.util

class BaseDataProvider[T](cls: Class[T]) extends IWailaDataProvider {
  def getTailStrings(target: T, stack: ItemStack, acc: IWailaDataAccessor, cfg: IWailaConfigHandler): Iterable[String] = None
  def getHeadStrings(target: T, stack: ItemStack, acc: IWailaDataAccessor, cfg: IWailaConfigHandler): Iterable[String] = None
  def getBodyStrings(target: T, stack: ItemStack, acc: IWailaDataAccessor, cfg: IWailaConfigHandler): Iterable[String] = None

  println("BDP: %s < %s".format(this, cls))

  import scala.collection.JavaConversions._

  final override def getWailaTail(itemStack: ItemStack, currenttip: util.List[String], accessor: IWailaDataAccessor, config: IWailaConfigHandler) = {
    try {
      if (cls.isInstance(accessor.getTileEntity))
        currenttip.addAll(getTailStrings(accessor.getTileEntity.asInstanceOf[T], itemStack, accessor, config))
      else if (cls.isInstance(accessor.getBlock))
        currenttip.addAll(getTailStrings(accessor.getBlock.asInstanceOf[T], itemStack, accessor, config))
    } catch {
      case e: Throwable =>
        Gendustry.logWarn("Error in waila handler: %s", e.toString)
        e.printStackTrace()
        currenttip.add("[%s%s%s]".format(EnumChatFormatting.RED, e.toString, EnumChatFormatting.RESET))
    }
    currenttip
  }

  final override def getWailaHead(itemStack: ItemStack, currenttip: util.List[String], accessor: IWailaDataAccessor, config: IWailaConfigHandler) = {
    try {
      if (cls.isInstance(accessor.getTileEntity))
        currenttip.addAll(getHeadStrings(accessor.getTileEntity.asInstanceOf[T], itemStack, accessor, config))
      else if (cls.isInstance(accessor.getBlock))
        currenttip.addAll(getHeadStrings(accessor.getBlock.asInstanceOf[T], itemStack, accessor, config))
    } catch {
      case e: Throwable =>
        Gendustry.logWarn("Error in waila handler: %s", e.toString)
        e.printStackTrace()
        currenttip.add("[%s%s%s]".format(EnumChatFormatting.RED, e.toString, EnumChatFormatting.RESET))
    }
    currenttip
  }

  final override def getWailaBody(itemStack: ItemStack, currenttip: util.List[String], accessor: IWailaDataAccessor, config: IWailaConfigHandler) = {
    try {
      if (cls.isInstance(accessor.getTileEntity))
        currenttip.addAll(getBodyStrings(accessor.getTileEntity.asInstanceOf[T], itemStack, accessor, config))
      else if (cls.isInstance(accessor.getBlock))
        currenttip.addAll(getBodyStrings(accessor.getBlock.asInstanceOf[T], itemStack, accessor, config))
    } catch {
      case e: Throwable =>
        Gendustry.logWarn("Error in waila handler: %s", e.toString)
        e.printStackTrace()
        currenttip.add("[%s%s%s]".format(EnumChatFormatting.RED, e.toString, EnumChatFormatting.RESET))
    }
    currenttip
  }

  override def getWailaStack(accessor: IWailaDataAccessor, config: IWailaConfigHandler) = null
}
