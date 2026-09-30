package com.crimsoncrips.alexsmobsinteraction.server;

import net.neoforged.neoforge.common.ModConfigSpec;

public class AMIAddTargetsConfig {

    public final ModConfigSpec.BooleanValue CANNIBALISM_ENABLED;

    public final ModConfigSpec.BooleanValue SPIDER_ENABLED;
    public final ModConfigSpec.BooleanValue ANACONDA_ENABLED;
    public final ModConfigSpec.BooleanValue BONE_SERPENT_ENABLED;
    public final ModConfigSpec.BooleanValue CACHALOT_ENABLED;
    public final ModConfigSpec.BooleanValue CAVE_CENTIPEDE_ENABLED;
    public final ModConfigSpec.BooleanValue CROW_ENABLED;
    public final ModConfigSpec.BooleanValue DROPBEAR_ENABLED;
    public final ModConfigSpec.BooleanValue EMU_ENABLED;
    public final ModConfigSpec.BooleanValue FARSEER_ENABLED;
    public final ModConfigSpec.BooleanValue FRILLED_SHARK_ENABLED;
    public final ModConfigSpec.BooleanValue GELADA_MONKEY_ENABLED;
    public final ModConfigSpec.BooleanValue GORILLA_ENABLED;
    public final ModConfigSpec.BooleanValue GRIZZLY_BEAR_ENABLED;
    public final ModConfigSpec.BooleanValue HAMMERHEAD_ENABLED;
    public final ModConfigSpec.BooleanValue MUDSKIPPER_ENABLED;
    public final ModConfigSpec.BooleanValue ORCA_ENABLED;
    public final ModConfigSpec.BooleanValue POTOO_ENABLED;
    public final ModConfigSpec.BooleanValue RACCOON_ENABLED;
    public final ModConfigSpec.BooleanValue RATTLESNAKE_ENABLED;
    public final ModConfigSpec.BooleanValue ROADRUNNER_ENABLED;
    public final ModConfigSpec.BooleanValue SHOEBILL_ENABLED;
    public final ModConfigSpec.BooleanValue STRADDLER_ENABLED;
    public final ModConfigSpec.BooleanValue TASMANIAN_DEVIL_ENABLED;
    public final ModConfigSpec.BooleanValue CAPUCHIN_ENABLED;

    public AMIAddTargetsConfig(final ModConfigSpec.Builder builder) {
        builder.comment();
        this.CANNIBALISM_ENABLED = buildBoolean(builder, "CANNIBALISM_ENABLED",  true, " ");

        builder.push("Vanilla Mobs");
        this.SPIDER_ENABLED = buildMob(builder, "SPIDER_ENABLED");
        builder.pop();

        builder.push("Alexs Mobs");
        this.ANACONDA_ENABLED = buildMob(builder, "ANACONDA_ENABLED");
        this.BONE_SERPENT_ENABLED = buildMob(builder, "BONE_SERPENT_ENABLED");
        this.CACHALOT_ENABLED = buildMob(builder, "CACHALOT_ENABLED");
        this.CAVE_CENTIPEDE_ENABLED = buildMob(builder, "CAVE_CENTIPEDE_ENABLED");
        this.CROW_ENABLED = buildMob(builder, "CROW_ENABLED");
        this.DROPBEAR_ENABLED = buildMob(builder, "DROPBEAR_ENABLED");
        this.EMU_ENABLED = buildMob(builder, "EMU_ENABLED");
        this.FARSEER_ENABLED = buildMob(builder, "FARSEER_ENABLED");
        this.FRILLED_SHARK_ENABLED = buildMob(builder, "FRILLED_SHARK_ENABLED");
        this.GELADA_MONKEY_ENABLED = buildMob(builder, "GELADA_MONKEY_ENABLED");
        this.GORILLA_ENABLED = buildMob(builder, "GORILLA_ENABLED");
        this.GRIZZLY_BEAR_ENABLED = buildMob(builder, "GRIZZLY_BEAR_ENABLED");
        this.HAMMERHEAD_ENABLED = buildMob(builder, "HAMMERHEAD_ENABLED");
        this.MUDSKIPPER_ENABLED = buildMob(builder, "MUDSKIPPER_ENABLED");
        this.ORCA_ENABLED = buildMob(builder, "ORCA_ENABLED");
        this.POTOO_ENABLED = buildMob(builder, "POTOO_ENABLED");
        this.RACCOON_ENABLED = buildMob(builder, "RACCOON_ENABLED");
        this.RATTLESNAKE_ENABLED = buildMob(builder, "RATTLESNAKE_ENABLED");
        this.ROADRUNNER_ENABLED = buildMob(builder, "ROADRUNNER_ENABLED");
        this.SHOEBILL_ENABLED = buildMob(builder, "SHOEBILL_ENABLED");
        this.STRADDLER_ENABLED = buildMob(builder, "STRADDLER_ENABLED");
        this.TASMANIAN_DEVIL_ENABLED = buildMob(builder, "TASMANIAN_DEVIL_ENABLED");
        this.CAPUCHIN_ENABLED = buildMob(builder, "CAPUCHIN_ENABLED");
        builder.pop();

    }

    private static ModConfigSpec.BooleanValue buildBoolean(ModConfigSpec.Builder builder, String name, boolean defaultValue,String comment){
        return builder.translation(name).define(name, defaultValue);
    }

    private static ModConfigSpec.BooleanValue buildMob(ModConfigSpec.Builder builder, String name){
        return builder.translation(name).define(name, true);
    }

}
