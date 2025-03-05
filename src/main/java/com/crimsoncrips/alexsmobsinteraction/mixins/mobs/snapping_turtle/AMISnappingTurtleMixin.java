package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.snapping_turtle;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.datagen.tags.AMIEntityTagGenerator;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.AMIBaseInterfaces;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityAlligatorSnappingTurtle;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;


@Mixin(EntityAlligatorSnappingTurtle.class)
public abstract class AMISnappingTurtleMixin extends Animal implements AMIBaseInterfaces {

    @Shadow @Nullable public abstract LivingEntity getTarget();

    @Shadow public abstract void setMoss(int moss);

    @Shadow public abstract int getMoss();

    protected AMISnappingTurtleMixin(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @ModifyArg(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Animal;travel(Lnet/minecraft/world/phys/Vec3;)V"))
    private Vec3 alexsMobsInteraction$travel(Vec3 par1) {
        if (isDaySleeping()){
            return (Vec3.ZERO);
        }
        return par1;
    }

    private static final EntityDataAccessor<Boolean> DAY_SLEEPING = SynchedEntityData.defineId(EntityAlligatorSnappingTurtle.class, EntityDataSerializers.BOOLEAN);

    @Override
    public boolean isDaySleeping() {
        return this.entityData.get(DAY_SLEEPING);
    }

    public void setDaySleeping(boolean sleeping) {
        this.entityData.set(DAY_SLEEPING, Boolean.valueOf(sleeping));
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void define(CallbackInfo ci) {
        this.entityData.define(DAY_SLEEPING, false);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void add(CompoundTag compound, CallbackInfo ci) {
        compound.putBoolean("DaySleeping", isDaySleeping());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void alexsMobsInteraction$read(CompoundTag compound, CallbackInfo ci) {
        this.setDaySleeping(compound.getBoolean("DaySleeping"));
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pHand);
        if (AlexsMobsInteraction.COMMON_CONFIG.MOSS_PROPOGATION_ENABLED.get() && itemStack.getItem() instanceof BoneMealItem) {
            if (!pPlayer.isCreative()) {
                itemStack.shrink(1);
            }
            this.gameEvent(GameEvent.ENTITY_INTERACT);
            this.playSound(SoundEvents.BONE_MEAL_USE, 1, 1);

            RandomSource randomSource = this.getRandom();
            for(int i = 0; i < 8; ++i) {
                double d0 = randomSource.nextGaussian() * 0.02D;
                double d1 = randomSource.nextGaussian() * 0.02D;
                double d2 = randomSource.nextGaussian() * 0.02D;
                this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0D), this.getRandomY() + 1.0D, this.getRandomZ(1.0D), d0, d1, d2);
            }



            if (randomSource.nextDouble() < 0.05) {
                this.setMoss(this.getMoss() + 1);
            }
        }
        
        return super.mobInteract(pPlayer, pHand);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void alexsMobsInteraction$registerGoals(CallbackInfo ci) {
        EntityAlligatorSnappingTurtle snappingturtle = (EntityAlligatorSnappingTurtle)(Object)this;
        snappingturtle.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(snappingturtle, LivingEntity.class, 1, true, false, AMEntityRegistry.buildPredicateFromTag(AMIEntityTagGenerator.SIGNIFICANT_PREY)){
            @Override
            public boolean canUse() {
                return snappingturtle.isInWater() && super.canUse();
            }
            @Override
            protected AABB getTargetSearchArea(double targetDistance) {
                return this.mob.getBoundingBox().inflate(10D, 1D, 10D);
            }
        });

        if (AlexsMobsInteraction.COMMON_CONFIG.SNAPPING_DORMANCY_ENABLED.get()){
            this.goalSelector.addGoal(5, new RandomLookAroundGoal(this){
                @Override
                public boolean canContinueToUse() {
                    return super.canContinueToUse() && !isDaySleeping();
                }
            });
            this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F){
                @Override
                public boolean canContinueToUse() {
                    return super.canContinueToUse() && !isDaySleeping();
                }
            });
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void alexsMobsInteraction$tick(CallbackInfo ci) {
        Level level = this.level();
        if (!level.isClientSide){
            boolean awake = level.isNight() || level.isRaining() || level.isThundering() || this.getTarget() != null || this.isInLove();
            setDaySleeping(!awake);

        }
    }

    @WrapWithCondition(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V",ordinal = 5))
    private boolean alexsMobsInteraction$lookRandom(GoalSelector instance, int pPriority, Goal pGoal) {
        return !AlexsMobsInteraction.COMMON_CONFIG.SNAPPING_DORMANCY_ENABLED.get();
    }

    @WrapWithCondition(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V",ordinal = 6))
    private boolean alexsMobsInteraction$lookPlayer(GoalSelector instance, int pPriority, Goal pGoal) {
        return !AlexsMobsInteraction.COMMON_CONFIG.SNAPPING_DORMANCY_ENABLED.get();
    }

}
