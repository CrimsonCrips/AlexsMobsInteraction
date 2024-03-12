package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.ReflectionUtil;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;




@Mixin(EntityAnaconda.class)
public class AIAnaconda extends Mob {

    private int ate = 0;
    protected AIAnaconda(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void AnacondaGoals(CallbackInfo ci){
        EntityAnaconda anaconda = (EntityAnaconda)(Object)this;
        Predicate<LivingEntity> ANACONDA_BABY_TARGETS = AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.ANACONDA_BABY_KILL);
        Predicate<LivingEntity> anacondaBaby = (livingEntity) -> {
            return ANACONDA_BABY_TARGETS.test(livingEntity) && level().isNight() && livingEntity.isBaby();
        };
        Predicate<LivingEntity> ancaondacannibalism = (livingEntity) -> {
            return (livingEntity.getHealth() <= 0.20F * livingEntity.getMaxHealth() || livingEntity.isBaby()) && ate >= 5000;
        };
        if (AInteractionConfig.anacondaattackbabies){
            this.targetSelector.addGoal(5, new EntityAINearestTarget3D<>(anaconda, LivingEntity.class, 1000, true, false, anacondaBaby) {
                public void start() {
                    super.start();
                    ate = 0;
                }
                protected AABB getTargetSearchArea(double targetDistance) {
                    return anaconda.getBoundingBox().inflate(25D, 1D, 25D);
                }
            });
        }


        if (AInteractionConfig.anacondacanibalize) {
            this.targetSelector.addGoal(5, new EntityAINearestTarget3D<>(anaconda, EntityAnaconda.class, 10, true, false, ancaondacannibalism) {
                public void start() {super.start(); ate = 0;}
            });
        }
    }
    @Inject(method = "tick", at = @At("HEAD"))
    private void AlexInteraction$tick(CallbackInfo ci) {
        if (AInteractionConfig.anacondacanibalize) ate++;
    }

}
