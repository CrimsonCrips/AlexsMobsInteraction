package com.crimsoncrips.alexsmobsinteraction.mixins.external_mobs.ac;

import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.crimsoncrips.alexsmobsinteraction.networking.StabilizedPacket;
import com.crimsoncrips.alexsmobsinteraction.server.enchantment.AMIEnchantmentRegistry;
import com.github.alexmodguy.alexscaves.server.entity.living.WatcherEntity;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;


@Mixin(WatcherEntity.class)
public abstract class AMIWatcher {

    @Unique
    private static final String LAST_RESISTED = "AMIStabilizerResisted";
    @Unique
    private static final long RESIST_FLASH_INTERVAL = AMIUtils.seconds(2);

    @WrapOperation(method = "attemptPossession", at = @At(value = "INVOKE", target = "Lcom/github/alexmodguy/alexscaves/server/entity/living/WatcherEntity;canPossessTargetEntity(Lnet/minecraft/world/entity/Entity;)Z"),remap = false)
    private boolean alexsMobsInteraction$attemptPossession(WatcherEntity instance, Entity entity, Operation<Boolean> original) {
        boolean possessable = original.call(instance, entity);
        if (possessable && entity instanceof Player player && AMIEnchantmentRegistry.getLevel(player.level(), player.getItemBySlot(EquipmentSlot.HEAD), AMIEnchantmentRegistry.STABILIZER) > 0) {
            if (player instanceof ServerPlayer serverPlayer) {
                CompoundTag data = serverPlayer.getPersistentData();
                long gameTime = serverPlayer.level().getGameTime();
                if (gameTime - data.getLong(LAST_RESISTED) >= RESIST_FLASH_INTERVAL) {
                    data.putLong(LAST_RESISTED, gameTime);
                    PacketDistributor.sendToPlayer(serverPlayer, new StabilizedPacket());
                }
            }
            return false;
        }
        return possessable;
    }


}
