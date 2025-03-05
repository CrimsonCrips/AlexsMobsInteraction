package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.server.goal.AMIHarvestCarrots;
import com.github.alexthe666.alexsmobs.entity.EntityAnteater;
import com.github.alexthe666.alexsmobs.entity.EntityBunfungus;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;


@Mixin(EntityBunfungus.class)
public abstract class AMIBunfungus extends PathfinderMob {


    protected AMIBunfungus(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        EntityBunfungus bunfungus = (EntityBunfungus)(Object)this;

        if(AlexsMobsInteraction.COMMON_CONFIG.UNSETTLING_BACKFIRE_ENABLED.get()) {
            bunfungus.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(bunfungus, LivingEntity.class, 2, true, false, livingEntity -> {
                return livingEntity.getItemBySlot(EquipmentSlot.CHEST).is(AMItemRegistry.UNSETTLING_KIMONO.get()) && !livingEntity.isAlliedTo(livingEntity);
            }));
        }
        if(AlexsMobsInteraction.COMMON_CONFIG.CARROT_HARVESTING_ENABLED.get()) {
            bunfungus.goalSelector.addGoal(2, new AMIHarvestCarrots(bunfungus));
        }
    }
}
