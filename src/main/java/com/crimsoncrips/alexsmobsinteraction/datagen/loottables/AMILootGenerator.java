package com.crimsoncrips.alexsmobsinteraction.datagen.loottables;

import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Map;

public class AMILootGenerator extends LootTableProvider  {
    //Props to Drull and TF for assistance//
    public AMILootGenerator(PackOutput output) {
        super(output, AMILootTables.allBuiltin(), List.of(
                new SubProviderEntry(AMIManualLoot::new, LootContextParamSets.EMPTY)
        ));
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationcontext) {

    }
}
