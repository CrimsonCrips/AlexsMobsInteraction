package com.crimsoncrips.alexsmobsinteraction.mixins.misc;

import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.crimsoncrips.alexsmobsinteraction.effect.AMIEffects;
import com.crimsoncrips.alexsmobsinteraction.enchantment.AMIEnchantmentRegistry;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityCrimsonMosquito;
import com.github.alexthe666.alexsmobs.entity.EntityTendonSegment;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;


@Mixin(EntityTendonSegment.class)
public abstract class AMITendonWhip extends Entity {

    @Unique
    Entity creator;


    public AMITendonWhip(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        EntityTendonSegment tendonSegment = (EntityTendonSegment)(Object)this;
        if (AMInteractionConfig.TENDON_GRAB_ENABLED) {
            creator = tendonSegment.getCreatorEntity();

            if (creator instanceof Player player && player.getMainHandItem().getEnchantmentLevel(AMIEnchantmentRegistry.STRETCHY_ACCUMULATION.get()) > 0) {

                Vec3 creatorPos = creator.position();

                List<ItemEntity> items = this.level().getEntitiesOfClass(ItemEntity.class, getBoundingBox().inflate(2));
                for(ItemEntity item : items) {
                    item.setPos(creatorPos);
                }

                List<ExperienceOrb> xp = this.level().getEntitiesOfClass(ExperienceOrb.class, getBoundingBox().inflate(2));

                for(ExperienceOrb xpOrb : xp) {
                    xpOrb.setPos(creatorPos);
                }
            }

        }
    }
}
