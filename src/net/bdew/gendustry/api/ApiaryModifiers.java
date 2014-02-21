/*
 * Copyright (c) bdew, 2013 - 2014
 * https://github.com/bdew/gendustry
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * https://raw.github.com/bdew/gendustry/master/MMPL-1.0.txt
 */

package net.bdew.gendustry.api;

public class ApiaryModifiers {
    public float territory = 1;

    //max safe = 10 (degenerating - offspring become unnatural)
    public float mutation = 1;

    //increases lifespan
    public float lifespan = 1;

    //increases chance to get products each cycle, max safe = 16 (overworked - becomes unnatural)
    public float production = 1;

    public float flowering = 1;

    // set to 0 to reduce decay from fatigue
    public float geneticDecay = 1;

    public boolean isSealed = false;
    public boolean isSelfLighted = false;
    public boolean isSunlightSimulated = false;
    public boolean isHellish = false;
    public boolean isAutomated = false;
    public boolean isCollectingPollen = false;

    public float energy = 1;

    public float temperature = 0;
    public float humidity = 0;
}
