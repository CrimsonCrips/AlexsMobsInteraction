package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.lavithan;

import com.crimsoncrips.alexsmobsinteraction.mobmodification.interfaces.AMILavithanInterface;
import com.crimsoncrips.alexsmobsinteraction.AMInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.AMIReflectionUtil;
import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(EntityLaviathan.class)
public abstract class AMILavithan extends Animal implements ISemiAquatic, IHerdPanic , AMILavithanInterface {


    @Shadow protected abstract float getXForPart(float yaw, float degree);

    @Shadow protected abstract float getZForPart(float yaw, float degree);

    protected AMILavithan(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    static{
        RELAVA = SynchedEntityData.defineId(EntityLaviathan.class, EntityDataSerializers.BOOLEAN);
    }

    private static final EntityDataAccessor<Boolean> RELAVA;

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void defineSynched(CallbackInfo ci){
        this.entityData.define(RELAVA, false);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void addAdditional(CompoundTag compound, CallbackInfo ci){
        compound.putBoolean("Relava", this.isRelava());
    }
    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readAdditional(CompoundTag compound, CallbackInfo ci){
        this.setRelava(compound.getBoolean("Relava"));

    }

    public boolean isRelava() {
        return this.entityData.get(RELAVA);
    }

    public void setRelava(boolean relava) {
        this.entityData.set(RELAVA, relava);
    }


    @Inject(method = "mobInteract", at = @At("TAIL"))
    private void mobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir){
        EntityLaviathan laviathan = (EntityLaviathan)(Object)this;
        if (player.getMainHandItem().is(AMInteractionTagRegistry.LAVITHAN_PICKAXES) && laviathan.isObsidian()){
            setRelava(true);
            laviathan.setObsidian(false);
            this.playSound(SoundEvents.BASALT_BREAK, this.getSoundVolume(), this.getVoicePitch());
            for (int i = 0; i < (isBaby() ? 6 : 2);i++){
                this.spawnAtLocation(Items.OBSIDIAN);
            }
            player.getMainHandItem().hurtAndBreak(1, this, (p_233654_0_) -> {});
            this.hurt(laviathan.damageSources().generic(),20);
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci){
        EntityLaviathan laviathan = (EntityLaviathan)(Object)this;
        if (!laviathan.isObsidian() && !this.isInWaterOrBubble() && isRelava() && AMInteractionConfig.OBSIDIAN_EXTRACT_ENABLED) {
            if (getPersistentData().getInt("RelavaTicks") < 6000) {
                getPersistentData().putInt("RelavaTicks",getPersistentData().getInt("RelavaTicks") + 1);
            } else {
                setRelava(false);
            }
        }

        if (this.level().isClientSide && this.isRelava() && AMInteractionConfig.OBSIDIAN_EXTRACT_ENABLED) {
            float yaw = laviathan.getYRot() * 0.017453292F;
            if (!this.isBaby() ) {
                if (random.nextDouble() < 0.1) {
                    this.level().addParticle(ParticleTypes.LAVA, this.getX() + getXForPart(yaw, 158) * 1.75F, this.getY(1), this.getZ() + getZForPart(yaw, 158) * 1.75F, 0.0D, this.random.nextDouble() / 5.0D, 0.0D);
                    this.level().addParticle(ParticleTypes.LAVA, this.getX() + getXForPart(yaw, -166) * 1.48F, this.getY(1), this.getZ() + getZForPart(yaw, -166) * 1.48F, 0.0D, this.random.nextDouble() / 5.0D, 0.0D);
                    this.level().addParticle(ParticleTypes.LAVA, this.getX() + getXForPart(yaw, 14) * 1.78F, this.getY(0.9), this.getZ() + getZForPart(yaw, 14) * 1.78F, 0.0D, this.random.nextDouble() / 5.0D, 0.0D);
                    this.level().addParticle(ParticleTypes.LAVA, this.getX() + getXForPart(yaw, -14) * 1.6F, this.getY(1.1), this.getZ() + getZForPart(yaw, -14) * 1.6F, 0.0D, this.random.nextDouble() / 5.0D, 0.0D);
                }
            }

        }


    }


    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/alexsmobs/entity/EntityLaviathan;isObsidian()Z",ordinal = 1), cancellable = true)
    private void alter1(CallbackInfo ci) {
        if (AMInteractionConfig.OBSIDIAN_EXTRACT_ENABLED && this.isRelava()){
            ci.cancel();
        }
    }


}
