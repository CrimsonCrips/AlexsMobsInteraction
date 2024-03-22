package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.ReflectionUtil;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;




@Mixin(EntityAnaconda.class)
public class AIAnaconda extends Mob {


    static{
        HUNGER = SynchedEntityData.defineId(EntityAnaconda.class, EntityDataSerializers.INT);
    }
    private static final EntityDataAccessor<Integer> HUNGER;

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void defineSynched(CallbackInfo ci){
        this.entityData.define(HUNGER, 0);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void addAdditional(CompoundTag compound, CallbackInfo ci){
        compound.putInt("Hunger", this.getHunger());
    }
    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readAdditional(CompoundTag compound, CallbackInfo ci){
        this.setHunger(compound.getInt("Hunger"));

    }

    public int getHunger() {
        return (Integer)this.entityData.get(HUNGER);
    }

    public void setHunger(int hunger) {
        this.entityData.set(HUNGER, hunger);
    }

    protected AIAnaconda(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "registerGoals", at = @At("HEAD"),cancellable = true)
    private void AnacondaGoals(CallbackInfo ci){
        ci.cancel();
        EntityAnaconda anaconda = (EntityAnaconda)(Object)this;
        this.goalSelector.addGoal(1, new AnimalAIPanicBaby(anaconda, 1.25));
        Object aiAIMelee = ReflectionUtil.createInstance(
                "com.github.alexthe666.alexsmobs.entity.EntityAnaconda$AIMelee",
                new Class[]{EntityAnaconda.class},
                new Object[]{anaconda}
        );
        this.goalSelector.addGoal(2, (Goal) aiAIMelee);
        this.goalSelector.addGoal(3, new AnimalAIFindWater(anaconda));
        this.goalSelector.addGoal(3, new AnimalAILeaveWater(anaconda));
        this.goalSelector.addGoal(4, new TemptGoal(anaconda, 1.25, Ingredient.of(new ItemLike[]{Items.CHICKEN, Items.COOKED_CHICKEN}), false));
        this.goalSelector.addGoal(5, new BreedGoal(anaconda, 1.0));
        this.goalSelector.addGoal(6, new FollowParentGoal(anaconda, 1.1));
        this.goalSelector.addGoal(7, new AnimalAIWanderRanged(anaconda, 60, 1.0, 14, 7));
        this.goalSelector.addGoal(8, new SemiAquaticAIRandomSwimming(anaconda, 1.5, 7));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 25.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, LivingEntity.class, 200, false, false, AMEntityRegistry.buildPredicateFromTag(AMTagRegistry.ANACONDA_TARGETS)));
        this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, Player.class, 110, false, true, (Predicate)null) {
            public boolean canUse() {
                return !anaconda.isBaby() && (int) ReflectionUtil.getField(anaconda, "passiveFor") == 0 && anaconda.level().getDifficulty() != Difficulty.PEACEFUL && !anaconda.isInLove() && super.canUse();
            }
        });
        this.targetSelector.addGoal(3, new HurtByTargetGoal(anaconda, EntityAnaconda.class));


        Predicate<LivingEntity> ANACONDA_BABY_TARGETS = AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.ANACONDA_BABY_KILL);
        Predicate<LivingEntity> anacondaBaby = (livingEntity) -> {
            return ANACONDA_BABY_TARGETS.test(livingEntity) && level().isNight() && livingEntity.isBaby();
        };
        Predicate<LivingEntity> ancaondacannibalism = (livingEntity) -> {
            return (livingEntity.getHealth() <= 0.20F * livingEntity.getMaxHealth() || livingEntity.isBaby()) && this.getHunger() >= 5000;
        };
        if (AInteractionConfig.anacondaattackbabies){
            this.targetSelector.addGoal(5, new EntityAINearestTarget3D<>(anaconda, LivingEntity.class, 1000, true, false, anacondaBaby) {
                public void start() {
                    super.start();
                    setHunger(0);
                }
                protected AABB getTargetSearchArea(double targetDistance) {
                    return anaconda.getBoundingBox().inflate(25D, 1D, 25D);
                }
            });
        }


        if (AInteractionConfig.anacondacanibalize) {
            this.targetSelector.addGoal(5, new EntityAINearestTarget3D<>(anaconda, EntityAnaconda.class, 10, true, false, ancaondacannibalism) {
                public void start() {super.start(); setHunger(0);;}
            });
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void AlexInteraction$tick(CallbackInfo ci) {
        if (AInteractionConfig.anacondacanibalize) this.setHunger(getHunger() + 1);;
    }

}
