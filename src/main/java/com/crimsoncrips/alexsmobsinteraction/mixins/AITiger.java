package com.crimsoncrips.alexsmobsinteraction.mixins;

import com.crimsoncrips.alexsmobsinteraction.AInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.ReflectionUtil;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIPanicBaby;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.entity.ai.CreatureAITargetItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;


@Mixin(EntityTiger.class)
public class AITiger extends Mob {


    protected AITiger(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "registerGoals", at = @At("HEAD"),cancellable = true)
    private void TigerGoals(CallbackInfo ci){
        ci.cancel();
        EntityTiger tiger = (EntityTiger)(Object)this;
        Predicate<LivingEntity> NOBLESSING = (livingEntity) -> {
                return !livingEntity.hasEffect(AMEffectRegistry.TIGERS_BLESSING.get());
        };
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new AnimalAIPanicBaby(tiger, 1.25D));
        Object aiMeleeAttackGoal = ReflectionUtil.createInstance(
                "com.github.alexthe666.alexsmobs.entity.EntityTiger$AIMelee",
                new Class[] {EntityTiger.class},
                new Object[]    {tiger}
        );
        this.goalSelector.addGoal(2,(Goal)aiMeleeAttackGoal);
        this.goalSelector.addGoal(5, new BreedGoal(tiger, 1.0D));
        this.goalSelector.addGoal(6, new FollowParentGoal(tiger, 1.1D));
        this.goalSelector.addGoal(7, new AnimalAIWanderRanged(tiger, 60, 1.0D, 14, 7));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 25F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new CreatureAITargetItems(tiger, false, 10));
        this.targetSelector.addGoal(1, new HurtByTargetGoal((EntityTiger)(Object)this) {
            public void start() {
                super.start();
                if (tiger.isBaby()) {
                    this.alertOthers();
                    this.stop();
                }

            }
            protected void alertOther(Mob mobIn, LivingEntity targetIn) {
                if (mobIn instanceof EntityTiger && !isBaby()) {
                    super.alertOther(mobIn, targetIn);
                }
            }

        });
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal(this, Player.class, 500, true, false,NOBLESSING) {
            public boolean canUse() {
                if (AInteractionConfig.weakened) {
                    return !isBaby() && !(getHealth() <= 0.20F * getMaxHealth()) && !tiger.isInLove() && super.canUse();
                } else {
                    return !isBaby() && !tiger.isInLove() && super.canUse();
                }


            }
            protected double getFollowDistance() {
                return 4.0;
            }
        });
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal(this, LivingEntity.class, 220, true, false, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.TIGER_KILL)) {

        });
        this.targetSelector.addGoal(5, new ResetUniversalAngerTargetGoal<>(tiger, true));

    }
}
