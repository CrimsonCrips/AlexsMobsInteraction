package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityAlligatorSnappingTurtle;
import com.github.alexthe666.alexsmobs.entity.EntityLaviathan;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIFindWater;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAILeaveWater;
import com.github.alexthe666.alexsmobs.entity.ai.BottomFeederAIWander;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.function.Predicate;


@Mixin(EntityAlligatorSnappingTurtle.class)
public abstract class AIAlligatorSnappingTurtle extends Mob {

    static{
        RAINMOSS = SynchedEntityData.defineId(EntityAlligatorSnappingTurtle.class, EntityDataSerializers.INT);
    }
    private static final EntityDataAccessor<Integer> RAINMOSS;

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void defineSynched(CallbackInfo ci){
        this.entityData.define(RAINMOSS, 0);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void addAdditional(CompoundTag compound, CallbackInfo ci){
        compound.putInt("RainMoss", this.getRainMossTime());
    }
    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readAdditional(CompoundTag compound, CallbackInfo ci){
        this.setRainMossTime(compound.getInt("RainMoss"));

    }

    public int getRainMossTime() {
        return (Integer)this.entityData.get(RAINMOSS);
    }

    public void setRainMossTime(int rainmoss) {
        this.entityData.set(RAINMOSS, rainmoss);
    }

    protected AIAlligatorSnappingTurtle(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Shadow public abstract void setTarget(@Nullable LivingEntity entitylivingbaseIn);

    @Inject(method = "registerGoals", at = @At("HEAD"),cancellable = true)
    private void SnappingTurtleGoals(CallbackInfo ci){
        ci.cancel();
        EntityAlligatorSnappingTurtle snapping = (EntityAlligatorSnappingTurtle)(Object)this;
        Predicate<LivingEntity> TARGET_PRED = (animal) -> {
            return !(animal instanceof EntityAlligatorSnappingTurtle) && !(animal instanceof ArmorStand) && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(animal) && animal.isAlive();
        };

        this.goalSelector.addGoal(1, new MeleeAttackGoal((EntityAlligatorSnappingTurtle)(Object)this, 1.3D, false));
        this.goalSelector.addGoal(2, new AnimalAIFindWater((EntityAlligatorSnappingTurtle)(Object)this));
        this.goalSelector.addGoal(2, new AnimalAILeaveWater((EntityAlligatorSnappingTurtle)(Object)this){
            public boolean canUse(){
                if (AInteractionConfig.snappingturtledormant) {return super.canUse() && level().isNight();}
                else {return super.canUse();}
            }});

        this.goalSelector.addGoal(3, new BottomFeederAIWander((EntityAlligatorSnappingTurtle)(Object)this, 1.0D, 120, 150, 10){
            public boolean canUse(){
                if (AInteractionConfig.snappingturtledormant) {return super.canUse() && level().isNight();}
                else {return super.canUse();}
            }});

        this.goalSelector.addGoal(3, new BreedGoal((EntityAlligatorSnappingTurtle)(Object)this, 1.0D));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this){
            public boolean canUse() {
                if (AInteractionConfig.snappingturtledormant) { return super.canUse() && level().isNight();}
                else { return super.canUse();}
            }});

        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F){
            public boolean canUse() {
                if (AInteractionConfig.snappingturtledormant) { return super.canUse() && level().isNight();}
                else { return super.canUse();}
            }});

        this.targetSelector.addGoal(1, new HurtByTargetGoal((EntityAlligatorSnappingTurtle)(Object)this, new Class[0]));
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, LivingEntity.class, 2, false, true, TARGET_PRED) {
            protected AABB getTargetSearchArea(double targetDistance) {
                return this.mob.getBoundingBox().inflate(0.5D, 2D, 0.5D);
            }
            public boolean canUse () {
                return snapping.chaseTime >= 0 && super.canUse();
            }

        });

        this.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(this, LivingEntity.class, 1, true, false, AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.SNAPPING_TURTLE_KILL)){
            public boolean canUse() {
                if (AInteractionConfig.snappingturtledormant) {return snapping.chaseTime >= 0 && super.canUse() && level().isNight();}
                else {return snapping.chaseTime >= 0 && super.canUse();}
            }
            protected AABB getTargetSearchArea(double targetDistance) {
                return this.mob.getBoundingBox().inflate(10D, 1D, 10D);
            }});
    }
    @Inject(method = "tick", at = @At("HEAD"))
    private void AlexInteraction$tick(CallbackInfo ci) {
        EntityAlligatorSnappingTurtle snapping = (EntityAlligatorSnappingTurtle)(Object)this;
        if (AInteractionConfig.weakened) {
            if((snapping.getHealth() <= 0.10F * getMaxHealth()) && snapping.getTarget() instanceof Player){
                setTarget(null);
            }
        }
        if(AInteractionConfig.snappingturtlemossincrease){
            this.setRainMossTime(this.getRainMossTime() + 1);
            if (level().isRaining() && random.nextDouble() < 0.0001) {
                snapping.setMoss(Math.min(10, snapping.getMoss() + 1));
            }
            if (this.isInWater() && this.getRainMossTime() > 12000) {
                this.setRainMossTime(0);
                snapping.setMoss(Math.min(10, snapping.getMoss() + 1));
            }
        }
    }
}
