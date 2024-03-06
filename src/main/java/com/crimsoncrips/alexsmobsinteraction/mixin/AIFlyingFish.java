package com.crimsoncrips.alexsmobsinteraction.mixin;

import com.crimsoncrips.alexsmobsinteraction.AInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.AvoidBlockGoal;
import com.crimsoncrips.alexsmobsinteraction.FollowNearestGoal;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityFly;
import com.github.alexthe666.alexsmobs.entity.EntityFlyingFish;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;


@Mixin(EntityFlyingFish.class)
public class AIFlyingFish extends Mob {

    protected AIFlyingFish(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void FlyingFishGoals(CallbackInfo ci){
        if(AInteractionConfig.preyfear) {
            this.goalSelector.addGoal(3, new AvoidEntityGoal<>((EntityFlyingFish) (Object) this, LivingEntity.class, 4.0F, 1.3, 1.7, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.FISHFEAR)));
        }
    }
}
