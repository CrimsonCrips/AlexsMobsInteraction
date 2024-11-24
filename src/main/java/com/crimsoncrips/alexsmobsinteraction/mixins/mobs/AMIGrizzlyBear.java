package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AMInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityAlligatorSnappingTurtle;
import com.github.alexthe666.alexsmobs.entity.EntityGrizzlyBear;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;

import static com.crimsoncrips.alexsmobsinteraction.AMInteractionTagRegistry.GRIZZLY_KILL;
import static com.crimsoncrips.alexsmobsinteraction.AMInteractionTagRegistry.GRIZZLY_TERRITORIAL;


@Mixin(EntityGrizzlyBear.class)
public abstract class AMIGrizzlyBear extends Animal {

    protected AMIGrizzlyBear(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }


    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        EntityGrizzlyBear grizzlyBear = (EntityGrizzlyBear) (Object) this;
            grizzlyBear.goalSelector.addGoal(5, new TameableAITempt(grizzlyBear, 1.1D, Ingredient.of(AMInteractionTagRegistry.GRIZZLY_ENTICE), false));
            grizzlyBear.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(grizzlyBear, LivingEntity.class, 4000, true, false, AMEntityRegistry.buildPredicateFromTag(GRIZZLY_KILL)) {
                public boolean canUse() {
                    return super.canUse() && !grizzlyBear.isTame() && !grizzlyBear.isHoneyed();
                }
            });
            if (AMInteractionConfig.GRIZZLY_FRIENDLY_ENABLED) {
                grizzlyBear.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(grizzlyBear, LivingEntity.class, 300, true, true, AMEntityRegistry.buildPredicateFromTag(GRIZZLY_TERRITORIAL)) {
                    public boolean canUse() {
                        return super.canUse() && !grizzlyBear.isTame();
                    }
                });
                grizzlyBear.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(grizzlyBear, Player.class, 10, true, true, null) {
                    public boolean canUse() {
                        return super.canUse() && !grizzlyBear.isTame();
                    }
                });
            }
    }

    @WrapWithCondition(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V",ordinal = 6))
    private boolean tameable(GoalSelector instance, int pPriority, Goal pGoal) {
        return false;
    }

    @WrapWithCondition(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V",ordinal = 18))
    private boolean attackPlayer(GoalSelector instance, int pPriority, Goal pGoal) {
        return !AMInteractionConfig.GRIZZLY_FRIENDLY_ENABLED;
    }

    @WrapWithCondition(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V",ordinal = 19))
    private boolean nearbyAttack(GoalSelector instance, int pPriority, Goal pGoal) {
        return !AMInteractionConfig.GRIZZLY_FRIENDLY_ENABLED;
    }

    @Inject(method = "onGetItem", at = @At("TAIL"),remap = false)
    private void getItem(ItemEntity e, CallbackInfo ci) {
        if (e.getItem().isEdible() && AMInteractionConfig.FOOD_TARGET_EFFECTS_ENABLED) {
            this.heal(5);
            List<Pair<MobEffectInstance, Float>> test = Objects.requireNonNull(e.getItem().getFoodProperties(this)).getEffects();
            if (!test.isEmpty()){
                for (int i = 0; i < test.size(); i++){
                    this.addEffect(new MobEffectInstance(test.get(i).getFirst()));
                }
            }
        }
    }

}
