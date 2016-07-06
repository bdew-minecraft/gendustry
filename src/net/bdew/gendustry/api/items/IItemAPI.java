/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.api.items;

import forestry.api.genetics.IGenome;
import net.minecraft.item.ItemStack;

import java.util.List;

public interface IItemAPI {
    /**
     * @return true if stack is a template with at least 1 sample added
     */
    boolean isFullTemplate(ItemStack stack);

    /**
     * @return true if stack is a template with no samples added
     */
    boolean isEmptyTemplate(ItemStack stack);

    /**
     * @return true if stack is a template with all required sample added (usable in replicator)
     */
    boolean isCompleteTemplate(ItemStack stack);

    /**
     * @return true if stack is a filled sample
     */
    boolean isFullSample(ItemStack stack);

    /**
     * @return true if stack is an empty sample
     */
    boolean isEmptySample(ItemStack stack);

    /**
     * @return Contents of a sample
     */
    IGeneSample getSampleInfo(ItemStack stack);

    /**
     * @return Contents of a template as a list of samples
     */
    List<IGeneSample> getTemplateSamples(ItemStack stack);

    /**
     * Contents of a template as a forestry API genome. Any unfilled chromosomes will be taken from default template.
     */
    IGenome getTemplateGenome(ItemStack stack);
}
