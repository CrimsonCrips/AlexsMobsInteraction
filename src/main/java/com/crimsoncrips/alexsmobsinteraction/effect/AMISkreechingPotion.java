package com.crimsoncrips.alexsmobsinteraction.effect;

import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import com.github.alexthe666.alexsmobs.entity.EntityEnderiophage;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.warden.Warden;

public class AMISkreechingPotion extends MobEffect {

    private int lastDuration = -1;

    public AMISkreechingPotion() {
        super(MobEffectCategory.HARMFUL, 0X142d40);
    }

    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (!AMInteractionConfig.SKREECH_YOUR_LAST_ENABLED)
            return;

        if (lastDuration == 120) {
            entity.level().addParticle(AMParticleRegistry.SKULK_BOOM.get(), entity.getX(), entity.getEyeY() - 1, entity.getZ(), 0, 0.2, 0);
        }

        if (entity.level().isClientSide)
            return;

        if (lastDuration == 20){
            entity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 500, 1));
            Entity entityToSpawn = (EntityType.WARDEN).spawn((ServerLevel) entity.level(), BlockPos.containing(entity.getX() + 0.5, entity.getY() + 1.0, entity.getZ() + 0.5), MobSpawnType.TRIGGERED);

            if (entityToSpawn instanceof Warden warden){
                warden.copyPosition(entity);
                warden.setTarget(entity);
                entity.level().addFreshEntity(warden);
            }
        }

    }

    public boolean isDurationEffectTick(int duration, int amplifier) {
        lastDuration = duration;
        return duration > 0;
    }

    public String getDescriptionId() {
        return "alexsmobsinteraction.potion.skreeching";
    }

}