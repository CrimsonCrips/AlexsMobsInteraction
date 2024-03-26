package com.crimsoncrips.alexsmobsinteraction.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class FinalStandEnchantment extends Enchantment {


    public FinalStandEnchantment(Rarity p_44676_, EnchantmentCategory p_44677_, EquipmentSlot... p_44678_) {
        super(p_44676_, p_44677_, p_44678_);
    }

    public int getMinCost(int i) {
        return 7 + (i + 1) * 3;
    }

    public int getMaxCost(int i) {
        return super.getMinCost(i) + 13;
    }


    public boolean isTradeable() {
        return super.isTradeable();
    }

    public boolean isDiscoverable() {
        return super.isDiscoverable();
    }

    public boolean isAllowedOnBooks() {
        return super.isAllowedOnBooks();
    }

}
