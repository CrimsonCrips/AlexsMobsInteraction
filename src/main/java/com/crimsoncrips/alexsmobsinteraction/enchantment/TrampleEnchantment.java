package com.crimsoncrips.alexsmobsinteraction.enchantment;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FrostedIceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.Iterator;

public class TrampleEnchantment extends Enchantment {


    public TrampleEnchantment(Rarity p_44676_, EnchantmentCategory p_44677_, EquipmentSlot... p_44678_) {
        super(p_44676_, p_44677_, p_44678_);
    }


    public int getMinCost(int i) {
        return 7 + (i + 2) * 8;
    }

    public int getMaxCost(int i) {
        return super.getMinCost(i) + 12;
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
