package com.crimsoncrips.alexsmobsinteraction.compat;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.NuclearBombEntity;
import com.github.alexmodguy.alexscaves.server.entity.item.NuclearExplosionEntity;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.mojang.datafixers.types.templates.Sum;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.logging.Level;

public class ACCompat {


    public static ItemStack gameController(){
        return ACItemRegistry.GAME_CONTROLLER.get().getDefaultInstance();
    }

    public static boolean toxicCaves(LivingEntity livingEntity){
        return livingEntity.level().getBiome(livingEntity.blockPosition()).is(ACBiomeRegistry.TOXIC_CAVES);
    }


    public static EntityType gammaroach(){
        return ACEntityRegistry.GAMMAROACH.get();
    }
}
