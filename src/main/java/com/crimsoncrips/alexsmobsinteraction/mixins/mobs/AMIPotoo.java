package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityFly;
import com.github.alexthe666.alexsmobs.entity.EntityPotoo;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityPotoo.class)
public abstract class AMIPotoo extends Animal {


    protected AMIPotoo(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }


    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        EntityPotoo potoo = (EntityPotoo)(Object)this;
        if (AlexsMobsInteraction.TARGETS_CONFIG.POTOO_ENABLED.get()){
            potoo.targetSelector.addGoal(3, new EntityAINearestTarget3D<>(potoo, EntityFly.class, 600, true, false, LivingEntity::isAlive));
        }

    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/alexsmobs/entity/EntityPotoo;gameEvent(Lnet/minecraft/world/level/gameevent/GameEvent;)V"))
    private void alexsMobsInteraction$tick(CallbackInfo ci) {
        EntityPotoo potoo = (EntityPotoo)(Object)this;
        if (AlexsMobsInteraction.COMMON_CONFIG.POTOO_VISION_ENABLED.get()){
            if (potoo.getVehicle() instanceof Player player && !player.hasEffect(MobEffects.NIGHT_VISION)){
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 300, 0));
                player.playSound(SoundEvents.BELL_RESONATE);
                RandomSource randomSource = player.getRandom();
                AMIUtils.addParticlesAroundSelf(ParticleTypes.END_ROD,player,6,1);
                AMIUtils.awardAdvancement(player,"potoo_vision","vision");
            }
        }
    }




}
