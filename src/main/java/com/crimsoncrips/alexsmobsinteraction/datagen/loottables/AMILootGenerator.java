package com.crimsoncrips.alexsmobsinteraction.datagen.loottables;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AMILootGenerator extends LootTableProvider {
    //Props to Drull and TF for assistance//
    public AMILootGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, AMILootTables.allBuiltin(), List.of(
                new SubProviderEntry(AMILoot::new, LootContextParamSets.EMPTY)
        ), registries);
    }

    @Override
    protected void validate(WritableRegistry<LootTable> writableregistry, ValidationContext validationcontext, ProblemReporter.Collector problemreporter$collector) {
    }
}
