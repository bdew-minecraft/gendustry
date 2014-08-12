/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.apiimpl

import net.bdew.gendustry.api.blocks.IBlockAPI
import net.bdew.gendustry.machines.advmutatron.TileMutatronAdv
import net.bdew.gendustry.machines.apiary.TileApiary
import net.minecraft.world.World

object BlockApiImpl extends IBlockAPI {
  private def getTypedTileEntity[T](w: World, x: Int, y: Int, z: Int, cls: Class[T]) =
    Option(w.getTileEntity(x, y, z)) filter cls.isInstance map (_.asInstanceOf[T])

  override def isWorkerMachine(w: World, x: Int, y: Int, z: Int) =
    getTypedTileEntity(w, x, y, z, classOf[TileWorker]).isDefined

  override def getWorkerMachine(w: World, x: Int, y: Int, z: Int) =
    getTypedTileEntity(w, x, y, z, classOf[TileWorker]) getOrElse null

  override def isAdvancedMutatron(w: World, x: Int, y: Int, z: Int) =
    getTypedTileEntity(w, x, y, z, classOf[TileMutatronAdv]).isDefined

  override def getAdvancedMutatron(w: World, x: Int, y: Int, z: Int) =
    getTypedTileEntity(w, x, y, z, classOf[TileMutatronAdv]) getOrElse null

  override def isIndustrialApiary(w: World, x: Int, y: Int, z: Int) =
    getTypedTileEntity(w, x, y, z, classOf[TileApiary]).isDefined

  override def getIndustrialApiary(w: World, x: Int, y: Int, z: Int) =
    getTypedTileEntity(w, x, y, z, classOf[TileApiary]) getOrElse null
}


