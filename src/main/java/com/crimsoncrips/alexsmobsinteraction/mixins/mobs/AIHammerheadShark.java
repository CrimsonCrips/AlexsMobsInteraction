package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.goal.AIHammerCircleReplace;
import com.crimsoncrips.alexsmobsinteraction.AInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.AbstractSchoolingFish;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;


@Mixin(EntityHammerheadShark.class)
public class AIHammerheadShark extends Mob {

    private static final Predicate<LivingEntity> MANTIS_EAT = (mob) -> {
        return mob.getHealth() <= 0.2 * mob.getMaxHealth();
    };
    private static final Predicate<LivingEntity> INJURED_PREDICATE = (mob) -> {
        return (double)mob.getHealth() <= (double)mob.getMaxHealth() / 2.0;
    };

    protected AIHammerheadShark(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "registerGoals", at = @At("HEAD"),cancellable = true)
    private void HammerGoals(CallbackInfo ci){
        ci.cancel();
        EntityHammerheadShark hammerheadShark = (EntityHammerheadShark)(Object)this;
        this.goalSelector.addGoal(1, new TryFindWaterGoal(hammerheadShark));
        this.goalSelector.addGoal(1, new AIHammerCircleReplace(hammerheadShark, 1.0F));
        this.goalSelector.addGoal(4, new RandomSwimmingGoal(hammerheadShark, 0.6000000238418579, 7));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(8, new FollowBoatGoal(hammerheadShark));
        this.goalSelector.addGoal(9, new AvoidEntityGoal(hammerheadShark, Guardian.class, 8.0F, 1.0, 1.0));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(hammerheadShark, new Class[0]));
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, LivingEntity.class, 50, false, true, INJURED_PREDICATE));
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, Squid.class, 50, false, true, (Predicate)null));
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, EntityMimicOctopus.class, 80, false, true, (Predicate)null));
        this.targetSelector.addGoal(3, new EntityAINearestTarget3D(this, AbstractSchoolingFish.class, 70, false, true, (Predicate)null));

        if (AInteractionConfig.hammerheadhuntmantisshrimp){
            this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, EntityMantisShrimp.class, 50, true, false, MANTIS_EAT));
        }
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, LivingEntity.class, 0, true, false, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.HAMMERHEAD_KILL)));

    }





}
