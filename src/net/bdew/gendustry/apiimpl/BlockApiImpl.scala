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
import net.minecraft.world.World
import net.bdew.gendustry.machines.advmutatron.TileMutatronAdv
import net.bdew.gendustry.machines.apiary.TileApiary

object BlockApiImpl extends IBlockAPI {
  override def isWorkerMachine(w: World, x: Int, y: Int, z: Int) =
    w.blockHasTileEntity(x, y, z) && w.getBlockTileEntity(x, y, z).isInstanceOf[TileWorker]

  override def getWorkerMachine(w: World, x: Int, y: Int, z: Int) =
    if (isWorkerMachine(w, x, y, z)) w.getBlockTileEntity(x, y, z).asInstanceOf[TileWorker] else null

  override def isAdvancedMutatron(w: World, x: Int, y: Int, z: Int) =
    w.blockHasTileEntity(x, y, z) && w.getBlockTileEntity(x, y, z).isInstanceOf[TileMutatronAdv]

  override def getAdvancedMutatron(w: World, x: Int, y: Int, z: Int) =
    if (isWorkerMachine(w, x, y, z)) w.getBlockTileEntity(x, y, z).asInstanceOf[TileMutatronAdv] else null

  override def isIndustrialApiary(w: World, x: Int, y: Int, z: Int) =
    w.blockHasTileEntity(x, y, z) && w.getBlockTileEntity(x, y, z).isInstanceOf[TileApiary]

  override def getIndustrialApiary(w: World, x: Int, y: Int, z: Int) =
    if (isWorkerMachine(w, x, y, z)) w.getBlockTileEntity(x, y, z).asInstanceOf[TileApiary] else null
}


