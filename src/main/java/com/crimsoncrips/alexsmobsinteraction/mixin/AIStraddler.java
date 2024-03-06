package com.crimsoncrips.alexsmobsinteraction.mixin;

import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
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

import static com.github.alexthe666.alexsmobs.entity.EntityStraddler.ANIMATION_LAUNCH;


@Mixin(EntityStraddler.class)
public class AIStraddler extends Mob {

    int shootcooldown = 0;

    int shootshots = AInteractionConfig.straddlershots;


    protected AIStraddler(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void SoulVultureGoals(CallbackInfo ci){
        if(AInteractionConfig.stradllervengeance) {
            this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, EntityBoneSerpent.class, true));
            this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, EntityCrimsonMosquito.class, true));
        }
    }
    public void tick() {
        EntityStraddler straddler = (EntityStraddler) (Object) this;
        if (AInteractionConfig.straddlershots != 0) {
            if (shootshots == 0) {
                shootcooldown--;
            }
            if (shootcooldown <= 0) {
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
