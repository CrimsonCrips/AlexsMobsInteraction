package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityKomodoDragon;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;


@Mixin(EntityKomodoDragon.class)
public abstract class AMIKomodoDragon extends TamableAnimal {


    protected AMIKomodoDragon(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }


    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        EntityKomodoDragon komodoDragon = (EntityKomodoDragon)(Object)this;
        komodoDragon.targetSelector.addGoal(8, new EntityAINearestTarget3D<>(komodoDragon, LivingEntity.class, 180, false, true, AMEntityRegistry.buildPredicateFromTag(AMTagRegistry.KOMODO_DRAGON_TARGETS)));

        if (!AMInteractionConfig.FRIENDLY_KOMODO_ENABLED)
            return;

        komodoDragon.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(komodoDragon, EntityKomodoDragon.class, 50, true, false, (livingEntity) -> {
            return livingEntity.isBaby() || livingEntity.getHealth() <= 0.7F * livingEntity.getMaxHealth();
        }){
            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && !komodoDragon.isTame();
            }
        });
        komodoDragon.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(komodoDragon, Player.class, 150, true, true, (Predicate)null){
            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && !komodoDragon.isTame();
            }
        });
    }

    @WrapWithCondition(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V",ordinal = 15))
    private boolean nearestTarget(GoalSelector instance, int pPriority, Goal pGoal) {
        return !AMInteractionConfig.FRIENDLY_KOMODO_ENABLED;
    }

    @WrapWithCondition(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V",ordinal = 16))
    private boolean nearestTarget2(GoalSelector instance, int pPriority, Goal pGoal) {
        return !AMInteractionConfig.FRIENDLY_KOMODO_ENABLED;
    }

    @WrapWithCondition(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V",ordinal = 17))
    private boolean target3D(GoalSelector instance, int pPriority, Goal pGoal) {
        return !AMInteractionConfig.FRIENDLY_KOMODO_ENABLED;
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
