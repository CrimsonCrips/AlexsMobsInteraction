package com.crimsoncrips.alexsmobsinteraction.mixins;

import com.crimsoncrips.alexsmobsinteraction.AInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityBananaSlug;
import com.github.alexthe666.alexsmobs.entity.EntityCrimsonMosquito;
import com.github.alexthe666.alexsmobs.entity.EntityStraddler;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Frog.class)
public class AIFrog extends Mob {

    int maggotFed,mungusFed,warpedFed;



    protected AIFrog(EntityType<? extends Mob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }


    @Inject(method = "tick", at = @At("HEAD"))
    private void AlexInteraction$tick(CallbackInfo ci) {
        if (mungusFed >= 1 && warpedFed >= 2 && maggotFed >= 10) {
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
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() == AMItemRegistry.MAGGOT.get() && AInteractionConfig.frogtransform && !(maggotFed >= 10)) {
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
        return null;
    }
}
