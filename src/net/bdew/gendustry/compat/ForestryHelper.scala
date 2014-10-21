/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.compat

import forestry.api.genetics.AlleleManager
import net.bdew.gendustry.Gendustry

object ForestryHelper {
  def haveRoot(root: String) = AlleleManager.alleleRegistry.getSpeciesRoot("root" + root) != null
  def getRoot(root: String) = AlleleManager.alleleRegistry.getSpeciesRoot("root" + root)

  def logAvailableRoots() {
    import scala.collection.JavaConversions._
    Gendustry.logInfo("Available Forestry species roots:")
    for ((name, root) <- AlleleManager.alleleRegistry.getSpeciesRoot) {
      Gendustry.logInfo(" * %s - %s", name, root.getClass.getCanonicalName)
    }
  }
}
