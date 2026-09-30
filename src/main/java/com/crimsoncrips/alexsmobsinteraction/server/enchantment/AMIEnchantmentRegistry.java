package com.crimsoncrips.alexsmobsinteraction.server.enchantment;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.datagen.tags.AMIItemTagGenerator;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

public class AMIEnchantmentRegistry {

    public static final ResourceKey<Enchantment> STABILIZER = create("stabilizer");
    public static final ResourceKey<Enchantment> LIGHTWEIGHT = create("lightweight");
    public static final ResourceKey<Enchantment> ROLLING_THUNDER = create("rolling_thunder");
    public static final ResourceKey<Enchantment> STRETCHY_ACCUMULATION = create("stretchy_accumulation");

    private static ResourceKey<Enchantment> create(String name) {
        return ResourceKey.create(Registries.ENCHANTMENT, AlexsMobsInteraction.prefix(name));
    }

    public static int getLevel(Level level, ItemStack stack, ResourceKey<Enchantment> enchantment) {
        if (stack.isEmpty()) return 0;
        return level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolder(enchantment)
                .map(holder -> EnchantmentHelper.getItemEnchantmentLevel(holder, stack))
                .orElse(0);
    }

    public static boolean has(Level level, ItemStack stack, ResourceKey<Enchantment> enchantment) {
        return getLevel(level, stack, enchantment) > 0;
    }

    public static void bootstrap(BootstrapContext<Enchantment> context) {
        HolderGetter<Item> items = context.lookup(Registries.ITEM);

        register(context, LIGHTWEIGHT, Enchantment.enchantment(Enchantment.definition(
                items.getOrThrow(ItemTags.CHEST_ARMOR_ENCHANTABLE), 5, 1,
                Enchantment.dynamicCost(13, 3), Enchantment.dynamicCost(24, 10), 2, EquipmentSlotGroup.CHEST)));

        register(context, ROLLING_THUNDER, Enchantment.enchantment(Enchantment.definition(
                items.getOrThrow(AMIItemTagGenerator.ROLLING_THUNDER_ENCHANTABLE), 1, 1,
                Enchantment.dynamicCost(13, 3), Enchantment.dynamicCost(24, 10), 8, EquipmentSlotGroup.CHEST)));

        register(context, STABILIZER, Enchantment.enchantment(Enchantment.definition(
                items.getOrThrow(ItemTags.HEAD_ARMOR_ENCHANTABLE), 2, 1,
                Enchantment.dynamicCost(13, 3), Enchantment.dynamicCost(24, 10), 4, EquipmentSlotGroup.HEAD)));

        register(context, STRETCHY_ACCUMULATION, Enchantment.enchantment(Enchantment.definition(
                items.getOrThrow(AMIItemTagGenerator.STRETCHY_ACCUMULATION_ENCHANTABLE), 1, 1,
                Enchantment.dynamicCost(13, 3), Enchantment.dynamicCost(24, 10), 8, EquipmentSlotGroup.MAINHAND)));
    }

    private static void register(BootstrapContext<Enchantment> context, ResourceKey<Enchantment> key, Enchantment.Builder builder) {
        context.register(key, builder.build(key.location()));
    }
}
