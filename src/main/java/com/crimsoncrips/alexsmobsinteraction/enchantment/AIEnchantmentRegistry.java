package com.crimsoncrips.alexsmobsinteraction.enchantment;


import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.github.alexthe666.alexsmobs.enchantment.StraddleEnchantment;
import com.github.alexthe666.alexsmobs.enchantment.StraddleJumpEnchantment;
import com.github.alexthe666.alexsmobs.item.ItemPigshoes;
import com.github.alexthe666.alexsmobs.item.ItemShieldOfTheDeep;
import com.github.alexthe666.alexsmobs.item.ItemStraddleboard;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AIEnchantmentRegistry {


    public static final DeferredRegister<Enchantment> DEF_REG;

    public static final EnchantmentCategory SHIELD;

    public static final RegistryObject<Enchantment> STABILIZER;

    public static final RegistryObject<Enchantment> LIGHTWEIGHT;

    public static final RegistryObject<Enchantment> FINAL_STAND;

    public static final RegistryObject<Enchantment> TRAMPLE;

    public AIEnchantmentRegistry() {
    }

    static {
        DEF_REG = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, "alexsmobsinteraction");
        SHIELD = EnchantmentCategory.create("shield", (item) -> {
            return item instanceof ItemShieldOfTheDeep || item instanceof ShieldItem;
        });
        LIGHTWEIGHT = DEF_REG.register("lightweight", () -> new LightweightEnchantment(Enchantment.Rarity.UNCOMMON, EnchantmentCategory.ARMOR_CHEST,EquipmentSlot.CHEST));

        STABILIZER = DEF_REG.register("stabilizer", () -> new StabilizerEnchantment(Enchantment.Rarity.RARE, EnchantmentCategory.ARMOR_HEAD,EquipmentSlot.HEAD));
        TRAMPLE = DEF_REG.register("trample", () -> {
            return new TrampleEnchantment(Enchantment.Rarity.UNCOMMON, EnchantmentCategory.ARMOR_FEET, new EquipmentSlot[]{EquipmentSlot.FEET});
        });
        FINAL_STAND = DEF_REG.register("final_stand", () -> new FinalStandEnchantment(Enchantment.Rarity.VERY_RARE, SHIELD,EquipmentSlot.OFFHAND));

    }
}
