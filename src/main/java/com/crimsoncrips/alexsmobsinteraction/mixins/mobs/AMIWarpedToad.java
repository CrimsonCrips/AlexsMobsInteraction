package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import net.minecraft.world.food.FoodProperties;
import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityWarpedToad;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;


@Mixin(EntityWarpedToad.class)
public abstract class AMIWarpedToad extends Animal {


    protected AMIWarpedToad(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void alexsMobsInteraction$registerGoals(CallbackInfo ci) {
        EntityWarpedToad warpedToad = (EntityWarpedToad)(Object)this;
        if(!AlexsMobsInteraction.COMMON_CONFIG.TAMED_FRIENDLIES_ENABLED.get())
            return;
        warpedToad.targetSelector.addGoal(4, new EntityAINearestTarget3D<>(warpedToad, LivingEntity.class, 50, false, true, AMEntityRegistry.buildPredicateFromTag(AMTagRegistry.WARPED_TOAD_TARGETS)){
            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && !warpedToad.isTame();
            }
        });
    }

    @WrapWithCondition(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V",ordinal = 15))
    private boolean alexsMobsInteraction$registerGoals1(GoalSelector instance, int pPriority, Goal pGoal) {
        return !AlexsMobsInteraction.COMMON_CONFIG.TAMED_FRIENDLIES_ENABLED.get();
    }

    @Inject(method = "onGetItem", at = @At("TAIL"),remap = false)
    private void alexsMobsInteraction$onGetItem(ItemEntity e, CallbackInfo ci) {
        if (e.getItem().getFoodProperties(this) != null && AlexsMobsInteraction.COMMON_CONFIG.FOOD_FX_ENABLED.get()) {
            this.heal(5);
            List<FoodProperties.PossibleEffect> test = Objects.requireNonNull(e.getItem().getFoodProperties(this)).effects();
            if (!test.isEmpty()){
                for (int i = 0; i < test.size(); i++){
                    this.addEffect(new MobEffectInstance(test.get(i).effect()));
                }
            }
        }
    }

}
