package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.portal;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.AMIBaseInterfaces;
import com.github.alexthe666.alexsmobs.entity.EntityLeafcutterAnt;
import com.github.alexthe666.alexsmobs.entity.EntityVoidPortal;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(EntityVoidPortal.class)
public abstract class AMIVoidPortalMixin extends Entity implements AMIBaseInterfaces {

    protected AMIVoidPortalMixin(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(EntityVoidPortal.class, EntityDataSerializers.INT);

    public int getVariant() {
        return this.entityData.get(VARIANT);
    }

    public void setVariant(int variant) {
        this.entityData.set(VARIANT, Integer.valueOf(variant));
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void alexsMobsInteraction$define(CallbackInfo ci) {
        this.entityData.define(VARIANT, 0);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void alexsMobsInteraction$add(CompoundTag compound, CallbackInfo ci) {
        compound.putInt("Variant", this.getVariant());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void alexsMobsInteraction$read(CompoundTag compound, CallbackInfo ci) {
        this.setVariant(compound.getInt("Variant"));
    }

    @Inject(method = "createAndSetSister", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/alexsmobs/entity/EntityVoidPortal;setShattered(Z)V"),remap = false)
    private void alexsMobsInteraction$createAndSetSister(Level world, Direction dir, CallbackInfo ci, @Local EntityVoidPortal sister) {
        ((AMIBaseInterfaces)sister).setVariant(this.getVariant());
        String result = sister.exitDimension.toString().replaceAll("ResourceKey\\[minecraft:dimension / |\\]", "");
        ((AMIBaseInterfaces)sister).setVariant(AMIUtils.dimensionDeterminer(result));
    }


}
