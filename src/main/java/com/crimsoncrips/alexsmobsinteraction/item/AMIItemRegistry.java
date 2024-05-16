package com.crimsoncrips.alexsmobsinteraction.item;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AMIItemRegistry {

    public static final DeferredRegister<Item> DEF_REG = DeferredRegister.create(ForgeRegistries.ITEMS, AlexsMobsInteraction.MODID);

    public static final RegistryObject<Item> SWATTER = DEF_REG.register("swatter", () -> new Swatter(new Item.Properties().durability(5).rarity(Rarity.UNCOMMON)));

}
