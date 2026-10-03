package com.crimsoncrips.alexsmobsinteraction.server.item;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class AMIItemRegistry {

    public static final DeferredRegister<Item> DEF_REG = DeferredRegister.create(BuiltInRegistries.ITEM, AlexsMobsInteraction.MODID);

    public static final DeferredHolder<Item, Item> EGGS = DEF_REG.register("eggs", () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));
    public static final DeferredHolder<Item, Item> MUTATE_ITEMS = DEF_REG.register("mutate_items", () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));

    public static final DeferredHolder<Item, Item> ASCENDER = DEF_REG.register("ascender", () -> new ItemAscender(new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<Item, Item> ASMON_CROWN = DEF_REG.register("asmon_crown", () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));

}