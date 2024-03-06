package com.crimsoncrips.alexsmobsinteraction.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public final class ConfigHolder {

    public static final InteractConfig INTERACT;

    public static final ForgeConfigSpec INTERACT_SPEC;

    static {
        {
            final Pair<InteractConfig, ForgeConfigSpec> interact = new ForgeConfigSpec.Builder().configure(InteractConfig::new);
            INTERACT = interact.getLeft();
            INTERACT_SPEC = interact.getRight();
        }
    }
}