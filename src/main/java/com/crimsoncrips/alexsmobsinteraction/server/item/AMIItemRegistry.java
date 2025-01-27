package com.crimsoncrips.alexsmobsinteraction.server.item;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AMIItemRegistry {

    public static final DeferredRegister<Item> DEF_REG = DeferredRegister.create(ForgeRegistries.ITEMS, AlexsMobsInteraction.MODID);

    public static final RegistryObject<Item> BOOK_ICON = DEF_REG.register("ami_book", () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));
}