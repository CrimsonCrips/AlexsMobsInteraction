package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.leafcutter;

import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.AMIVariant;
import com.github.alexthe666.alexsmobs.entity.EntityLeafcutterAnt;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(EntityLeafcutterAnt.class)
public abstract class AMILeafcutterAntMixin extends Animal implements AMIVariant {

    protected AMILeafcutterAntMixin(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(EntityLeafcutterAnt.class, EntityDataSerializers.INT);

    public int getVariant() {
        return this.entityData.get(VARIANT);
    }

    public void setVariant(int variant) {
        this.entityData.set(VARIANT, Integer.valueOf(variant));
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        EntityLeafcutterAnt leafcutterAnt = (EntityLeafcutterAnt)(Object)this;
        if (AMInteractionConfig.LEAFCUTTER_VARIANTS_ENABLED) {
            leafcutterAnt.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(this, EntityLeafcutterAnt.class, 100, false, true, livingEntity ->  {
                return ((AMIVariant) livingEntity).getVariant() != getVariant();
            }){
                public boolean canUse() {
                    return super.canUse() && !leafcutterAnt.hasLeaf();
                }
            });
        }
    }


    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void define(CallbackInfo ci) {
        this.entityData.define(VARIANT, 0);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void add(CompoundTag compound, CallbackInfo ci) {
        compound.putInt("Variant", this.getVariant());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void read(CompoundTag compound, CallbackInfo ci) {
        this.setVariant(compound.getInt("Variant"));
    }

    @Inject(method = "finalizeSpawn", at = @At("HEAD"))
    private void finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, SpawnGroupData spawnDataIn, CompoundTag dataTag, CallbackInfoReturnable<SpawnGroupData> cir) {
        if (AMInteractionConfig.LEAFCUTTER_VARIANTS_ENABLED) {
            this.setVariant(random.nextBoolean() ? 1 : 2);
        } else this.setVariant(1);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void finalizeSpawn(CallbackInfo ci) {
        if (!AMInteractionConfig.LEAFCUTTER_VARIANTS_ENABLED && getVariant() == 2) {
            this.setVariant(1);
        }
    }


}
