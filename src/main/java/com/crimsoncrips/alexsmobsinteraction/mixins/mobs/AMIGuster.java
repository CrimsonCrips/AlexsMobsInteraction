package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.server.effect.AMIEffects;
import com.crimsoncrips.alexsmobsinteraction.server.enchantment.AMIEnchantmentRegistry;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.entity.*;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;


@Mixin(EntityGuster.class)
public class AMIGuster extends Mob {

    protected AMIGuster(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @WrapOperation(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setDeltaMovement(DDD)V"),remap = false)
    private void aiStep(Entity instance, double pX, double pY, double pZ, Operation<Void> original, @Local Entity lifted,@Local(ordinal = 1) float resist,@Local(ordinal = 2) double d0,@Local(ordinal = 3) double d1) {
        if(AlexsMobsInteraction.COMMON_CONFIG.GUSTING_EFFECT_ENABLED.get() && lifted instanceof LivingEntity living && !living.hasEffect(AMIEffects.GUSTING.get())){
            if (lifted instanceof Player player && AlexsMobsInteraction.COMMON_CONFIG.GUSTER_WEIGHT_ENABLED.get()) {
                int lift = player.getArmorValue();
                lifted(lift, player, d0, d1, resist);
            } else {
                original.call(instance,pX,pY,pZ);
            }
        }
    }

    @Override
    public boolean canBeHitByProjectile() {
        return !AlexsMobsInteraction.COMMON_CONFIG.GUSTER_PROJECTILE_PROT_ENABLED.get();
    }

    public void lifted(int lift,Player lifted, double d0,double d1,float resist){
        float multiplier = 0.1f - lift / 3.0f;
        multiplier = Math.max(multiplier, 0.0f);
        lifted.setDeltaMovement(d0, multiplier * resist, d1);
    }

}
