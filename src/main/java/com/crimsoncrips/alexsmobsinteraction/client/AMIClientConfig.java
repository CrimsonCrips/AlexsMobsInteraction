package com.crimsoncrips.alexsmobsinteraction.client;

import net.minecraftforge.common.ForgeConfigSpec;

public class AMIClientConfig {

    public final ForgeConfigSpec.BooleanValue FARSEER_EFFECTS_ENABLED;
    public final ForgeConfigSpec.BooleanValue EFFECTS_REMINDER_ENABLED;
    public final ForgeConfigSpec.IntValue NETHER_PORTAL_VARIANT;
    public final ForgeConfigSpec.IntValue END_PORTAL_VARIANT;


    public AMIClientConfig(final ForgeConfigSpec.Builder builder) {
        builder.push("visuals");
        this.FARSEER_EFFECTS_ENABLED = buildBoolean(builder, "FARSEER_EFFECTS_ENABLED", " ", true, "Whether Farseer's effects are enabled (this is for sensitive types like photosensitivity)");
        this.EFFECTS_REMINDER_ENABLED = buildBoolean(builder, "EFFECTS_REMINDER_ENABLED", " ", true, "Whether Farseer's effects reminder is enabled in login");
        builder.comment("0. Default Value, 1.Vanilla End, 2. Better End");
        this.END_PORTAL_VARIANT = buildInt(builder,"END_PORTAL_VARIANT", " ", 0, 0, 2, "Defines the texture for the end portal");

        builder.comment("0. Default Value, 1.Vanilla Nether, 2. Better Nether");
        this.NETHER_PORTAL_VARIANT = buildInt(builder,"NETHER_PORTAL_VARIANT", " ", 0, 0, 2, "Defines the texture for the nether portal");

        builder.pop();

    }

    private static ForgeConfigSpec.BooleanValue buildBoolean(ForgeConfigSpec.Builder builder, String name, String catagory, boolean defaultValue, String comment){
        return builder.comment(comment).translation(name).define(name, defaultValue);
    }

    private static ForgeConfigSpec.IntValue buildInt(ForgeConfigSpec.Builder builder, String name, String catagory, int defaultValue, int min, int max, String comment){
        return builder.comment(comment).translation(name).defineInRange(name, defaultValue, min, max);
    }
}
