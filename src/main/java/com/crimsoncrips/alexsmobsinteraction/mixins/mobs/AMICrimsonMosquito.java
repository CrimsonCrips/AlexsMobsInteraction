package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(EntityCrimsonMosquito.class)
public abstract class AMICrimsonMosquito extends Mob {

    @Shadow public abstract int getBloodLevel();

    @Shadow public abstract void setSick(boolean shrink);

    private static final EntityDataAccessor<Boolean> TRANFORMING = SynchedEntityData.defineId(EntityCrimsonMosquito.class, EntityDataSerializers.BOOLEAN);

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void defineSynched(CallbackInfo ci){
        this.entityData.define(TRANFORMING, false);
    }

    protected AMICrimsonMosquito(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void AlexInteraction$tick(CallbackInfo ci) {

        Entity attach = this.getVehicle();

        if ((AMInteractionConfig.GOOFY_MODE_ENABLED && AMInteractionConfig.GOOFY_CRIMSON_MULTIPLY_ENABLED &&  attach != null && this.getBloodLevel() > 1)) {
            if (!(attach instanceof Player)){
                EntityCrimsonMosquito crimsonMosquito1 = AMEntityRegistry.CRIMSON_MOSQUITO.get().create(level());
                if (crimsonMosquito1 == null)
                    return;
                crimsonMosquito1.copyPosition(this);
                if (!this.level().isClientSide) {
                    crimsonMosquito1.finalizeSpawn((ServerLevelAccessor) level(), level().getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.CONVERSION, null, null);
                }

                if (!this.level().isClientSide) {
                    this.level().broadcastEntityEvent(this, (byte) 79);
                    level().addFreshEntity(crimsonMosquito1);
                }
                attach.remove(RemovalReason.DISCARDED);
            }

        }

    }


    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        InteractionResult type = super.mobInteract(player, hand);
        if (AMInteractionConfig.FROG_TRANSFORM_ENABLED) {
            if (itemstack.getItem() == AMItemRegistry.WARPED_MUSCLE.get() && this.hasEffect(MobEffects.WEAKNESS)){
                if (!player.isCreative()) {
                    itemstack.shrink(1);
                }
                gameEvent(GameEvent.ENTITY_INTERACT);
                this.gameEvent(GameEvent.EAT);
                this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), this.getVoicePitch());
                this.setSick(true);
                return InteractionResult.SUCCESS;
            }
        } return type;
    }

    @Inject(method = "mobInteract", at = @At("HEAD"))
    private void mobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (AMInteractionConfig.CRIMSON_TRANSFORM_ENABLED) {
            if (itemstack.getItem() == AMItemRegistry.WARPED_MUSCLE.get() && this.hasEffect(MobEffects.WEAKNESS)){
                if (!player.isCreative()) {
                    itemstack.shrink(1);
                }
                gameEvent(GameEvent.ENTITY_INTERACT);
                this.gameEvent(GameEvent.EAT);
                this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), this.getVoicePitch());
                this.setSick(true);
            }
        }
    }


}
