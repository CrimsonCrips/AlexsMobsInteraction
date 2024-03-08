package com.crimsoncrips.alexsmobsinteraction.mixins;

import com.crimsoncrips.alexsmobsinteraction.AInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.FlyMosquitoGoal;
import com.crimsoncrips.alexsmobsinteraction.ReflectionUtil;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;


@Mixin(EntityCrimsonMosquito.class)
public abstract class AICrimsonMosquito extends Mob {

    @Shadow private int sickTicks;

    @Shadow public abstract boolean isFlying();

    @Shadow public abstract void setFlying(boolean flying);

    public boolean given = false;

    int sporeFed = 0;

    int warpedFed = 0;

    boolean pacified = false;



    protected AICrimsonMosquito(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "registerGoals", at = @At("HEAD"),cancellable = true)
    private void CrimsonGoals(CallbackInfo ci){
        ci.cancel();
        EntityCrimsonMosquito crimsonMosquito = (EntityCrimsonMosquito)(Object)this;
        Predicate<LivingEntity> NO_REPELLENT = (mob) -> {
            return !mob.hasEffect(AMEffectRegistry.MOSQUITO_REPELLENT.get());

        };

            this.goalSelector.addGoal(2, new EntityCrimsonMosquito.FlyTowardsTarget(crimsonMosquito));
            this.goalSelector.addGoal(2, new EntityCrimsonMosquito.FlyAwayFromTarget(crimsonMosquito));
            this.goalSelector.addGoal(3, new FlyMosquitoGoal(crimsonMosquito));

            this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
            this.targetSelector.addGoal(1, new HurtByTargetGoal(crimsonMosquito, new Class[]{EntityCrimsonMosquito.class, EntityWarpedMosco.class}));
            this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, Player.class, 20, true, false, NO_REPELLENT));
            this.targetSelector.addGoal(2, new EntityAINearestTarget3D(this, LivingEntity.class, 50, false, true, AMEntityRegistry.buildPredicateFromTag(AMTagRegistry.CRIMSON_MOSQUITO_TARGETS)));
            this.goalSelector.addGoal(3, new AvoidEntityGoal(crimsonMosquito, EntityTriops.class, 16.0F, 1.3, 1.0));

    }


    @Inject(method = "tick", at = @At("HEAD"))
    private void AlexInteraction$tick(CallbackInfo ci) {
        EntityCrimsonMosquito crimsonMosquito = (EntityCrimsonMosquito)(Object)this;
        if (pacified){
            this.setFlying(false);
            this.jumping = false;
            if (onGround()) this.setNoAi(true);
        }
        if(AInteractionConfig.bloodedmosquitoes && !given){
            crimsonMosquito.setBloodLevel(random.nextInt(2));
            given = true;
        }
        if (sporeFed >= 3 && warpedFed >= 10) {
            crimsonMosquito.setSick(true);
            if (sickTicks > 159){
                for (int i = 0; i < 100; ++i) {
                    double d0 = this.random.nextGaussian() * 0.02D;
                    double d1 = this.random.nextGaussian() * 0.02D;
                    double d2 = this.random.nextGaussian() * 0.02D;
                    this.level().addParticle(ParticleTypes.CRIMSON_SPORE, this.getRandomX(1.6D), this.getY() + random.nextFloat() * 3.4F, this.getRandomZ(1.6D), d0, d1, d2);
                }
            }
        }
        
        if (crimsonMosquito.getTarget() instanceof EntityStraddler && !(crimsonMosquito.getBloodLevel() > 0)){
            setTarget(null);
        }

    }
    @Inject(method = "mobInteract", at = @At("TAIL"))
    private void mobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir){
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() == AMItemRegistry.MUNGAL_SPORES.get() && AInteractionConfig.crimsontransform && !(sporeFed >= 3) && pacified) {
            gameEvent(GameEvent.ENTITY_INTERACT);
            stack.shrink(1);
            this.playSound(SoundEvents.GENERIC_EAT);
            sporeFed++;
        }
        if (stack.getItem() == Items.DIAMOND_AXE && AInteractionConfig.crimsontransform && !pacified) {
            gameEvent(GameEvent.ENTITY_INTERACT);
            stack.hurtAndBreak(1, this, (p_233654_0_) -> {
            });
            pacified = true;

        }
        if (stack.getItem() == Items.WARPED_FUNGUS && AInteractionConfig.crimsontransform && !(warpedFed >= 10) && pacified) {
            gameEvent(GameEvent.ENTITY_INTERACT);
            stack.shrink(1);
            this.playSound(SoundEvents.GENERIC_EAT);
            warpedFed++;
        }
    }

}
