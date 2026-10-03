package com.crimsoncrips.alexsmobsinteraction.client;

import net.neoforged.neoforge.common.ModConfigSpec;

public class AMIClientConfig {

    public final ModConfigSpec.BooleanValue PHOTOSENSITIVITY_ENABLED;
    public final ModConfigSpec.BooleanValue WARNING_ENABLED;
    public final ModConfigSpec.IntValue NETHER_PORTAL_VARIANT;
    public final ModConfigSpec.IntValue END_PORTAL_VARIANT;
    public final ModConfigSpec.IntValue WELCOME_TOAST_SECONDS;


    public AMIClientConfig(final ModConfigSpec.Builder builder) {
        builder.push("visuals");
        this.PHOTOSENSITIVITY_ENABLED = buildBoolean(builder, "PHOTOSENSITIVITY_ENABLED", false, "Photosensitivity mode, disables flashing visuals such as the farseer effects and turns the ascender static solid black");
        this.WELCOME_TOAST_SECONDS = buildInt(builder, "WELCOME_TOAST_SECONDS", 10, 1, Integer.MAX_VALUE, "How many seconds the welcome toast stays on screen before sliding out");
        this.WARNING_ENABLED = buildBoolean(builder, "WARNING_ENABLED",  true, "Whether Photosensitivity reminder is on on login");

        builder.comment("0. Default Value, 1.Vanilla End, 2. Better End");
        this.END_PORTAL_VARIANT = buildInt(builder,"END_PORTAL_VARIANT", 0, 0, 2, "Defines the texture for the end portal");
        builder.comment("0. Default Value, 1.Vanilla Nether, 2. Better Nether");
        this.NETHER_PORTAL_VARIANT = buildInt(builder,"NETHER_PORTAL_VARIANT", 0, 0, 2, "Defines the texture for the nether portal");

        builder.pop();

    }

    private static ModConfigSpec.BooleanValue buildBoolean(ModConfigSpec.Builder builder, String name, boolean defaultValue, String comment){
        return builder.comment(comment).translation(name).define(name, defaultValue);
    }

    private static ModConfigSpec.IntValue buildInt(ModConfigSpec.Builder builder, String name, int defaultValue, int min, int max, String comment){
        return builder.comment(comment).translation(name).defineInRange(name, defaultValue, min, max);
    }
}
