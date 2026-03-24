package com.crimsoncrips.alexsmobsinteraction.datagen.loottables.modifiers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

public class AddEnchantmentModifier extends LootModifier {
	public static final Codec<AddEnchantmentModifier> CODEC = RecordCodecBuilder.create((instance) -> instance.group(IGlobalLootModifier.LOOT_CONDITIONS_CODEC.fieldOf("conditions").forGetter((glm) -> glm.conditions), ResourceLocation.CODEC.fieldOf("table").forGetter(AddEnchantmentModifier::table)).apply(instance, AddEnchantmentModifier::new));
	private final ResourceLocation table;

	public AddEnchantmentModifier(LootItemCondition[] conditionsIn, ResourceLocation table) {
		super(conditionsIn);
		this.table = table;
	}

	public ResourceLocation table() {
		return this.table;
	}

	protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
		LootTable lootTable = context.getResolver().getLootTable(this.table);
		if (context.getRandom().nextDouble() < 0.15){
			lootTable.getRandomItemsRaw(context, LootTable.createStackSplitter(context.getLevel(), generatedLoot::add));
		}
		return generatedLoot;
	}

	public Codec<? extends IGlobalLootModifier> codec() {
		return CODEC;
	}
}