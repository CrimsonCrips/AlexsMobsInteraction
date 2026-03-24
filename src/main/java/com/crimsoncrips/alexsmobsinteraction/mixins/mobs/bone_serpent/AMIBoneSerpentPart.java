package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.bone_serpent;

import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.BonePartInterface;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.ChildnParent_Interface;
import com.github.alexthe666.alexsmobs.entity.EntityBoneSerpent;
import com.github.alexthe666.alexsmobs.entity.EntityBoneSerpentPart;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;


@Mixin(EntityBoneSerpentPart.class)
public abstract class AMIBoneSerpentPart extends LivingEntity implements BonePartInterface {

    @Shadow
    public abstract Entity getParent();

    @Shadow
    public abstract void setTail(boolean tail);

    protected AMIBoneSerpentPart(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    private static final EntityDataAccessor<Optional<UUID>> CHILD_UUID = SynchedEntityData.defineId(EntityBoneSerpentPart.class, EntityDataSerializers.OPTIONAL_UUID);

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void alexsMobsInteraction$defineSynchedData(CallbackInfo ci) {
        this.entityData.define(CHILD_UUID, Optional.empty());
    }
    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void alexsMobsInteraction$addAdditionalSaveData(CompoundTag compound, CallbackInfo ci) {
        if (this.getChildUUID() != null) {
            compound.putUUID("ChildUUID", this.getChildUUID());
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void alexsMobsInteraction$readAdditionalSaveData(CompoundTag compound, CallbackInfo ci) {
        if (compound.hasUUID("ChildUUID")) {
            this.setChildUUID(compound.getUUID("ChildUUID"));
        }
    }

    @Nullable
    @Override
    public UUID getChildUUID() {
        return this.entityData.get(CHILD_UUID).orElse(null);
    }

    @Override
    public void setChildUUID(@Nullable UUID uniqueId) {
        this.entityData.set(CHILD_UUID, Optional.ofNullable(uniqueId));
    }

    @Override
    public void detectChildLoop(){
        if(getChild() instanceof EntityBoneSerpentPart entityBoneSerpentPart1){
            if (entityBoneSerpentPart1.isTail()){
                if (this.getParent() instanceof EntityBoneSerpent)
                    return;
                entityBoneSerpentPart1.discard();
                this.setTail(true);
            } else {
                ((BonePartInterface)entityBoneSerpentPart1).detectChildLoop();
            }
        }
    }

    @Override
    public Entity getChild() {
        UUID id = getChildUUID();
        if (id != null && !this.level().isClientSide) {
            return ((ServerLevel) level()).getEntity(id);
        }
        return null;
    }

}
