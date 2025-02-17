package com.crimsoncrips.alexsmobsinteraction.misc;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class ManualLootUtil {

    public static void spawnLoot (ResourceLocation location, LivingEntity entity, Player player,int loop){
        LootParams ctx = new LootParams.Builder((ServerLevel) entity.level()).withParameter(LootContextParams.THIS_ENTITY, entity).create(LootContextParamSets.EMPTY);
        ObjectArrayList<ItemStack> rewards = entity.level().getServer().getLootData().getLootTable(location).getRandomItems(ctx);
        if (!rewards.isEmpty()){
            for(int i = 0; i <= loop;i++){
                rewards.forEach(stack -> BehaviorUtils.throwItem(entity, rewards.get(0), player.position().add(0.0D, 1.0D, 0.0D)));
            }
        }
    }
}
