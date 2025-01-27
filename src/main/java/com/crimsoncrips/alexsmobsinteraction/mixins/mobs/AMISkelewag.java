package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.server.effect.AMIEffects;
import com.crimsoncrips.alexsmobsinteraction.server.enchantment.AMIEnchantmentRegistry;
import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.crimsoncrips.alexsmobsinteraction.server.goal.AMISkelewagCircleGoal;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntitySkelewag.class)
public class AMISkelewag extends Mob {

    protected AMISkelewag(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }


    private boolean stun;

    @Override
    protected boolean isImmobile() {
        return stun;
    }



    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        if (AMInteractionConfig.CHARGE_STUN_ENABLED) {
            stun = this.hasEffect(AMIEffects.DISABLED.get());

            LivingEntity target = getTarget();


            if (this.getTarget() instanceof Player player && (player.getItemBySlot(EquipmentSlot.OFFHAND).getEnchantmentLevel(AMIEnchantmentRegistry.FINAL_STAND.get()) > 0 || player.getItemBySlot(EquipmentSlot.MAINHAND).getEnchantmentLevel(AMIEnchantmentRegistry.FINAL_STAND.get()) > 0)) {
                if (this.distanceTo(this.getTarget()) < 3F && this.hasLineOfSight(this.getTarget()) && this.getTarget().isBlocking() && !stun) {
                    stun = true;
                    this.playSound(SoundEvents.SHIELD_BLOCK, 2F, 1F);
                    target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 0));
                    target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 150, 1));
                    this.addEffect(new MobEffectInstance(AMIEffects.DISABLED.get(), 500, 1));
                    this.setTarget(null);
                }
            }

        }
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        EntitySkelewag skelewag = (EntitySkelewag)(Object)this;
        if (AMInteractionConfig.SKELEWAG_CIRCLE_ENABLED){
            skelewag.goalSelector.addGoal(1, new AMISkelewagCircleGoal(skelewag,1F));
        }
        if (AMInteractionConfig.MIGHT_UPGRADE_ENABLED){
            skelewag.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(skelewag, Player.class, 100, true, false, (livingEntity) -> {
                return !livingEntity.hasEffect(AMEffectRegistry.ORCAS_MIGHT.get());
            }));
        }
    }

    @WrapWithCondition(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V",ordinal = 1))
    private boolean attackGoal(GoalSelector instance, int pPriority, Goal pGoal) {
        return !AMInteractionConfig.SKELEWAG_CIRCLE_ENABLED;
    }

    @WrapWithCondition(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V",ordinal = 4))
    private boolean nearestTarget(GoalSelector instance, int pPriority, Goal pGoal) {
        return !AMInteractionConfig.MIGHT_UPGRADE_ENABLED;
    }

    @WrapWithCondition(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V",ordinal = 5))
    private boolean nearestTarget2(GoalSelector instance, int pPriority, Goal pGoal) {
        return !AMInteractionConfig.MIGHT_UPGRADE_ENABLED;
    }

}
