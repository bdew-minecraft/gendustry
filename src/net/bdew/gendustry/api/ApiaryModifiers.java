/*
 * Copyright (c) bdew, 2013 - 2016
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.gendustry.api;

import net.minecraft.world.biome.Biome;

/**
 * Stores current modifiers from all upgrades in an apiary
 */
public class ApiaryModifiers {
    /**
     * Territory modifier, multiplicative
     */
    public float territory = 1;

    /**
     * Mutation chance modifier, multiplicative
     * max safe = 10 (degenerating - offspring become unnatural)
     */
    public float mutation = 1;

    /**
     * Lifespan modifier, multiplicative, higher = longer
     */
    public float lifespan = 1;

    /**
     * Production modifier - increases chance to get products each tick, multiplicative
     * max safe = 10 (overworked - becomes unnatural)
     */
    public float production = 1;

    /**
     * Flowering and pollination chance modifier, multiplicative
     */
    public float flowering = 1;

    /**
     * Genetic decay chance, applies to fatigued unnatural bees, multiplicative
     */
    public float geneticDecay = 1;

    /**
     * Sealed - bees can work in rain without the required traits
     */
    public boolean isSealed = false;

    /**
     * Self lighted - bees can work in the night without the required traits, makes block emit light
     */
    public boolean isSelfLighted = false;

    /**
     * Sunlight simulated - bees can work in caves without the required traits
     */
    public boolean isSunlightSimulated = false;

    /**
     * Automated - will auto move offspring to the right slots to allow further breeding
     */
    public boolean isAutomated = false;

    /**
     * Allows collection of pollen from trees
     */
    public boolean isCollectingPollen = false;

    /**
     * If set - overrides biome as seen by jubilance checks, etc.
     */
    public Biome biomeOverride = null;

    /**
     * Energy use modifier, multiplicative
     */
    public float energy = 1;

    /**
     * Temperature modifier, additive, same units as {@link Biome#getTemperature()}
     */
    public float temperature = 0;

    /**
     * Humidity modifier, additive, same units as {@link Biome#getRainfall()}
     */
    public float humidity = 0;
}
