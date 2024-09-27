package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.fly;

import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.crimsoncrips.alexsmobsinteraction.effect.AMIEffects;
import com.crimsoncrips.alexsmobsinteraction.mobmodification.interfaces.AMITransform;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(EntityFly.class)
public class AMIFly extends Mob implements AMITransform {

    int flyConvert;

    boolean transformingBoolean = false;

    protected AMIFly(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "mobInteract", at = @At("HEAD"))
    private void mobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir){
        ItemStack itemstack = player.getItemInHand(hand);
     if (AMInteractionConfig.FLY_CONVERT_ENABLED) {
         if (itemstack.getItem() == AMItemRegistry.BLOOD_SAC.get() && this.hasEffect(AMIEffects.BLOODED.get())){
             transformingBoolean = true;
             this.gameEvent(GameEvent.EAT);
             this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), this.getVoicePitch());
         }
        }

    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void Test(CallbackInfo ci) {
        System.out.println(transformingBoolean);
        if (isTransforming()) {
            flyConvert++;

            if (flyConvert > 160) {
                EntityCrimsonMosquito crimsonMosquito = AMEntityRegistry.CRIMSON_MOSQUITO.get().create(level());
                if (crimsonMosquito == null)
                    return;
                crimsonMosquito.copyPosition(this);
                if (!this.level().isClientSide) {
                    crimsonMosquito.finalizeSpawn((ServerLevelAccessor) level(), level().getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.CONVERSION, null, null);
                }
                crimsonMosquito.onSpawnFromFly();

                if (!this.level().isClientSide) {
                    this.level().broadcastEntityEvent(this, (byte) 79);
                    level().addFreshEntity(crimsonMosquito);
                }

                this.remove(RemovalReason.DISCARDED);

            }
        }

    }




    public boolean isTransforming() {
        return transformingBoolean;
    }

    @Override
    public void setTransforming(boolean transforming) {

    }

}
