package com.crimsoncrips.alexsmobsinteraction.enchantment;


import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.item.ItemShieldOfTheDeep;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AMIEnchantmentRegistry {


    public static final DeferredRegister<Enchantment> DEF_REG;

    public static final EnchantmentCategory SHIELD;

    public static final EnchantmentCategory ROLLER;

    public static final RegistryObject<Enchantment> STABILIZER;

    //placeholder for future implementation//
    //public static final RegistryObject<Enchantment> MIMICLESS;//

    public static final RegistryObject<Enchantment> LIGHTWEIGHT;

    public static final RegistryObject<Enchantment> FINAL_STAND;

    public static final RegistryObject<Enchantment> TRAMPLE;

    public static final RegistryObject<Enchantment> ROLLING_THUNDER;

    public AMIEnchantmentRegistry() {
    }

    static ItemStack itemStack;

    static {
        DEF_REG = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, "alexsmobsinteraction");
        SHIELD = EnchantmentCategory.create("shield", (item) -> {
            return item instanceof ItemShieldOfTheDeep || item instanceof ShieldItem;
        });
        ROLLER  = EnchantmentCategory.create("teleport_staff", item -> item == AMItemRegistry.ROCKY_CHESTPLATE.get());

        LIGHTWEIGHT = DEF_REG.register("lightweight", () -> new AMIBasicEnchantment(Enchantment.Rarity.UNCOMMON, EnchantmentCategory.ARMOR_CHEST,EquipmentSlot.CHEST));
        ROLLING_THUNDER = DEF_REG.register("rolling_thunder", () -> new AMIBasicEnchantment(Enchantment.Rarity.UNCOMMON, ROLLER,EquipmentSlot.CHEST));
        //MIMICLESS = DEF_REG.register("mimicless", () -> new AMIBasicEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentCategory.ARMOR_HEAD,EquipmentSlot.HEAD));//

        STABILIZER = DEF_REG.register("stabilizer", () -> new AMIBasicEnchantment(Enchantment.Rarity.RARE, EnchantmentCategory.ARMOR_HEAD,EquipmentSlot.HEAD));
        TRAMPLE = DEF_REG.register("trample", () -> {
            return new AMIBasicEnchantment(Enchantment.Rarity.UNCOMMON, EnchantmentCategory.ARMOR_FEET, new EquipmentSlot[]{EquipmentSlot.FEET});
        });
        FINAL_STAND = DEF_REG.register("final_stand", () -> new AMIBasicEnchantment(Enchantment.Rarity.VERY_RARE, SHIELD,EquipmentSlot.OFFHAND));

    }
}
