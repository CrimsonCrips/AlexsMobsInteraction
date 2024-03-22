package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.vanillamob;

import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.crimsoncrips.alexsmobsinteraction.mobmodification.interfaces.AITransform;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Frog.class)
public class AIFrog extends Mob implements AITransform {
    int frogWarped;

    static {
        FROGSICK = SynchedEntityData.defineId(Frog.class, EntityDataSerializers.BOOLEAN);
        MAGGOTFED = SynchedEntityData.defineId(Frog.class, EntityDataSerializers.INT);
        MUNGUSFED = SynchedEntityData.defineId(Frog.class, EntityDataSerializers.INT);
        WARPEDFED = SynchedEntityData.defineId(Frog.class, EntityDataSerializers.INT);
    }

    private static final EntityDataAccessor<Boolean> FROGSICK;
    private static final EntityDataAccessor<Integer> MAGGOTFED;
    private static final EntityDataAccessor<Integer> MUNGUSFED;
    private static final EntityDataAccessor<Integer> WARPEDFED;

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void defineSynched(CallbackInfo ci){
        this.entityData.define(FROGSICK, false);
        this.entityData.define(MAGGOTFED, 0);
        this.entityData.define(MUNGUSFED, 0);
        this.entityData.define(WARPEDFED, 0);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void addAdditional(CompoundTag compound, CallbackInfo ci){
        compound.putBoolean("FrogSick", this.isTransforming());
        compound.putInt("MaggotFed", this.getMaggotFed());
        compound.putInt("MungusFed", this.getMungusFed());
        compound.putInt("WarpedFed", this.getWarpedFed());
    }
    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readAdditional(CompoundTag compound, CallbackInfo ci){
        this.setTransforming(compound.getBoolean("FrogSick"));
        this.setMaggotFed(compound.getInt("MaggotFed"));
        this.setMungusFed(compound.getInt("MungusFed"));
        this.setWarpedFed(compound.getInt("WarpedFed"));
    }

    public int getMaggotFed() {
        return (Integer)this.entityData.get(MAGGOTFED);
    }

    public void setMaggotFed(int maggot) {
        this.entityData.set(MAGGOTFED, maggot);
    }
    public int getMungusFed() {
        return (Integer)this.entityData.get(MUNGUSFED);
    }

    public void setMungusFed(int mungus) {
        this.entityData.set(MUNGUSFED, mungus);
    }
    public int getWarpedFed() {
        return (Integer)this.entityData.get(WARPEDFED);
    }

    public void setWarpedFed(int warped) {
        this.entityData.set(WARPEDFED, warped);
    }



    protected AIFrog(EntityType<? extends Mob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }


    @Inject(method = "tick", at = @At("HEAD"))
    private void AlexInteraction$tick(CallbackInfo ci) {
        Frog frog = (Frog)(Object)this;
            if (this.getMungusFed() >= 1 && this.getWarpedFed() >= 2 && this.getMaggotFed() >= 10)  setTransforming(true);
            if (isTransforming()){
                frogWarped++;
                if (frogWarped > 160) {
                    EntityWarpedToad warpedToad = AMEntityRegistry.WARPED_TOAD.get().create(level());
                    warpedToad.copyPosition(this);
                    if (!this.level().isClientSide) {
                        warpedToad.finalizeSpawn((ServerLevelAccessor) level(), level().getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.CONVERSION, null, null);
                    }
                    frog.playSound(SoundEvents.ZOMBIE_VILLAGER_CONVERTED);

                    if (!this.level().isClientSide) {
                        this.level().broadcastEntityEvent(this, (byte) 79);
                        level().addFreshEntity(warpedToad);
                    }
                    frog.remove(RemovalReason.DISCARDED);

                }
            }
    }
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        InteractionResult type = super.mobInteract(player, hand);
        if(AInteractionConfig.frogtransform){
            if (stack.getItem() == AMItemRegistry.MAGGOT.get() && !(getMaggotFed() >= 10)) {

                gameEvent(GameEvent.ENTITY_INTERACT);
                stack.shrink(1);
                this.playSound(SoundEvents.GENERIC_EAT);
                this.setMaggotFed(this.getMaggotFed() + 1);
                return InteractionResult.SUCCESS;
            }
            if (stack.getItem() == AMItemRegistry.MUNGAL_SPORES.get() && !(getMungusFed() >= 1)) {
                gameEvent(GameEvent.ENTITY_INTERACT);
                stack.shrink(1);
                this.playSound(SoundEvents.GENERIC_EAT);
                this.setMungusFed(this.getMungusFed() + 1);
                return InteractionResult.SUCCESS;
            }
            if (stack.getItem() == Items.WARPED_FUNGUS && !(getWarpedFed() >= 2)) {
                gameEvent(GameEvent.ENTITY_INTERACT);
                stack.shrink(1);
                this.playSound(SoundEvents.GENERIC_EAT);
                this.setWarpedFed(this.getWarpedFed() + 1);
                return InteractionResult.SUCCESS;
            }
        } return type;
    }

    @Override
    public boolean isTransforming() {
        return this.entityData.get(FROGSICK);
    }

    @Override
    public void setTransforming(boolean transforming) {
        this.entityData.set(FROGSICK, transforming);
    }


}
