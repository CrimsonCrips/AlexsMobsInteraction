package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.server.effect.AMIEffects;
import com.crimsoncrips.alexsmobsinteraction.server.goal.AMIFungusBonemeal;
import com.github.alexthe666.alexsmobs.entity.EntityAnteater;
import com.github.alexthe666.alexsmobs.entity.EntityCosmaw;
import com.github.alexthe666.alexsmobs.entity.EntityCrimsonMosquito;
import com.github.alexthe666.alexsmobs.entity.EntityTriops;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.List;
import java.util.Objects;


@Mixin(EntityCrimsonMosquito.class)
public abstract class AMICrimsonMosquitoMixin extends Monster {


    protected AMICrimsonMosquitoMixin(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"),remap = false)
    private void alexsMobsInteraction$registerGoals(CallbackInfo ci) {
        EntityCrimsonMosquito crimsonMosquito = (EntityCrimsonMosquito)(Object)this;

        if (AlexsMobsInteraction.COMMON_CONFIG.FUNGUS_POLLINATE_ENABLED.get()) {
            this.goalSelector.addGoal(2, new AMIFungusBonemeal(crimsonMosquito, 1.1, 10));
        }
    }

    @Inject(method = "rideTick", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/alexsmobs/entity/EntityCrimsonMosquito;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V"),remap = false)
    private void alexsMobsInteraction$rideTick(CallbackInfo ci) {
        if (AlexsMobsInteraction.COMMON_CONFIG.FUNGUS_POLLINATE_ENABLED.get()) {
            EntityCrimsonMosquito crimsonMosquito = (EntityCrimsonMosquito)(Object)this;

            List<MobEffectInstance> mobEffects = crimsonMosquito.getActiveEffects().stream().toList();
            for (MobEffectInstance mobEffect : mobEffects){
                if (mobEffect.getEffect().getCategory().equals(MobEffectCategory.HARMFUL) && getVehicle() instanceof LivingEntity living){
                    living.addEffect(new MobEffectInstance(mobEffect.getEffect(), (int) (mobEffect.getDuration() * 0.1),mobEffect.getAmplifier() > 0 ? mobEffect.getAmplifier() - mobEffect.getAmplifier() + 1 : 0));
                }
            }

        }
    }
}
