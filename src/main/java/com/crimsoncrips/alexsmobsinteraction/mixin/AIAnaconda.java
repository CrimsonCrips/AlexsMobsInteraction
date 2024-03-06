package com.crimsoncrips.alexsmobsinteraction.mixin;

import com.crimsoncrips.alexsmobsinteraction.AInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityAnaconda;
import com.github.alexthe666.alexsmobs.entity.EntityFly;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import io.netty.util.internal.ReflectionUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
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
        Predicate<LivingEntity> ANACONDA_BABY_TARGETS = AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.ANACONDA_BABY_KILL);
        Predicate<LivingEntity> anacondaBaby = (livingEntity) -> {
            return ANACONDA_BABY_TARGETS.test(livingEntity) && level().isNight() && livingEntity.isBaby();
        };
        Predicate<LivingEntity> ancaondacannibalism = (livingEntity) -> {
            return (livingEntity.getHealth() <= 0.20F * livingEntity.getMaxHealth() || livingEntity.isBaby()) && ate >= 5000;
        };

        this.targetSelector.addGoal(5, new EntityAINearestTarget3D<>(this, LivingEntity.class, 50, true, false, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.ANACONDA_KILL)){
            public void start() {super.start(); ate = 0;}
            protected AABB getTargetSearchArea(double targetDistance) {
                return this.mob.getBoundingBox().inflate(25D, 1D, 25D);
            }
        });
        if (AInteractionConfig.anacondaattackbabies){
            this.targetSelector.addGoal(5, new EntityAINearestTarget3D<>(this, LivingEntity.class, 4000, true, false, anacondaBaby) {
                public void start() {
                    super.start();
                    ate = 0;
                }
                protected AABB getTargetSearchArea(double targetDistance) {
                    return this.mob.getBoundingBox().inflate(25D, 1D, 25D);
                }
            });
        }


        if (AInteractionConfig.anacondacanibalize) {
            this.targetSelector.addGoal(5, new EntityAINearestTarget3D<>(this, EntityAnaconda.class, 200, true, false, ancaondacannibalism) {
                public void start() {super.start(); ate = 0;}
            });
        }
    }
    public void awardKillScore(Entity entity, int score, DamageSource src) {
        if(entity instanceof LivingEntity living){
            final CompoundTag emptyNbt = new CompoundTag();
            living.addAdditionalSaveData(emptyNbt);
            emptyNbt.putString("DeathLootTable", BuiltInLootTables.EMPTY.toString());
            living.readAdditionalSaveData(emptyNbt);

        }
        super.awardKillScore(entity, score, src);
    }
    @Inject(method = "tick", at = @At("HEAD"))
    private void AlexInteraction$tick(CallbackInfo ci) {
        if (AInteractionConfig.anacondacanibalize) ate++;
    }


}
