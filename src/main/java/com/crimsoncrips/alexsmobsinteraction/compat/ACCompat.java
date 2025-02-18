package com.crimsoncrips.alexsmobsinteraction.compat;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.NuclearExplosionEntity;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.mojang.datafixers.types.templates.Sum;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.logging.Level;

public class ACCompat {

    public static boolean falconBomb(ItemStack itemStack){
        return itemStack.is(ACBlockRegistry.NUCLEAR_BOMB.get().asItem().getDefaultInstance().getItem());
    }

    public static ItemStack gameController(){
        return ACItemRegistry.GAME_CONTROLLER.get().getDefaultInstance();
    }

    public static boolean toxicCaves(LivingEntity livingEntity){
        return livingEntity.level().getBiome(livingEntity.blockPosition()).is(ACBiomeRegistry.TOXIC_CAVES);
    }

    public static void summonNuke(LivingEntity living){
        NuclearExplosionEntity explosion = ACEntityRegistry.NUCLEAR_EXPLOSION.get().create(living.level());
        explosion.copyPosition(living);
        explosion.setSize(1.75F);
        living.level().addFreshEntity(explosion);
    }
}
