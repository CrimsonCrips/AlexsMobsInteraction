package com.crimsoncrips.alexsmobsinteraction.mixins;

import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.ai.StraddlerAIShoot;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.github.alexthe666.alexsmobs.entity.EntityStraddler.ANIMATION_LAUNCH;


@Mixin(EntityStraddler.class)
public class AIStraddler extends Mob {

    int shootcooldown = 0;

    int shootshots = AInteractionConfig.straddlershots;


    protected AIStraddler(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "shouldShoot", at = @At("HEAD"), cancellable = true,remap = false)
    private void injected(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(doSomething4());
    }


    private boolean doSomething4() {
        if (AInteractionConfig.straddlershots != 0) {
            if (!(shootshots <= 0)) return true;
            else return false;
        }else {
            return true;
        }
    }

    @Inject(method = "registerGoals", at = @At("HEAD"),cancellable = true)
    private void StraddlerGoals(CallbackInfo ci){
        ci.cancel();
        EntityStraddler straddler = (EntityStraddler)(Object)this;
        this.goalSelector.addGoal(1, new StraddlerAIShoot(straddler, 0.5, 30, 16.0F));
        this.goalSelector.addGoal(7, new RandomStrollGoal(straddler, 1.0, 60));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Strider.class, 8.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(straddler, new Class[0]));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, AbstractVillager.class, true));
        if(AInteractionConfig.stradllervengeance) {
            this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, EntityBoneSerpent.class, true));
            this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, EntityCrimsonMosquito.class, true));
            this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, EntityWarpedMosco.class, true));
        }
    }
    public void tick() {
        EntityStraddler straddler = (EntityStraddler)(Object)this;
        if (AInteractionConfig.straddlershots != 0) {
            if (shootshots <= 0) {
                shootcooldown--;
            }
            if (shootcooldown <= 0 && shootshots <= 0) {
                shootshots = AInteractionConfig.straddlershots;
                shootcooldown = 100;
            }
        }
        super.tick();
        this.floatStrider();
        this.checkInsideBlocks();
        if (straddler.getAnimation() == ANIMATION_LAUNCH && this.isAlive()){
            if(straddler.getAnimationTick() == 2){
                this.playSound(SoundEvents.CROSSBOW_LOADING_MIDDLE, 2F, 1F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
            }
        }
        if (AInteractionConfig.straddlershots != 0) {
            if (straddler.getAnimation() == ANIMATION_LAUNCH && this.isAlive() && straddler.getAnimationTick() == 20 && this.getTarget() != null && !(shootshots == 0)) {
                EntityStradpole pole = AMEntityRegistry.STRADPOLE.get().create(level());
                pole.setParentId(this.getUUID());
                pole.setPos(this.getX(), this.getEyeY(), this.getZ());
                final double d0 = this.getTarget().getEyeY() - (double) 1.1F;
                final double d1 = this.getTarget().getX() - this.getX();
                final double d2 = d0 - pole.getY();
                final double d3 = this.getTarget().getZ() - this.getZ();
                final float f3 = Mth.sqrt((float) (d1 * d1 + d2 * d2 + d3 * d3)) * 0.2F;
                this.gameEvent(GameEvent.PROJECTILE_SHOOT);
                this.playSound(SoundEvents.CROSSBOW_LOADING_END, 2F, 1F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
                pole.shoot(d1, d2 + (double) f3, d3, 2F, 0F);
                pole.setYRot(this.getYRot() % 360.0F);
                pole.setXRot(Mth.clamp(this.getYRot(), -90.0F, 90.0F) % 360.0F);
                if (!this.level().isClientSide) {
                    this.level().addFreshEntity(pole);
                }
                shootshots--;
            }
        } else {
            if (straddler.getAnimation() == ANIMATION_LAUNCH && this.isAlive() && straddler.getAnimationTick() == 20 && this.getTarget() != null) {
                EntityStradpole pole = AMEntityRegistry.STRADPOLE.get().create(level());
                pole.setParentId(this.getUUID());
                pole.setPos(this.getX(), this.getEyeY(), this.getZ());
                final double d0 = this.getTarget().getEyeY() - (double) 1.1F;
                final double d1 = this.getTarget().getX() - this.getX();
                final double d2 = d0 - pole.getY();
                final double d3 = this.getTarget().getZ() - this.getZ();
                final float f3 = Mth.sqrt((float) (d1 * d1 + d2 * d2 + d3 * d3)) * 0.2F;
                this.gameEvent(GameEvent.PROJECTILE_SHOOT);
                this.playSound(SoundEvents.CROSSBOW_LOADING_END, 2F, 1F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
                pole.shoot(d1, d2 + (double) f3, d3, 2F, 0F);
                pole.setYRot(this.getYRot() % 360.0F);
                pole.setXRot(Mth.clamp(this.getYRot(), -90.0F, 90.0F) % 360.0F);
                if (!this.level().isClientSide) {
                    this.level().addFreshEntity(pole);
                }
            }
        }
        AnimationHandler.INSTANCE.updateAnimations(straddler);
    }
    private void floatStrider() {
        if (this.isInLava()) {
            CollisionContext lvt_1_1_ = CollisionContext.of(this);
            double d1 = this.getFluidTypeHeight((FluidType) ForgeMod.LAVA_TYPE.get());
            if (d1 <= 0.5 && d1 > 0.0) {
                if (this.getDeltaMovement().y < 0.0) {
                    this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.0, 1.0));
                }

                this.setOnGround(true);
            } else if (lvt_1_1_.isAbove(LiquidBlock.STABLE_SHAPE, this.blockPosition().below(), true) && !this.level().getFluidState(this.blockPosition().above()).is(FluidTags.LAVA)) {
                this.setOnGround(true);
            } else {
                this.setDeltaMovement(0.0, Math.min(d1 - 0.5, 1.0) * 0.20000000298023224, 0.0);
            }
        }

    }

}
