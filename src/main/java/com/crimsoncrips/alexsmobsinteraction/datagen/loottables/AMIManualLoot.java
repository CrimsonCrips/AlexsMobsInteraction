package com.crimsoncrips.alexsmobsinteraction.datagen.loottables;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.BiConsumer;

public class AMIManualLoot implements LootTableSubProvider {

    //Props to Drull and TF for assistance//
    @Override
    public void generate(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {

        consumer.accept(AMILootTables.UNDERZEALOT_TRADE, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(5))
                           .add(LootItem.lootTableItem(ACBlockRegistry.THORNWOOD_BRANCH.get().asItem()).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 5))).setWeight(80))
                           .add(LootItem.lootTableItem(ACBlockRegistry.THORNWOOD_LOG.get().asItem()).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))).setWeight(70))
                        .setRolls(ConstantValue.exactly(2))
                           .add(LootItem.lootTableItem(ACItemRegistry.MOTH_DUST.get()).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4))).setWeight(45))
                           .add(LootItem.lootTableItem(ACItemRegistry.VESPER_WING.get()).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))).setWeight(50))
                           .add(LootItem.lootTableItem(ACItemRegistry.CORRODENT_TEETH.get()).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1))).setWeight(40))
                        .setRolls(ConstantValue.exactly(1))
                           .add(LootItem.lootTableItem(ACItemRegistry.DESOLATE_DAGGER.get()).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1))).setWeight(10))

                ));
    }


}
