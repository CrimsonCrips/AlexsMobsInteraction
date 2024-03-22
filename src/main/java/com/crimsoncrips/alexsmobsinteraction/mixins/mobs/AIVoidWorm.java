package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.ReflectionUtil;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.ai.*;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;


@Mixin(EntityVoidWorm.class)
public class AIVoidWorm extends Monster {

    float damagetaken = 0;

    boolean stunned = false;

    int stuntick = 0;

    int damageretain = 0;
    protected AIVoidWorm(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public boolean isNoGravity() {
        return !stunned;
    }

    @Override
    protected boolean isImmobile() {
        return stunned;
    }

    public boolean hurt(DamageSource source, float amount) {
        boolean prev = super.hurt(source, amount);
       if (!stunned) {
           damagetaken = damagetaken + amount;
       }
       damageretain = 500;

        return prev;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        EntityVoidWorm voidWorm = (EntityVoidWorm)(Object)this;

        if (damageretain > 0) damageretain--;
        else damagetaken = 0;
        if (damagetaken > 30) {
            stunned = true;
            damagetaken = 0;
        }
        if (stunned && !(stuntick <= 0)) {
            ReflectionUtil.setField(voidWorm, "stillTicks", 0);
            stuntick--;
            Objects.requireNonNull(this.getAttribute(Attributes.ARMOR)).setBaseValue(10.0);
        } else {
            stuntick = 100;
            Objects.requireNonNull(this.getAttribute(Attributes.ARMOR)).setBaseValue(4.0);
        }

        if (stuntick <= 0) stunned = false;

    }
    @Inject(method = "spit", at = @At("HEAD"),cancellable = true,remap = false)
    private void spit(Vec3 shotAt, boolean portal, CallbackInfo ci) {
        ci.cancel();
        EntityVoidWorm voidWorm = (EntityVoidWorm)(Object)this;
        if (!stunned){
            shotAt = shotAt.yRot(-this.getYRot() * 0.017453292F);
            EntityVoidWormShot shot = new EntityVoidWormShot(this.level(), voidWorm);
            double d0 = shotAt.x;
            double d1 = shotAt.y;
            double d2 = shotAt.z;
            float f = Mth.sqrt((float) (d0 * d0 + d2 * d2)) * 0.35F;
            shot.shoot(d0, d1 + (double) f, d2, 0.5F, 3.0F);
            if (!this.isSilent()) {
                this.gameEvent(GameEvent.PROJECTILE_SHOOT);
                this.level().playSound((Player) null, this.getX(), this.getY(), this.getZ(), SoundEvents.DROWNED_SHOOT, this.getSoundSource(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
            }

            voidWorm.openMouth(5);
            this.level().addFreshEntity(shot);
        }
        

 
    }




}
