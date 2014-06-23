/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.api.items;

import forestry.api.genetics.IAllele;
import forestry.api.genetics.ISpeciesRoot;

/**
 * Represents a single genetic sample
 * Returned from IItemApi.getSampleInfo and getTemplateSamples
 */
public interface IGeneSample {

    /**
     * @return Species root object
     */
    public ISpeciesRoot root();

    /**
     * Chromosome number, see {@link forestry.api.apiculture.EnumBeeChromosome},
     * {@link forestry.api.arboriculture.EnumTreeChromosome} and {@link forestry.api.lepidopterology.EnumButterflyChromosome}
     */
    public int chromosome();

    /**
     * @return Sampled allele
     */
    public IAllele allele();

    /**
     * Human-readable, localized string
     */
    public String getLocalizedName();
}
