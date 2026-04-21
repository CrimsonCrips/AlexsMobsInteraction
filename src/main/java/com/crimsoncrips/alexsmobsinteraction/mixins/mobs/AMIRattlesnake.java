package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.datagen.tags.AMIEntityTagGenerator;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.AMIBasicInterfaces;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityRattlesnake;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityRattlesnake.class)
public abstract class AMIRattlesnake extends Animal implements AMIBasicInterfaces {

    protected AMIRattlesnake(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }


    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        EntityRattlesnake rattlesnake = (EntityRattlesnake)(Object)this;
        if (AlexsMobsInteraction.TARGETS_CONFIG.RATTLESNAKE_ENABLED.get()) {
            rattlesnake.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(rattlesnake, LivingEntity.class, 300, true, true, AMEntityRegistry.buildPredicateFromTag(AMIEntityTagGenerator.WEAK_PREY)) {
                public void start() {
                    super.start();
                }
            });
        }
        if (AlexsMobsInteraction.TARGETS_CONFIG.CANNIBALISM_ENABLED.get()) {
            rattlesnake.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(rattlesnake, EntityRattlesnake.class, 1500, true, true, (livingEntity) -> {
                return livingEntity.getHealth() <= 0.60F * livingEntity.getMaxHealth() || livingEntity.isBaby();
            }));
        }


    }

    private static final EntityDataAccessor<Boolean> WARDING = SynchedEntityData.defineId(EntityRattlesnake.class, EntityDataSerializers.BOOLEAN);

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void defineSynched(CallbackInfo ci){
        this.entityData.define(WARDING, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putBoolean("Warding", this.isWarding());
        super.addAdditionalSaveData(pCompound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        this.setWarding(pCompound.getBoolean("Warding"));
        super.readAdditionalSaveData(pCompound);
    }


    public void setWarding(boolean val) {
        this.entityData.set(WARDING, val);
    }

    @Override
    public boolean isWarding() {
        return this.entityData.get(WARDING);
    }


}
