package com.crimsoncrips.alexsmobsinteraction.client;

import net.minecraftforge.common.ForgeConfigSpec;

public class ACClientConfig {

    public final ForgeConfigSpec.BooleanValue FARSEER_EFFECTS_ENABLED;


    public ACClientConfig(final ForgeConfigSpec.Builder builder) {
        builder.push("visuals");
        this.FARSEER_EFFECTS_ENABLED = buildBoolean(builder, "FARSEER_EFFECTS_ENABLED", " ", true, "Whether Farseer's effects are enabled (this is for sensitive types like photosensitivity)");

        builder.pop();

    }

    private static ForgeConfigSpec.BooleanValue buildBoolean(ForgeConfigSpec.Builder builder, String name, String catagory, boolean defaultValue, String comment){
        return builder.comment(comment).translation(name).define(name, defaultValue);
    }
}
