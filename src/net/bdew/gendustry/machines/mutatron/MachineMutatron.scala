/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.machines.mutatron

import java.util.Locale

import net.bdew.gendustry.Gendustry
import net.bdew.gendustry.api.EnumMutationSetting
import net.bdew.gendustry.apiimpl.MutatronOverridesImpl
import net.bdew.gendustry.config.Tuning
import net.bdew.lib.gui.GuiProvider
import net.bdew.lib.machine.{Machine, ProcessorMachine}
import net.bdew.lib.recipes.gencfg.EntryStr
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

object MachineMutatron extends Machine("Mutatron", BlockMutatron) with GuiProvider with ProcessorMachine {
  def guiId = 2
  type TEClass = TileMutatron

  lazy val tankSize = tuning.getInt("TankSize")
  lazy val mutagenPerItem = tuning.getInt("MutagenPerItem")
  lazy val labwareConsumeChance = tuning.getFloat("LabwareConsumeChance")
  lazy val degradeChanceNatural = tuning.getFloat("DegradeChanceNatural")
  lazy val deathChanceArtificial = tuning.getFloat("DeathChanceArtificial")
  lazy val secretChance = tuning.getFloat("SecretMutationChance")

  lazy val mutatronOverrides =
    (
      MutatronOverridesImpl.overrides ++
        (for ((key, value) <- Tuning.getSection("Genetics").getSection("MutatronOverrides").filterType(classOf[EntryStr]))
          yield value.v.toUpperCase(Locale.US) match {
            case "ENABLED" => Some(key -> EnumMutationSetting.ENABLED)
            case "DISABLED" => Some(key -> EnumMutationSetting.DISABLED)
            case "REQUIREMENTS" => Some(key -> EnumMutationSetting.REQUIREMENTS)
            case _ =>
              Gendustry.logWarn("Ignoring mutatron override for species %s - invalid value (%s)", key, value.v)
              None
          }).flatten.toMap
      ).withDefaultValue(EnumMutationSetting.ENABLED)

  @SideOnly(Side.CLIENT)
  def getGui(te: TileMutatron, player: EntityPlayer) = new GuiMutatron(te, player)
  def getContainer(te: TileMutatron, player: EntityPlayer) = new ContainerMutatron(te, player)
}
