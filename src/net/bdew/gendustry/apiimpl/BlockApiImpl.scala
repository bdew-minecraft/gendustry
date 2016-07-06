/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.apiimpl

import net.bdew.gendustry.api.blocks.IBlockAPI
import net.bdew.gendustry.machines.advmutatron.TileMutatronAdv
import net.bdew.gendustry.machines.apiary.TileApiary
import net.bdew.gendustry.machines.mutatron.TileMutatron
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

object BlockApiImpl extends IBlockAPI {
  private def getTypedTileEntity[T](w: World, pos: BlockPos, cls: Class[T]) =
    Option(w.getTileEntity(pos)) filter cls.isInstance map (_.asInstanceOf[T])

  override def isWorkerMachine(w: World, pos: BlockPos) =
    getTypedTileEntity(w, pos, classOf[TileWorker]).isDefined

  override def getWorkerMachine(w: World, pos: BlockPos) =
    getTypedTileEntity(w, pos, classOf[TileWorker]).orNull

  override def isMutatron(w: World, pos: BlockPos) =
    getTypedTileEntity(w, pos, classOf[TileMutatron]).isDefined

  override def getMutatron(w: World, pos: BlockPos) =
    getTypedTileEntity(w, pos, classOf[TileMutatron]).orNull

  override def isAdvancedMutatron(w: World, pos: BlockPos) =
    getTypedTileEntity(w, pos, classOf[TileMutatronAdv]).isDefined

  override def getAdvancedMutatron(w: World, pos: BlockPos) =
    getTypedTileEntity(w, pos, classOf[TileMutatronAdv]).orNull

  override def isIndustrialApiary(w: World, pos: BlockPos) =
    getTypedTileEntity(w, pos, classOf[TileApiary]).isDefined

  override def getIndustrialApiary(w: World, pos: BlockPos) =
    getTypedTileEntity(w, pos, classOf[TileApiary]).orNull
}


