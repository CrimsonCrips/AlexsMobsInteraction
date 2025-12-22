package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.compat.CuriosCompat;
import com.crimsoncrips.alexsmobsinteraction.datagen.tags.AMIEntityTagGenerator;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.AMIBaseInterfaces;
import com.crimsoncrips.alexsmobsinteraction.server.goal.AMIWarnPredator;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityRattlesnake;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(EntityRattlesnake.class)
public abstract class AMIRattlesnake extends Animal implements AMIBaseInterfaces {

    protected AMIRattlesnake(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }


    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        EntityRattlesnake rattlesnake = (EntityRattlesnake)(Object)this;
        if (AlexsMobsInteraction.COMMON_CONFIG.ADD_TARGETS_ENABLED.get()) {
            rattlesnake.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(rattlesnake, LivingEntity.class, 300, true, true, AMEntityRegistry.buildPredicateFromTag(AMIEntityTagGenerator.WEAK_PREY)) {
                public void start() {
                    super.start();
                }
            });
        }
        if (AlexsMobsInteraction.COMMON_CONFIG.CANNIBALIZATION_ENABLED.get()) {
            rattlesnake.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(rattlesnake, EntityRattlesnake.class, 1500, true, true, (livingEntity) -> {
                return livingEntity.getHealth() <= 0.60F * livingEntity.getMaxHealth() || livingEntity.isBaby();
            }));
        }

        if (AlexsMobsInteraction.COMMON_CONFIG.RATTLESNAKE_TERRITORIAL_ENABLED.get()) {
            rattlesnake.goalSelector.addGoal(2, new AMIWarnPredator(rattlesnake));

            rattlesnake.goalSelector.addGoal(1, new AvoidEntityGoal<>(rattlesnake, EntityRattlesnake.class, 5.0F, 1.2, 1.5, (livingEntity) -> {
                return livingEntity instanceof EntityRattlesnake rattle && ((AMIBaseInterfaces)rattle).isWarding() && rattle.isRattling();
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
