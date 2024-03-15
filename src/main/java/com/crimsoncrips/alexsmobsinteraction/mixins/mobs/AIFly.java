package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.goal.AvoidBlockGoal;
import com.crimsoncrips.alexsmobsinteraction.goal.FollowNearestGoal;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.crimsoncrips.alexsmobsinteraction.item.AIItemRegistry;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(EntityFly.class)
public class AIFly extends Mob {

    private boolean noFollow = false;

    boolean blooded = false;

    protected AIFly(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void FlyGoals(CallbackInfo ci){
        EntityFly fly = (EntityFly)(Object)this;
        java.util.function.Predicate<LivingEntity> PESTERTARGET = AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.FLY_PESTER);
        if(AInteractionConfig.flyfearall) {
            this.goalSelector.addGoal(7, new AvoidEntityGoal<>(fly, LivingEntity.class, 2.0F, 1.1, 1.3, (livingEntity) ->{
                return !PESTERTARGET.test(livingEntity) && !(livingEntity instanceof EntityFly);
            }));
        }
        if(AInteractionConfig.flyfearcandles){
            this.goalSelector.addGoal(3, new AvoidBlockGoal(fly, 4, 1, 1.2, (pos) -> {
                BlockState state = level().getBlockState(pos);
                return state.is(BlockTags.CANDLES);
            }));
        }
        if(AInteractionConfig.flypester) {
            this.goalSelector.addGoal(8, new FollowNearestGoal<>(fly, LivingEntity.class, 3, 1.2, (livingEntity) -> {
                return PESTERTARGET.test(livingEntity) && !noFollow;
            }));
        }
    }

    boolean mungusAte = false;
    public boolean hurt(DamageSource source, float amount) {
        boolean prev = super.hurt(source, amount);
        if (source.getDirectEntity() instanceof EntityMosquitoSpit && AInteractionConfig.flyconvert) {
            blooded = true;
        }

        return prev;
    }
    @Inject(method = "mobInteract", at = @At("HEAD"))
    private void mobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir){
        ItemStack itemstack = player.getItemInHand(hand);
     if (AInteractionConfig.flyconvert && (itemstack.getItem() == AMItemRegistry.MUNGAL_SPORES.get()) && !mungusAte) {
            this.gameEvent(GameEvent.EAT);
            this.playSound(SoundEvents.GENERIC_EAT, this.getSoundVolume(), this.getVoicePitch());
            mungusAte = true;
        }
        if (itemstack.getItem() == AIItemRegistry.SWATTER.get() && AInteractionConfig.flyconvert && !pacify) {
            gameEvent(GameEvent.ENTITY_INTERACT);
            itemstack.hurtAndBreak(1, this, (p_233654_0_) -> {
            });
            pacify = true;
            this.playSound(AMSoundRegistry.FLY_HURT.get(), 2F, 1F);

        }

    }
    boolean pacify = false;

    int flyConvert;
    double y2 = 0;


    @Inject(method = "tick", at = @At("HEAD"))
    private void Test(CallbackInfo ci) {
        if (pacify){
                y2 = -0.05 + y2;
                this.setDeltaMovement(0, y2, 0);
           }
        if(AInteractionConfig.flypester){
            if (random.nextDouble() < 0.001 && !noFollow || level().isNight()) noFollow = true;
            if (random.nextDouble() < 0.05 && noFollow && level().isDay()) noFollow = false;
        }
        if (pacify && blooded && mungusAte) {
            flyConvert++;
            this.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20, 0));
            if (flyConvert > 60) {
                this.setDeltaMovement(0, 0.01, 0);
            }
            if (flyConvert > 160) {
                EntityCrimsonMosquito crimsonMosquito = AMEntityRegistry.CRIMSON_MOSQUITO.get().create(level());
                crimsonMosquito.copyPosition(this);
                if (!this.level().isClientSide) {
                    crimsonMosquito.finalizeSpawn((ServerLevelAccessor) level(), level().getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.CONVERSION, null, null);
                }

                if (!this.level().isClientSide) {
                    this.level().broadcastEntityEvent(this, (byte) 79);
                    level().addFreshEntity(crimsonMosquito);
                }
                this.remove(RemovalReason.DISCARDED);

            }
        }
    }
}
