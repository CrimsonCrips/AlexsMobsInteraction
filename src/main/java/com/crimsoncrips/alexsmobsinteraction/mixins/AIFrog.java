package com.crimsoncrips.alexsmobsinteraction.mixins;

import com.crimsoncrips.alexsmobsinteraction.AInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.core.particles.ParticleTypes;
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


@Mixin(Frog.class)
public class AIFrog extends Mob {
    int maggotFed,mungusFed,warpedFed,frogWarped;

    boolean transforming = false;



    protected AIFrog(EntityType<? extends Mob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    protected boolean isImmobile() {
        return super.isImmobile() || transforming;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void AlexInteraction$tick(CallbackInfo ci) {
        Frog frog = (Frog)(Object)this;
            if (mungusFed >= 1 && warpedFed >= 2 && maggotFed >= 10 && AInteractionConfig.frogtransform) {
                transforming=true;
                frogWarped++;
                frog.moveTo(this.getX(), this.getY(), this.getZ());
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
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        InteractionResult type = super.mobInteract(player, hand);
        if (stack.getItem() == AMItemRegistry.MAGGOT.get() && AInteractionConfig.frogtransform && !(maggotFed >= 10)) {

            gameEvent(GameEvent.ENTITY_INTERACT);
            stack.shrink(1);
            this.playSound(SoundEvents.GENERIC_EAT);
            maggotFed++;
            return InteractionResult.SUCCESS;
        }
        if (stack.getItem() == AMItemRegistry.MUNGAL_SPORES.get() && AInteractionConfig.frogtransform && !(mungusFed >= 1)) {
            gameEvent(GameEvent.ENTITY_INTERACT);
            stack.shrink(1);
            this.playSound(SoundEvents.GENERIC_EAT);
            mungusFed++;
            return InteractionResult.SUCCESS;
        }
        if (stack.getItem() == Items.WARPED_FUNGUS && AInteractionConfig.frogtransform && !(warpedFed >= 2)) {
            gameEvent(GameEvent.ENTITY_INTERACT);
            stack.shrink(1);
            this.playSound(SoundEvents.GENERIC_EAT);
            warpedFed++;
            return InteractionResult.SUCCESS;
        } else return type;
    }
}
