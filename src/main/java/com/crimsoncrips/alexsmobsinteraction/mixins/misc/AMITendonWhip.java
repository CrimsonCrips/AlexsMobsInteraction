package com.crimsoncrips.alexsmobsinteraction.mixins.misc;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.crimsoncrips.alexsmobsinteraction.server.enchantment.AMIEnchantmentRegistry;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityTendonSegment;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
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
        if (AlexsMobsInteraction.COMMON_CONFIG.TENDON_GRAB_ENABLED.get()) {
            creator = tendonSegment.getCreatorEntity();

            if (creator instanceof Player player && player.getMainHandItem().getEnchantmentLevel(AMIEnchantmentRegistry.STRETCHY_ACCUMULATION.get()) > 0) {

                Vec3 creatorPos = creator.position();
                for (Entity entity : tendonSegment.level().getEntitiesOfClass(Entity.class, tendonSegment.getBoundingBox().inflate(2, 2, 2))) {
                    if(entity instanceof ExperienceOrb || entity instanceof ItemEntity){
                        entity.setPos(creatorPos);
                        if (entity instanceof ItemEntity item){
                            item.setPickUpDelay(0);
                        }
                        AMIUtils.awardAdvancement(player, "stretchy_accumulation", "stretch");
                    }
                }


            }

        }
    }
}
