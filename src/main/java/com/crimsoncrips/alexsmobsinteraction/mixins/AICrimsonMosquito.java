package com.crimsoncrips.alexsmobsinteraction.mixins;

import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
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


@Mixin(EntityCrimsonMosquito.class)
public abstract class AICrimsonMosquito extends Mob {

    @Shadow private int sickTicks;
    public boolean given = false;

    int sporeFed = 0;

    int warpedFed = 0;



    protected AICrimsonMosquito(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }
    @Inject(method = "tick", at = @At("HEAD"))
    private void AlexInteraction$tick(CallbackInfo ci) {
        EntityCrimsonMosquito crimsonMosquito = (EntityCrimsonMosquito)(Object)this;
        if(AInteractionConfig.bloodedmosquitoes && !given){
            crimsonMosquito.setBloodLevel(random.nextInt(2));
            given = true;
        }
        if (sporeFed >= 3 && warpedFed >= 10) {
            crimsonMosquito.setSick(true);
            if (sickTicks > 159){
                for (int i = 0; i < 27; ++i) {
                    double d0 = this.random.nextGaussian() * 0.02D;
                    double d1 = this.random.nextGaussian() * 0.02D;
                    double d2 = this.random.nextGaussian() * 0.02D;
                    this.level().addParticle(ParticleTypes.EXPLOSION, this.getRandomX(1.6D), this.getY() + random.nextFloat() * 3.4F, this.getRandomZ(1.6D), d0, d1, d2);
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
        if (stack.getItem() == AMItemRegistry.MUNGAL_SPORES.get() && AInteractionConfig.crimsontransform && !(sporeFed >= 3)) {
            gameEvent(GameEvent.ENTITY_INTERACT);
            stack.shrink(1);
            sporeFed++;
        }
        if (stack.getItem() == Items.WARPED_FUNGUS && AInteractionConfig.crimsontransform && !(warpedFed >= 10)) {
            gameEvent(GameEvent.ENTITY_INTERACT);
            stack.shrink(1);
            warpedFed++;
        }
    }

}
