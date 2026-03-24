package com.crimsoncrips.alexsmobsinteraction.datagen.loottables;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.datagen.loottables.modifiers.AddEnchantmentModifier;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.LootTableIdCondition;


public class AMIGlobalLootModifierGenerator extends GlobalLootModifierProvider {
    public AMIGlobalLootModifierGenerator(PackOutput output) {
        super(output, AlexsMobsInteraction.MODID);
    }

    @Override
    protected void start() {
        add("stabilizer", new AddEnchantmentModifier(new LootItemCondition[]{
                LootTableIdCondition.builder(new ResourceLocation("alexsmobs", "entities/void_worm")).build()},
                new ResourceLocation("alexsmobsinteraction",  "glm/stabilizer_addition")));

        add("light_weight", new AddEnchantmentModifier(new LootItemCondition[]{
                LootTableIdCondition.builder(new ResourceLocation("minecraft", "chests/end_city_treasure")).build()},
                new ResourceLocation("alexsmobsinteraction",  "glm/lightweight_addition")));

        add("rolling_thunder", new AddEnchantmentModifier(new LootItemCondition[]{
                LootTableIdCondition.builder(new ResourceLocation("minecraft", "chests/abandoned_mineshaft")).build()},
                new ResourceLocation("alexsmobsinteraction",  "glm/rolling_addition")));

        add("stretchy_accumuation", new AddEnchantmentModifier(new LootItemCondition[]{
                LootTableIdCondition.builder(new ResourceLocation("minecraft", "chests/abandoned_mineshaft")).build()},
                new ResourceLocation("alexsmobsinteraction",  "glm/stretchy_addition")));

    }
}