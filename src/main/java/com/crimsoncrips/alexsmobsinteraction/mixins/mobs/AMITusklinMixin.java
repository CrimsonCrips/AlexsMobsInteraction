package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.server.goal.AvoidBlockGoal;
import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;


@Mixin(EntityTusklin.class)
public abstract class AMITusklinMixin extends Mob {

    @Shadow @Nullable public abstract LivingEntity getControllingPassenger();

    static{
        PERMTRUSTED = SynchedEntityData.defineId(EntityTusklin.class, EntityDataSerializers.BOOLEAN);
    }

    private static final EntityDataAccessor<Boolean> PERMTRUSTED;

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void defineSynched(CallbackInfo ci){
        this.entityData.define(PERMTRUSTED, false);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void addAdditional(CompoundTag compound, CallbackInfo ci){
        compound.putBoolean("PermTrusted", this.isPermTrusted());
    }
    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readAdditional(CompoundTag compound, CallbackInfo ci){
        this.setPermTrusted(compound.getBoolean("PermTrusted"));

    }

    public boolean isPermTrusted() {
        return this.entityData.get(PERMTRUSTED);
    }

    public void setPermTrusted(boolean permTrusted) {
        this.entityData.set(PERMTRUSTED, permTrusted);
    }

    protected AMITusklinMixin(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    public boolean hurt(DamageSource source, float amount) {
        boolean prev = super.hurt(source, amount);
        if (source.getDirectEntity() instanceof Player) {
            setPermTrusted(false);
        }
        return prev;
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/alexsmobs/entity/EntityTusklin;isVehicle()Z"))
    private void readAdditional(CallbackInfo ci){
        isPermTrusted();
    }

    @Inject(method = "mobInteract", at = @At("HEAD"))
    private void mobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir){
        ItemStack itemstack = player.getItemInHand(hand);
        if (AMInteractionConfig.TUSKLIN_TRUST_ENABLED && itemstack.getItem() == Items.MUSHROOM_STEW) {
            itemstack.shrink(1);
            this.gameEvent(GameEvent.EAT);
            this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), this.getVoicePitch());
            setPermTrusted(true);
        }
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        EntityTusklin tusklin = (EntityTusklin)(Object)this;
        if(AMInteractionConfig.TUSKLIN_FLEE_ENABLED){
            tusklin.goalSelector.addGoal(3, new AvoidBlockGoal(tusklin, 4, 1, 1.2, (pos) -> {
                BlockState state = tusklin.level().getBlockState(pos);
                return state.is(BlockTags.HOGLIN_REPELLENTS);
            }));
        }
    }

}
