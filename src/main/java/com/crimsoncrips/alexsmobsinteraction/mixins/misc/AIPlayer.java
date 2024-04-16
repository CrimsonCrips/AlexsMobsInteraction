package com.crimsoncrips.alexsmobsinteraction.mixins.misc;

import com.crimsoncrips.alexsmobsinteraction.AInteractionTagRegistry;
import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.github.alexthe666.alexsmobs.block.BlockBananaPeel;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;


@Mixin(Player.class)
public abstract class AIPlayer extends LivingEntity {

    protected AIPlayer(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void AlexInteraction$tick(CallbackInfo ci) {
        Block block = getFeetBlockState().getBlock();

        if(AInteractionConfig.bananaslip && AInteractionConfig.goofymode){
            if (block instanceof BlockBananaPeel){
                kill();
            }
        }

        if(AInteractionConfig.sunbirdupgrade){

            for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(7, 4, 7))) {
                EntityType<?> entityType = entity.getType();
                if (entityType.is(AInteractionTagRegistry.BURNABLE_DEAD) && !entity.isInWater()) {
                    if (hasEffect(AMEffectRegistry.SUNBIRD_BLESSING.get())) {
                        entity.setSecondsOnFire(3);
                    }
                    if (hasEffect(AMEffectRegistry.SUNBIRD_CURSE.get())) {
                        entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 350, 2));
                        entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 600, 0));
                        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600, 0));
                    }
                }
            }
        }



        if (AInteractionConfig.tigersstealth && hasEffect(AMEffectRegistry.TIGERS_BLESSING.get()) && isCrouching()){
            addEffect(new MobEffectInstance(MobEffects. INVISIBILITY, 20, 0));
        }

    }

}
