/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.power

import net.bdew.gendustry.compat.PowerProxy
import net.bdew.lib.capabilities.CapabilityProviderItem
import net.bdew.lib.power.ItemPoweredBase

trait ItemPoweredTesla extends ItemPoweredBase with CapabilityProviderItem {
  if (PowerProxy.haveTesla && PowerProxy.TeslaEnabled) {
    Tesla.injectTesla(this)
  }
}
