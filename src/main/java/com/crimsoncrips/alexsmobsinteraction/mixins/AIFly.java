package com.crimsoncrips.alexsmobsinteraction.mixins;

import com.crimsoncrips.alexsmobsinteraction.AInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.AvoidBlockGoal;
import com.crimsoncrips.alexsmobsinteraction.FollowNearestGoal;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityFly;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityFly.class)
public class AIFly extends Mob {

    private boolean noFollow = false;

    protected AIFly(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void FlyGoals(CallbackInfo ci){
        EntityFly fly = (EntityFly)(Object)this;
        java.util.function.Predicate<LivingEntity> PESTERTARGET = AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.FLY_PESTER);
        if(AInteractionConfig.flyfearall) {
            this.goalSelector.addGoal(7, new AvoidEntityGoal<>(fly, LivingEntity.class, 2.0F, 1.1, 1.3, (livingEntity) ->{
                return !PESTERTARGET.test(livingEntity) && !(livingEntity instanceof EntityFly);
            }));
        }
        if(AInteractionConfig.flyfearcandles){
            this.goalSelector.addGoal(3, new AvoidBlockGoal(fly, 4, 1, 1.2, (pos) -> {
                BlockState state = level().getBlockState(pos);
                return state.is(BlockTags.CANDLES);
            }));
        }
        if(AInteractionConfig.flypester) {
            this.goalSelector.addGoal(8, new FollowNearestGoal<>(fly, LivingEntity.class, 3, 1.2, (livingEntity) -> {
                return PESTERTARGET.test(livingEntity) && !noFollow;
            }));
        }
    }
    @Inject(method = "tick", at = @At("HEAD"))
    private void AlexInteraction$tick(CallbackInfo ci) {
        if(AInteractionConfig.flypester){
            if (random.nextDouble() < 0.001 && !noFollow || level().isNight()) noFollow = true;
            if (random.nextDouble() < 0.05 && noFollow && level().isDay()) noFollow = false;
        }
    }
}
