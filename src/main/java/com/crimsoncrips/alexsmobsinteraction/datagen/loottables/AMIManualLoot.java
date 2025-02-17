package com.crimsoncrips.alexsmobsinteraction.datagen.loottables;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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

        consumer.accept(AMILootTables.FLUTTER_SHEAR, LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Blocks.MOSS_BLOCK.asItem()).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 2))).setWeight(80))
                        .add(LootItem.lootTableItem(Blocks.AZALEA.asItem()).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1))).setWeight(60))
                        .add(LootItem.lootTableItem(Blocks.SPORE_BLOSSOM.asItem()).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1))).setWeight(25))
                ));

        consumer.accept(AMILootTables.BANANA_SHEAR, LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(AMItemRegistry.BANANA.get()).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))).setWeight(5))
                        .add(LootItem.lootTableItem(AMItemRegistry.BANANA_SLUG_SLIME.get()).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1))))
                ));

        consumer.accept(AMILootTables.GRIZZLY_BRUSH, LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(AMItemRegistry.BEAR_FUR.get()).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))).setWeight(100))
                        .add(LootItem.lootTableItem(AMItemRegistry.BEAR_DUST.get()).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 1))))
                ));
    }


}
