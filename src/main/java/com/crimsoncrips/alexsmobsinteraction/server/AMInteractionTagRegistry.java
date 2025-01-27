package com.crimsoncrips.alexsmobsinteraction.server;//



import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;


public class AMInteractionTagRegistry {


    public static final TagKey<Block> CENTIPEDE_BLOCK_FEAR = registerBlockTag("centipede_block_fear");
    public static final TagKey<EntityType<?>> ANACONDA_BABY_KILL = registerEntityTag("anaconda_kill_baby");
    public static final TagKey<EntityType<?>> BONESERPENT_KILL = registerEntityTag("boneserpent_kill");
    public static final TagKey<EntityType<?>> BURNABLE_DEAD = registerEntityTag("burnable_dead");
    public static final TagKey<EntityType<?>> CACHALOT_WHALE_KILL_CHANCE = registerEntityTag("cachalot_whale_kill");
    public static final TagKey<EntityType<?>> CAVE_CENTIPEDE_KILL = registerEntityTag("cave_centipede_kill");
    public static final TagKey<EntityType<?>> CROCODILE_BABY_KILL = registerEntityTag("crocodile_kill_baby");
    public static final TagKey<EntityType<?>> CROW_KILL = registerEntityTag("crow_kill");
    public static final TagKey<EntityType<?>> DROPBEAR_KILL = registerEntityTag("dropbear_kill");
    public static final TagKey<EntityType<?>> EMU_KILL = registerEntityTag("emu_kill");
    public static final TagKey<EntityType<?>> PESTER_ENTITY = registerEntityTag("fly_pester");
    public static final TagKey<EntityType<?>> FRILLED_KILL = registerEntityTag("frilled_kill");
    public static final TagKey<EntityType<?>> GRIZZLY_KILL = registerEntityTag("grizzly_kill");
    public static final TagKey<EntityType<?>> GRIZZLY_TERRITORIAL = registerEntityTag("grizzly_territorial");
    public static final TagKey<EntityType<?>> HAMMERHEAD_KILL = registerEntityTag("hammerhead_kill");
    public static final TagKey<EntityType<?>> JERBOAFEAR = registerEntityTag("jerboa_fear");
    public static final TagKey<EntityType<?>> MUDSKIPPER_KILL = registerEntityTag("mudskipper_kill");
    public static final TagKey<EntityType<?>> ORCA_CHANCE_KILL = registerEntityTag("orca_chance_kill");
    public static final TagKey<EntityType<?>> ORCA_KILL = registerEntityTag("orca_kill");
    public static final TagKey<EntityType<?>> POTOO_KILL = registerEntityTag("potoo_kill");
    public static final TagKey<EntityType<?>> RACCOON_FEAR = registerEntityTag("raccoon_fear");
    public static final TagKey<EntityType<?>> RACCOON_KILL = registerEntityTag("raccoon_kill");
    public static final TagKey<EntityType<?>> RAINFROG_FEAR = registerEntityTag("rainfrog_fear");
    public static final TagKey<EntityType<?>> RATTLESNAKE_KILL = registerEntityTag("rattlesnake_kill");
    public static final TagKey<EntityType<?>> ROADRUNNER_KILL = registerEntityTag("roadrunner_kill");
    public static final TagKey<EntityType<?>> SHOEBILL_BABY_KILL = registerEntityTag("shoebill_baby_kill");
    public static final TagKey<EntityType<?>> SHOEBILL_KILL = registerEntityTag("shoebill_kill");
    public static final TagKey<EntityType<?>> INSECTS = registerEntityTag("insects");
    public static final TagKey<EntityType<?>> SMALLINSECTFEAR = registerEntityTag("smallinsectfear");
    public static final TagKey<EntityType<?>> SNAPPING_TURTLE_KILL = registerEntityTag("snapping_turtle_kill");
    public static final TagKey<EntityType<?>> TASMANIAN_KILL = registerEntityTag("tasmanian_kill");
    public static final TagKey<Item> CENTIPEDE_LIGHT_FEAR = registerItemTag("centipede_light_fear");
    public static final TagKey<Item> CROW_WEAPON = registerItemTag("crow_weapon");
    public static final TagKey<Item> EMU_TRIGGER = registerItemTag("emu_trigger");
    public static final TagKey<Item> GRIZZLY_ENTICE = registerItemTag("grizzly_entice");
    public static final TagKey<Item> LAVITHAN_PICKAXES = registerItemTag("lavithan_pickaxes");



    public AMInteractionTagRegistry() {
    }

    private static TagKey<Item> registerItemTag(String name) {
        return TagKey.create(Registries.ITEM, new ResourceLocation("alexsmobsinteraction", name));
    }
    private static TagKey<EntityType<?>> registerEntityTag(String name) {
        return TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("alexsmobsinteraction", name));
    }

    private static TagKey<Block> registerBlockTag(String name) {
        return TagKey.create(Registries.BLOCK, new ResourceLocation("alexsmobsinteraction", name));
    }


}
