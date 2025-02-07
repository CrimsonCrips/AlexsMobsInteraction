package com.crimsoncrips.alexsmobsinteraction.compat;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.logging.Level;

public class ACCompat {

    public static boolean falconBomb(ItemStack itemStack){
        return itemStack.is(ACBlockRegistry.NUCLEAR_BOMB.get().asItem().getDefaultInstance().getItem());
    }

    public static boolean toxicCaves(LivingEntity livingEntity){
        return livingEntity.level().getBiome(livingEntity.blockPosition()).is(ACBiomeRegistry.TOXIC_CAVES);
    }
}
