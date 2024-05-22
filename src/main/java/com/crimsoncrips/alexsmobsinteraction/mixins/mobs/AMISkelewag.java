package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.enchantment.AMIEnchantmentRegistry;
import com.crimsoncrips.alexsmobsinteraction.goal.AMISkelewagCircleGoal;
import com.crimsoncrips.alexsmobsinteraction.ReflectionUtil;
import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.crimsoncrips.alexsmobsinteraction.mobmodification.interfaces.AMISkelewagInterface;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIRandomSwimming;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.ForgeEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;


@Mixin(EntitySkelewag.class)
public class AMISkelewag extends Mob implements AMISkelewagInterface {

    protected AMISkelewag(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "registerGoals", at = @At("HEAD"),cancellable = true)
    private void ShoebillGoals(CallbackInfo ci){
        ci.cancel();
        EntitySkelewag skelewag = (EntitySkelewag) (Object) this;
        Predicate<LivingEntity> MIGHT = (livingEntity) -> {
            return !livingEntity.hasEffect(AMEffectRegistry.ORCAS_MIGHT.get());
        };
        this.goalSelector.addGoal(1, new TryFindWaterGoal(skelewag));
        if (AMInteractionConfig.SKELEWAG_CIRCLE_ENABLED){
            this.goalSelector.addGoal(1, new AMISkelewagCircleGoal(skelewag,1F));
        } else {
            Object aiAttackGoal = ReflectionUtil.createInstance(
                    "com.github.alexthe666.alexsmobs.entity.EntitySkelewag$AttackGoal",
                    new Class[]{EntitySkelewag.class, EntitySkelewag.class},
                    new Object[]{skelewag, this}
            );
            this.goalSelector.addGoal(2,(Goal)aiAttackGoal);
        }
        this.goalSelector.addGoal(3, new AnimalAIRandomSwimming(skelewag, 1F, 12, 5));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(skelewag, Drowned.class, EntitySkelewag.class));
        if(AMInteractionConfig.MIGHT_UPGRADE_ENABLED){
            this.targetSelector.addGoal(2, new EntityAINearestTarget3D<>(this, Player.class, 100, true, false, MIGHT));
        }
        this.targetSelector.addGoal(3, new EntityAINearestTarget3D(this, Dolphin.class, true));

    }

    boolean stun = false;

    private static final EntityDataAccessor<Integer> STUNTICK = SynchedEntityData.defineId(EntitySkelewag.class, EntityDataSerializers.INT);

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void defineSynched(CallbackInfo ci){
        this.entityData.define(STUNTICK, 0);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void addAdditional(CompoundTag compound, CallbackInfo ci){
        compound.putInt("StunTicks", this.getStunTicks());
    }
    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readAdditional(CompoundTag compound, CallbackInfo ci){
        this.setStunTicks(compound.getInt("StunTicks"));

    }

    public int getStunTicks() {
        return (Integer)this.entityData.get(STUNTICK);
    }

    public void setStunTicks(int stuntick) {
        this.entityData.set(STUNTICK, stuntick);
    }

    protected boolean isImmobile() {
        return stun;
    }


    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        if (AMInteractionConfig.CHARGE_STUN_ENABLED && AMInteractionConfig.SKELEWAG_CIRCLE_ENABLED) {
            setStunTicks(getStunTicks() - 1);
            LivingEntity target = getTarget();
            if (getStunTicks() > 0 && target != null) {
                setTarget(null);
            }
            if (getStunTicks() < 50) {
                stun = false;
            }

            if (this.getTarget() instanceof Player player && (player.getItemBySlot(EquipmentSlot.OFFHAND).getEnchantmentLevel(AMIEnchantmentRegistry.FINAL_STAND.get()) > 0 || player.getItemBySlot(EquipmentSlot.MAINHAND).getEnchantmentLevel(AMIEnchantmentRegistry.FINAL_STAND.get()) > 0)) {
                if (this.distanceTo(this.getTarget()) < 3F && this.hasLineOfSight(this.getTarget()) && this.getTarget().isBlocking() && !stun) {
                    setStunTicks(100);
                    stun = true;
                    this.playSound(SoundEvents.SHIELD_BLOCK, 2F, 1F);
                    target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 0));
                    target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 150, 1));
                }
            }

        }
    }

}
