package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.crimsoncrips.alexsmobsinteraction.AInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityRainFrog;
import com.github.alexthe666.alexsmobs.entity.EntityWarpedToad;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(EntityRainFrog.class)
public class AIRainfrog extends Mob {

    int maggotFed,mungusFed,warpedFed,frogWarped;

    boolean transforming = false;

    protected AIRainfrog(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void BlobFishGoals(CallbackInfo ci) {
        if (AInteractionConfig.preyfear)
            this.goalSelector.addGoal(3, new AvoidEntityGoal((EntityRainFrog)(Object)this, LivingEntity.class, 10.0F, 1.2, 1.5,AMEntityRegistry.buildPredicateFromTag(AInteractionTagRegistry.RAINFROG_FEAR)));

    }

    protected boolean isImmobile() {
        return super.isImmobile() || transforming;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void AlexInteraction$tick(CallbackInfo ci) {
        EntityRainFrog frog = (EntityRainFrog)(Object)this;
        if (mungusFed >= 1 && warpedFed >= 2 && maggotFed >= 5 && AInteractionConfig.frogtransform) {
            transforming=true;
            frogWarped++;
            frog.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20, 0));
            if (frogWarped > 60) {
                this.setDeltaMovement(0, 0.01, 0);
            }
            if (frogWarped > 160) {
                EntityWarpedToad warpedToad = AMEntityRegistry.WARPED_TOAD.get().create(level());
                warpedToad.copyPosition(this);
                if (!this.level().isClientSide) {
                    warpedToad.finalizeSpawn((ServerLevelAccessor) level(), level().getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.CONVERSION, null, null);
                }
                frog.playSound(SoundEvents.ZOMBIE_VILLAGER_CONVERTED);

                if (!this.level().isClientSide) {
                    this.level().broadcastEntityEvent(this, (byte) 79);
                    level().addFreshEntity(warpedToad);
                }
                frog.remove(RemovalReason.DISCARDED);

            }
        }
    }

    @Inject(method = "mobInteract", at = @At("HEAD"))
    private void AlexInteraction$tick(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        {
            ItemStack stack = player.getItemInHand(hand);
            InteractionResult type = super.mobInteract(player, hand);
            if (stack.getItem() == AMItemRegistry.MAGGOT.get() && AInteractionConfig.frogtransform && !(maggotFed >= 5)) {

                gameEvent(GameEvent.ENTITY_INTERACT);
                stack.shrink(1);
                this.playSound(SoundEvents.GENERIC_EAT);
                maggotFed++;

            }
            if (stack.getItem() == AMItemRegistry.MUNGAL_SPORES.get() && AInteractionConfig.frogtransform && !(mungusFed >= 1)) {
                gameEvent(GameEvent.ENTITY_INTERACT);
                stack.shrink(1);
                this.playSound(SoundEvents.GENERIC_EAT);
                mungusFed++;

            }
            if (stack.getItem() == Items.WARPED_FUNGUS && AInteractionConfig.frogtransform && !(warpedFed >= 2)) {
                gameEvent(GameEvent.ENTITY_INTERACT);
                stack.shrink(1);
                this.playSound(SoundEvents.GENERIC_EAT);
                warpedFed++;

            }
        }
    }

}
