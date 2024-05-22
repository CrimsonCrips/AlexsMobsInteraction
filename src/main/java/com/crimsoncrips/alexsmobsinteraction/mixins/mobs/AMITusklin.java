package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.enchantment.AMIEnchantmentRegistry;
import com.crimsoncrips.alexsmobsinteraction.goal.AvoidBlockGoal;
import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.EntityLaviathan;
import com.github.alexthe666.alexsmobs.entity.EntityTerrapin;
import com.github.alexthe666.alexsmobs.entity.EntityTusklin;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Iterator;

import static com.github.alexthe666.alexsmobs.entity.EntityTusklin.*;


@Mixin(EntityTusklin.class)
public abstract class AMITusklin extends Mob {

    @Shadow @Nullable public abstract LivingEntity getControllingPassenger();

    private int ridingTime = 0;
    private int entityToLaunchId = -1;
    private int conversionTime = 0;




    static{
        CANTRAMPLE = SynchedEntityData.defineId(EntityTusklin.class, EntityDataSerializers.BOOLEAN);
        PERMTRUSTED = SynchedEntityData.defineId(EntityTusklin.class, EntityDataSerializers.BOOLEAN);
    }

    private static final EntityDataAccessor<Boolean> CANTRAMPLE;
    private static final EntityDataAccessor<Boolean> PERMTRUSTED;

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void defineSynched(CallbackInfo ci){
        this.entityData.define(CANTRAMPLE, false);
        this.entityData.define(PERMTRUSTED, false);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void addAdditional(CompoundTag compound, CallbackInfo ci){
        compound.putBoolean("CanTrample", this.isTusklinTrample());
        compound.putBoolean("PermTrusted", this.isPermTrusted());
    }
    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readAdditional(CompoundTag compound, CallbackInfo ci){
        this.setTusklinTrample(compound.getBoolean("CanTrample"));
        this.setPermTrusted(compound.getBoolean("PermTrusted"));

    }

    public boolean isTusklinTrample() {
        return this.entityData.get(CANTRAMPLE);
    }

    public void setTusklinTrample(boolean tusklinTrample) {
        this.entityData.set(CANTRAMPLE, tusklinTrample);
    }

    public boolean isPermTrusted() {
        return this.entityData.get(PERMTRUSTED);
    }

    public void setPermTrusted(boolean permTrusted) {
        this.entityData.set(PERMTRUSTED, permTrusted);
    }

    protected AMITusklin(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    public boolean hurt(DamageSource source, float amount) {
        boolean prev = super.hurt(source, amount);
        if (source.getDirectEntity() instanceof Player) {
            setPermTrusted(false);
        }

        return prev;
    }

    private boolean isAngryAt(Object p_21675_) {
        return this.canAttack((LivingEntity) p_21675_);
    }
    public boolean canAttack(LivingEntity entity) {
        EntityTusklin tusklin = (EntityTusklin)(Object)this;
        boolean prev = super.canAttack(entity);
        return !(entity instanceof Player) ||  this.getLastHurtByMob() != null && this.getLastHurtByMob().equals(entity) || (tusklin.getPassiveTicks() <= 0 && !isPermTrusted()) && !this.isMushroom(entity.getItemInHand(InteractionHand.MAIN_HAND)) && !tusklin.isMushroom(entity.getItemInHand(InteractionHand.OFF_HAND)) ? prev : false;
    }
    public boolean isMushroom(ItemStack stack) {
        return stack.is(Items.BROWN_MUSHROOM) || stack.is(Items.MUSHROOM_STEW);
    }

    public void tick() {
        EntityTusklin tusklin = (EntityTusklin)(Object)this;
        super.tick();
        if (tusklin.isInNether()) {
            ++this.conversionTime;
            if (this.conversionTime > 300 && !this.level().isClientSide) {
                Hoglin hoglin = (Hoglin)this.convertTo(EntityType.HOGLIN, false);
                if (hoglin != null) {
                    hoglin.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
                    this.dropEquipment();
                    this.level().addFreshEntity(hoglin);
                    this.remove(RemovalReason.DISCARDED);
                }
            }
        }
        Iterator var4 = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().expandTowards(0.2,-2,0.2)).iterator();

        if(AMInteractionConfig.TUSKLIN_TRAMPLE_ENABLED && this.isVehicle() && isTusklinTrample()){
            while (var4.hasNext()) {
                Entity entity = (Entity) var4.next();
                if (entity != this && entity != this.getControllingPassenger() && entity.getBbHeight() <= 2.1F && this.isVehicle()) {
                    entity.hurt(this.damageSources().mobAttack((LivingEntity) this), 3.0F + this.random.nextFloat() * 2.0F);
                    if (entity.onGround()) {
                        double d0 = entity.getX() - this.getX();
                        double d1 = entity.getZ() - this.getZ();
                        double d2 = Math.max(d0 * d0 + d1 * d1, 0.001);
                        float f = 0.5F;
                        entity.push(d0 / d2 * (double)f, (double)f, d1 / d2 * (double)f);
                    }
                }


            }
        }

        if (this.entityToLaunchId != -1 && this.isAlive()) {
            Entity launch = this.level().getEntity(this.entityToLaunchId);
            this.ejectPassengers();
            this.entityToLaunchId = -1;
            if (launch != null && !launch.isPassenger() && launch instanceof LivingEntity) {
                launch.setPos(this.getEyePosition().add(0.0, 1.0, 0.0));
                float rot = 180.0F + this.getYRot();
                float strength = (float)((double)this.getLaunchStrength() * (1.0 - ((LivingEntity)launch).getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)));
                float x = Mth.sin(rot * 0.017453292F);
                float z = -Mth.cos(rot * 0.017453292F);
                if (!((double)strength <= 0.0)) {
                    launch.hasImpulse = true;
                    Vec3 vec3 = this.getDeltaMovement();
                    Vec3 vec31 = vec3.add((new Vec3((double)x, 0.0, (double)z)).normalize().scale((double)strength));
                    launch.setDeltaMovement(vec31.x, (double)strength, vec31.z);
                }
            }
        }

        if (tusklin.getAnimation() == ANIMATION_BUCK && tusklin.getAnimationTick() >= 5) {
            Entity passenger = this.getControllingPassenger();
            if (passenger instanceof LivingEntity) {
                this.entityToLaunchId = passenger.getId();
            }
        }

        if (!this.level().isClientSide) {
            if (AMInteractionConfig.TUSKLIN_TRUST_ENABLED) {
                if (this.isVehicle() && !isPermTrusted()) {
                    ++this.ridingTime;
                    if (this.ridingTime >= this.getMaxRidingTime() && tusklin.getAnimation() != ANIMATION_BUCK) {
                        tusklin.setAnimation(ANIMATION_BUCK);
                    }
                } else {
                    this.ridingTime = 0;
                }
            } else if (this.isVehicle()) {
                ++this.ridingTime;
                if (this.ridingTime >= this.getMaxRidingTime() && tusklin.getAnimation() != ANIMATION_BUCK) {
                    tusklin.setAnimation(ANIMATION_BUCK);
                }
            } else {
                this.ridingTime = 0;
            }

            if (this.getTarget() != null && this.hasLineOfSight(this.getTarget()) && this.distanceTo(this.getTarget()) < this.getTarget().getBbWidth() + this.getBbWidth() + 1.8F) {
                if (tusklin.getAnimation() == ANIMATION_FLING && tusklin.getAnimationTick() == 6) {
                    this.getTarget().hurt(this.damageSources().mobAttack(this), (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                    this.knockbackTarget(this.getTarget(), 0.9F, 0.0F);
                }

                if (tusklin.getAnimation() == ANIMATION_GORE_L && tusklin.getAnimationTick() == 6) {
                    this.getTarget().hurt(this.damageSources().mobAttack(this), (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                    this.knockbackTarget(this.getTarget(), 0.5F, -90.0F);
                }

                if (tusklin.getAnimation() == ANIMATION_GORE_R && tusklin.getAnimationTick() == 6) {
                    this.getTarget().hurt(this.damageSources().mobAttack(this), (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                    this.knockbackTarget(this.getTarget(), 0.5F, 90.0F);
                }
            }
        }

        if (tusklin.getAnimation() == ANIMATION_RUT && tusklin.getAnimationTick() == 23 && this.level().getBlockState(this.blockPosition().below()).is(Blocks.GRASS_BLOCK) && this.getRandom().nextInt(3) == 0) {
            if (this.isBaby() && this.level().getBlockState(this.blockPosition()).canBeReplaced() && this.random.nextInt(3) == 0) {
                this.level().setBlockAndUpdate(this.blockPosition(), Blocks.BROWN_MUSHROOM.defaultBlockState());
                this.gameEvent(GameEvent.BLOCK_DESTROY);
                this.playSound(SoundEvents.CROP_PLANTED, this.getSoundVolume(), this.getVoicePitch());
            }

            this.level().levelEvent(2001, this.blockPosition().below(), Block.getId(Blocks.GRASS_BLOCK.defaultBlockState()));
            this.level().setBlock(this.blockPosition().below(), Blocks.DIRT.defaultBlockState(), 2);
            this.heal(5.0F);
        }

        if (!this.level().isClientSide && tusklin.getAnimation() == NO_ANIMATION && this.getRandom().nextInt(this.isBaby() ? 140 : 70) == 0 && (this.getLastHurtByMob() == null || this.distanceTo(this.getLastHurtByMob()) > 30.0F) && this.level().getBlockState(this.blockPosition().below()).is(Blocks.GRASS_BLOCK) && this.getRandom().nextInt(3) == 0) {
            tusklin.setAnimation(ANIMATION_RUT);
        }

        AnimationHandler.INSTANCE.updateAnimations(tusklin);
    }

    private float getLaunchStrength() {
        EntityTusklin tusklin = (EntityTusklin)(Object)this;
        return tusklin.getShoeStack().is((Item)AMItemRegistry.PIGSHOES.get()) ? 0.4F : 0.9F;
    }

    private int getMaxRidingTime() {
        EntityTusklin tusklin = (EntityTusklin)(Object)this;
        return tusklin.getShoeStack().is((Item)AMItemRegistry.PIGSHOES.get()) ? 160 : 60;
    }

    private void knockbackTarget(LivingEntity entity, float strength, float angle) {
        float rot = this.getYRot() + angle;
        if (entity != null) {
            entity.knockback((double)strength, (double)Mth.sin(rot * 0.017453292F), (double)(-Mth.cos(rot * 0.017453292F)));
        }

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
        if (AMInteractionConfig.TUSKLIN_TRUST_ENABLED && itemstack.getEnchantmentLevel(AMIEnchantmentRegistry.TRAMPLE.get()) > 0) {
            setTusklinTrample(true);
        }
        if (AMInteractionConfig.REMOVE_TUSKLIN_EQUIPMENT_ENABLED && itemstack.getItem() == Items.SHEARS) {
            setTusklinTrample(false);
            this.playSound(SoundEvents.SHEEP_SHEAR, this.getSoundVolume(), this.getVoicePitch());
            this.dropEquipment();
        }
    }

}
