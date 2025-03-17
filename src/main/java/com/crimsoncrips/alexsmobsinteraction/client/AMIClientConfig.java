package com.crimsoncrips.alexsmobsinteraction.client;

import net.minecraftforge.common.ForgeConfigSpec;

public class AMIClientConfig {

    public final ForgeConfigSpec.BooleanValue FARSEER_EFFECTS_ENABLED;
    public final ForgeConfigSpec.BooleanValue EFFECTS_REMINDER_ENABLED;


    public AMIClientConfig(final ForgeConfigSpec.Builder builder) {
        builder.push("visuals");
        this.FARSEER_EFFECTS_ENABLED = buildBoolean(builder, "FARSEER_EFFECTS_ENABLED", " ", true, "Whether Farseer's effects are enabled (this is for sensitive types like photosensitivity)");
        this.EFFECTS_REMINDER_ENABLED = buildBoolean(builder, "EFFECTS_REMINDER_ENABLED", " ", true, "Whether Farseer's effects reminder is enabled in login");

        builder.pop();

    }

    private static ForgeConfigSpec.BooleanValue buildBoolean(ForgeConfigSpec.Builder builder, String name, String catagory, boolean defaultValue, String comment){
        return builder.comment(comment).translation(name).define(name, defaultValue);
    }
}
