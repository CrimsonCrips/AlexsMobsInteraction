package com.crimsoncrips.alexsmobsinteraction.datagen.loottables;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.datagen.loottables.modifiers.AddEnchantmentModifier;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.concurrent.CompletableFuture;


public class AMIGlobalLootModifierGenerator extends GlobalLootModifierProvider {
    public AMIGlobalLootModifierGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, AlexsMobsInteraction.MODID);
    }

    @Override
    protected void start() {
        add("stabilizer", new AddEnchantmentModifier(new LootItemCondition[]{
                LootTableIdCondition.builder(ResourceLocation.fromNamespaceAndPath("alexsmobs", "entities/void_worm")).build()},
                ResourceLocation.fromNamespaceAndPath("alexsmobsinteraction",  "glm/stabilizer_addition")));

        add("light_weight", new AddEnchantmentModifier(new LootItemCondition[]{
                LootTableIdCondition.builder(ResourceLocation.fromNamespaceAndPath("minecraft", "chests/end_city_treasure")).build()},
                ResourceLocation.fromNamespaceAndPath("alexsmobsinteraction",  "glm/lightweight_addition")));

        add("rolling_thunder", new AddEnchantmentModifier(new LootItemCondition[]{
                LootTableIdCondition.builder(ResourceLocation.fromNamespaceAndPath("minecraft", "chests/abandoned_mineshaft")).build()},
                ResourceLocation.fromNamespaceAndPath("alexsmobsinteraction",  "glm/rolling_addition")));

        add("stretchy_accumuation", new AddEnchantmentModifier(new LootItemCondition[]{
                LootTableIdCondition.builder(ResourceLocation.fromNamespaceAndPath("minecraft", "chests/abandoned_mineshaft")).build()},
                ResourceLocation.fromNamespaceAndPath("alexsmobsinteraction",  "glm/stretchy_addition")));

    }
}