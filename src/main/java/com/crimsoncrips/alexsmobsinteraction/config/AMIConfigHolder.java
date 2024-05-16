package com.crimsoncrips.alexsmobsinteraction.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public final class AMIConfigHolder {

    public static final AMInteractConfig INTERACT;

    public static final ForgeConfigSpec INTERACT_SPEC;

    static {
        {
            final Pair<AMInteractConfig, ForgeConfigSpec> interact = new ForgeConfigSpec.Builder().configure(AMInteractConfig::new);
            INTERACT = interact.getLeft();
            INTERACT_SPEC = interact.getRight();
        }
    }
}