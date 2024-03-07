package com.crimsoncrips.alexsmobsinteraction.mixins;

import com.crimsoncrips.alexsmobsinteraction.ReflectionUtil;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;


@Mixin(EntityEnderiophage.class)
public abstract class AIEnderiophage extends Mob {


    @Shadow public abstract void setVariant(int variant);

    protected AIEnderiophage(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "registerGoals", at = @At("HEAD"),cancellable = true)
    private void CapuchinMonkeyGoals(CallbackInfo ci){
        ci.cancel();
        EntityEnderiophage enderiophage = (EntityEnderiophage)(Object)this;

        Predicate<LivingEntity> ENDERGRADE_OR_INFECTED = (livingEntity) -> {
            if(AInteractionConfig.enderioimmunity) {
                return (livingEntity instanceof EntityEndergrade || livingEntity.hasEffect(AMEffectRegistry.ENDER_FLU.get())) && !livingEntity.hasEffect(MobEffects.DAMAGE_RESISTANCE);
            } else {
                return livingEntity instanceof EntityEndergrade || livingEntity.hasEffect(AMEffectRegistry.ENDER_FLU.get());
            }
        };
        Predicate<LivingEntity> NONRESIST = (livingEntity) -> {
            if(AInteractionConfig.enderioimmunity) {
                return !livingEntity.hasEffect(MobEffects.DAMAGE_RESISTANCE);
            }
            return true;
        };
        Predicate<LivingEntity> weakEnough = (livingEntity) -> {
            return livingEntity.getHealth() <= 0.40F * livingEntity.getMaxHealth() && !livingEntity.hasEffect(MobEffects.DAMAGE_RESISTANCE);
        };
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new EntityEnderiophage.FlyTowardsTarget(enderiophage));
Object aiWalkIdle = ReflectionUtil.createInstance(
        "com.github.alexthe666.alexsmobs.entity.EntityEnderiophage$AIWalkIdle",
        new Class[] {EntityEnderiophage.class},
        new Object[]    {enderiophage}
);
this.goalSelector.addGoal(2,(Goal)aiWalkIdle);

        this.targetSelector.addGoal(1, new EntityAINearestTarget3D(this, EnderMan.class, 15, true, true, NONRESIST) {
            public boolean canUse() {
                return enderiophage.isMissingEye() && super.canUse();
            }

            public boolean canContinueToUse() {
                return enderiophage.isMissingEye() && super.canContinueToUse();
            }
        });
        this.targetSelector.addGoal(1, new EntityAINearestTarget3D(this, LivingEntity.class, 15, true, true, ENDERGRADE_OR_INFECTED) {
            public boolean canUse() {
                return !enderiophage.isMissingEye() &&(int) ReflectionUtil.getField(enderiophage, "fleeAfterStealTime") == 0 && super.canUse();
            }

            public boolean canContinueToUse() {
                return !enderiophage.isMissingEye() && super.canContinueToUse();
            }
        });
        this.targetSelector.addGoal(3, new HurtByTargetGoal(enderiophage, EnderMan.class));
        if(AInteractionConfig.enderiophageplayer){
            this.targetSelector.addGoal(1, new EntityAINearestTarget3D(this, Player.class, 15, true, true, weakEnough) {
                public boolean canUse() {
                    return !enderiophage.isMissingEye() && (int) ReflectionUtil.getField(enderiophage, "fleeAfterStealTime") == 0 && super.canUse();
                }

                public boolean canContinueToUse() {
                    return !enderiophage.isMissingEye() && super.canContinueToUse();
                }
            });
        }
    }
    @Inject(method = "tick", at = @At("HEAD"))
    private void AlexInteraction$tick(CallbackInfo ci) {
        if (AInteractionConfig.enderioadaption) {
            if (level().dimension() == Level.NETHER && !this.isNoAi())
                setVariant(2);
            else if (level().dimension() == Level.OVERWORLD && !this.isNoAi())
                setVariant(1);
        }
    }

}
