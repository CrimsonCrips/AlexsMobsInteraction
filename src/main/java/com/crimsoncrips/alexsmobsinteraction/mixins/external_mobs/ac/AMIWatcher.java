package com.crimsoncrips.alexsmobsinteraction.mixins.external_mobs.ac;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.server.enchantment.AMIEnchantmentRegistry;
import com.github.alexmodguy.alexscaves.server.entity.living.WatcherEntity;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;


@Mixin(WatcherEntity.class)
public abstract class AMIWatcher {

    @WrapOperation(method = "attemptPossession", at = @At(value = "INVOKE", target = "Lcom/github/alexmodguy/alexscaves/server/entity/living/WatcherEntity;canPossessTargetEntity(Lnet/minecraft/world/entity/Entity;)Z"),remap = false)
    private boolean alexsMobsInteraction$attemptPossession(WatcherEntity instance, Entity playerData, Operation<Boolean> original) {

        return !(playerData instanceof Player player) || AMIEnchantmentRegistry.getLevel(player.level(), player.getItemBySlot(EquipmentSlot.HEAD), AMIEnchantmentRegistry.STABILIZER) <= 0;
    }


}
