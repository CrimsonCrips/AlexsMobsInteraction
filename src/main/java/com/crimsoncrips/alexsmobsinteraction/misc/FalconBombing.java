package com.crimsoncrips.alexsmobsinteraction.misc;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.blockentity.NuclearSirenBlockEntity;
import com.github.alexmodguy.alexscaves.server.block.poi.ACPOIRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.NuclearBombEntity;
import com.google.common.base.Predicates;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.stream.Stream;

public class FalconBombing {

    public static List<String> bombs = List.of(new String[]{"tnt","nuclear_bomb","gamma_nuclear_bomb"});


    public static String falconBomb(ItemStack itemStack){

        return switch (itemStack.getItem().toString()) {
            case "tnt" -> "minecraft:tnt";
            case "nuclear_bomb" -> "alexscaves:nuclear_bomb";
            case "gamma_nuclear_bomb" -> "alexscavesexemplified:gamma_nuclear_bomb";
            default -> null;
        };
    }
    //From AC NuclearBombEntity

    public static void activateSiren(BlockPos pos, LivingEntity living) {
        if(living.level().getBlockEntity(pos) instanceof NuclearSirenBlockEntity nuclearSirenBlock){
            nuclearSirenBlock.setNearestNuclearBomb(living);
        }
    }

    public static Stream<BlockPos> getNearbySirens(ServerLevel world, int range, LivingEntity living) {
        PoiManager pointofinterestmanager = world.getPoiManager();
        return pointofinterestmanager.findAll(poiTypeHolder -> poiTypeHolder.is(ACPOIRegistry.NUCLEAR_SIREN.getKey()), Predicates.alwaysTrue(), living.blockPosition(), range, PoiManager.Occupancy.ANY);
    }
}
