package com.crimsoncrips.alexsmobsinteraction.enchantment;


import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AIEnchantmentRegistry {

    public static final DeferredRegister<Enchantment> DEF_REG = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, AlexsMobsInteraction.MODID);
    public static final RegistryObject<Enchantment> STABILIZER = DEF_REG.register("stabilizer", () -> new StabilizerEnchantment(Enchantment.Rarity.RARE, EnchantmentCategory.ARMOR_HEAD,EquipmentSlot.HEAD));

}
