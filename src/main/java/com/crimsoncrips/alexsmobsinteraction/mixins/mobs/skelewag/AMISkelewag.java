package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.skelewag;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.datagen.loottables.AMILootTables;
import com.crimsoncrips.alexsmobsinteraction.server.effect.AMIEffects;
import com.crimsoncrips.alexsmobsinteraction.server.enchantment.AMIEnchantmentRegistry;
import com.crimsoncrips.alexsmobsinteraction.server.goal.AMISkelewagCircleGoal;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.citadel.config.biome.SpawnBiomeData;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Debug(export = true)
@Mixin(EntitySkelewag.class)
public abstract class AMISkelewag extends Monster {

    protected AMISkelewag(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Shadow public abstract int getVariant();

    @Inject(method = "<init>", at = @At("TAIL"))
    private void alexsMobsInteraction$init(EntityType monster, Level level, CallbackInfo ci) {
        this.setPathfindingMalus(BlockPathTypes.LAVA, 0.0F);
    }

    @Override
    protected ResourceLocation getDefaultLootTable() {
        return getVariant() == 2 || getVariant() == 3 ? AMILootTables.WITHERED_SKELEWAG : super.getDefaultLootTable();
    }

    @Override
    public boolean fireImmune() {
        return getVariant() == 3 || getVariant() == 2;
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        return level().getFluidState(pos).is(FluidTags.WATER) || (level().getFluidState(pos).is(FluidTags.LAVA) && AlexsMobsInteraction.COMMON_CONFIG.WITHERED_SKELEWAG_ENABLED.get()) ? 10.0F + level.getLightLevelDependentMagicValue(pos) - 0.5F : super.getWalkTargetValue(pos, level());
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        EntitySkelewag skelewag = (EntitySkelewag)(Object)this;
        FluidState fluidState = worldIn.getFluidState(skelewag.getOnPos().below());

        skelewag.setVariant(fluidState.is(FluidTags.LAVA) && AlexsMobsInteraction.COMMON_CONFIG.WITHERED_SKELEWAG_ENABLED.get() ? (getRandom().nextFloat() < 0.3F ? 3 : 2) : getRandom().nextFloat() < 0.3F ? 1 : 0);

        if (this.random.nextFloat() < 0.2F && fluidState.is(FluidTags.WATER)) {
            Drowned drowned = (Drowned)EntityType.DROWNED.create(this.level());
            drowned.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
            drowned.copyPosition(this);
            drowned.startRiding(this);
            worldIn.addFreshEntityWithPassengers(drowned);
        }


        if (fluidState.is(FluidTags.LAVA) && AlexsMobsInteraction.COMMON_CONFIG.WITHERED_SKELEWAG_ENABLED.get()) {
            if (this.random.nextFloat() < 0.4F){
                ZombifiedPiglin zombifiedPiglin = EntityType.ZOMBIFIED_PIGLIN.create(this.level());
                zombifiedPiglin.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
                zombifiedPiglin.copyPosition(this);
                zombifiedPiglin.startRiding(this);
                worldIn.addFreshEntityWithPassengers(zombifiedPiglin);
            } else if (this.random.nextFloat() < 0.1F) {
                WitherSkeleton witherSkeleton = EntityType.WITHER_SKELETON.create(this.level());
                witherSkeleton.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
                witherSkeleton.copyPosition(this);
                witherSkeleton.startRiding(this);
                worldIn.addFreshEntityWithPassengers(witherSkeleton);
            }
        }

        if (reason == MobSpawnType.STRUCTURE) {
            this.restrictTo(this.blockPosition(), 15);
        }

        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void alexsMobsInteraction$registerGoals(CallbackInfo ci) {
        EntitySkelewag skelewag = (EntitySkelewag)(Object)this;
        if (AlexsMobsInteraction.COMMON_CONFIG.SKELEWAG_CIRCLE_ENABLED.get()){
            skelewag.goalSelector.addGoal(1, new AMISkelewagCircleGoal(skelewag,1F));
        }
        if (AlexsMobsInteraction.COMMON_CONFIG.MIGHT_UPGRADE_ENABLED.get()){
            skelewag.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(skelewag, Player.class, 100, true, false, (livingEntity) -> {
                return !livingEntity.hasEffect(AMEffectRegistry.ORCAS_MIGHT.get());
            }));
        }
    }

    @Inject(method = "canSkelewagSpawn", at = @At("HEAD"),remap = false, cancellable = true)
    private static void alexsMobsInteraction$canSkelewagSpawn(EntityType<EntitySkelewag> type, ServerLevelAccessor levelAccessor, MobSpawnType p_32352_, BlockPos below, RandomSource random, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(levelAccessor.getDifficulty() != Difficulty.PEACEFUL && (p_32352_ == MobSpawnType.SPAWNER || (random.nextInt(40) == 0 && (levelAccessor.getFluidState(below).is(FluidTags.WATER) && isDarkEnoughToSpawn(levelAccessor, below, random)) || (levelAccessor.getFluidState(below).is(FluidTags.LAVA) && AlexsMobsInteraction.COMMON_CONFIG.WITHERED_SKELEWAG_ENABLED.get() && random.nextInt(40  * 4) == 0))));
    }

    @WrapWithCondition(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V",ordinal = 1))
    private boolean alexsMobsInteraction$attackGoal(GoalSelector instance, int pPriority, Goal pGoal) {
        return !AlexsMobsInteraction.COMMON_CONFIG.SKELEWAG_CIRCLE_ENABLED.get();
    }

    @WrapWithCondition(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V",ordinal = 4))
    private boolean alexsMobsInteraction$targetPlayer(GoalSelector instance, int pPriority, Goal pGoal) {
        return !AlexsMobsInteraction.COMMON_CONFIG.MIGHT_UPGRADE_ENABLED.get();
    }

    @WrapWithCondition(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V",ordinal = 5))
    private boolean alexsMobsInteraction$targetDolphin(GoalSelector instance, int pPriority, Goal pGoal) {
        return !AlexsMobsInteraction.COMMON_CONFIG.MIGHT_UPGRADE_ENABLED.get();
    }

    @WrapOperation(method = "travel", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/alexsmobs/entity/EntitySkelewag;isInWater()Z"),remap = false)
    private boolean alexsMobsInteraction$travel(EntitySkelewag instance, Operation<Boolean> original){
        if (AlexsMobsInteraction.COMMON_CONFIG.WITHERED_SKELEWAG_ENABLED.get()){
            return original.call(instance) || this.isInLava();
        }
        return original.call(instance);
    }

    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/alexsmobs/entity/EntitySkelewag;isInWaterOrBubble()Z",ordinal = 0),remap = false)
    private boolean alexsMobsInteraction$tick(boolean original){
        if (AlexsMobsInteraction.COMMON_CONFIG.WITHERED_SKELEWAG_ENABLED.get()){
            return original || this.isInLava();
        }
        return original;
    }


    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/alexsmobs/entity/EntitySkelewag;isInWaterOrBubble()Z",ordinal = 1),remap = false)
    private boolean alexsMobsInteraction$tick1(boolean original){
        if (AlexsMobsInteraction.COMMON_CONFIG.WITHERED_SKELEWAG_ENABLED.get()){
            return original || this.isInLava();
        }
        return original;
    }


}
