package com.crimsoncrips.alexsmobsinteraction.misc;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.blockentity.NuclearSirenBlockEntity;
import com.github.alexmodguy.alexscaves.server.block.poi.ACPOIRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.NuclearBombEntity;
import com.github.alexthe666.alexsmobs.entity.EntityBaldEagle;
import com.google.common.base.Predicates;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class FalconBombing {

    public static List<String> bombs = List.of(new String[]{"minecraft:tnt","alexscaves:nuclear_bomb","alexscavesexemplified:gamma_nuclear_bomb"});

    public static void dropBomb(EntityBaldEagle eagle, Player player) {
        String name = eagle.getItemInHand(InteractionHand.MAIN_HAND).getItem().toString();
        Optional<EntityType<?>> test = EntityType.byString(name);

        if (test.isPresent() && eagle.level() instanceof ServerLevel serverLevel){
            EntityType<?> bomb2 = test.get();

            bomb2.spawn(serverLevel, BlockPos.containing(eagle.getX(), eagle.getY() - 0.5, eagle.getZ()), MobSpawnType.MOB_SUMMONED);
            eagle.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            AMIUtils.awardAdvancement(player,"bird_bomb","bomb");
        }
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
