package com.crimsoncrips.alexsmobsinteraction.enchantment;


import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.github.alexthe666.alexsmobs.enchantment.StraddleEnchantment;
import com.github.alexthe666.alexsmobs.enchantment.StraddleJumpEnchantment;
import com.github.alexthe666.alexsmobs.item.ItemPigshoes;
import com.github.alexthe666.alexsmobs.item.ItemStraddleboard;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AIEnchantmentRegistry {


    public static final DeferredRegister<Enchantment> DEF_REG;
    public static final EnchantmentCategory HOGSHOES;

    public static final RegistryObject<Enchantment> STABILIZER;

    public static final RegistryObject<Enchantment> TRAMPLE;

    public AIEnchantmentRegistry() {
    }

    static {
        DEF_REG = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, "alexsmobsinteraction");
        HOGSHOES = EnchantmentCategory.create("hogshoes", (item) -> {
            return item instanceof ItemPigshoes;
        });
        STABILIZER = DEF_REG.register("stabilizer", () -> new StabilizerEnchantment(Enchantment.Rarity.RARE, EnchantmentCategory.ARMOR_HEAD,EquipmentSlot.HEAD));
        TRAMPLE = DEF_REG.register("trample", () -> {
            return new TrampleEnchantment(Enchantment.Rarity.UNCOMMON, HOGSHOES, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
        });
    }
}
