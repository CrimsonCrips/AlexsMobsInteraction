package com.crimsoncrips.alexsmobsinteraction.datagen.loottables;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.datagen.loottables.modifiers.AddEnchantmentModifier;

import com.mojang.serialization.Codec;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AMILootModifiers {

	public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, AlexsMobsInteraction.MODID);

	public static final RegistryObject<Codec<AddEnchantmentModifier>> ADD_TABLE_LOOT_MODIFIER_TYPE = LOOT_MODIFIERS.register("add_to_loot_table", () -> AddEnchantmentModifier.CODEC);

}
