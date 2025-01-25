package com.crimsoncrips.alexsmobsinteraction.effect;

import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityGust;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

import java.util.List;
import java.util.UUID;

public class AMIGusting extends MobEffect {

    private int timer;

    public AMIGusting() {
        super(MobEffectCategory.BENEFICIAL, 0Xfae6af);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, String.valueOf(UUID.randomUUID()), 0.35000000596046448, AttributeModifier.Operation.MULTIPLY_BASE);
    }

    public void applyEffectTick(LivingEntity entity, int amplifier) {
        Level level = entity.level();
        if (!AMInteractionConfig.GUSTING_ENABLED)
            return;
        if (entity.getRandom().nextDouble() < 0.5){
            for (int j = 0; j < 4; ++j) {
                float f1 = (entity.getRandom().nextFloat() * 2.0F - 1.0F) * entity.getBbWidth() * 0.95F;
                float f2 = (entity.getRandom().nextFloat() * 2.0F - 1.0F) * entity.getBbWidth() * 0.95F;
                level.addParticle(AMParticleRegistry.GUSTER_SAND_SPIN.get(), entity.getX() + (double) f1, entity.getY(), entity.getZ() + (double) f2, entity.getX(), entity.getY() + entity.getRandom().nextFloat() * entity.getBbHeight() + 0.2F, entity.getZ());
            }
        }
        if (timer < 0){
            timer = 40;
        }
        timer--;
        if (level.isClientSide)
            return;
        if(timer == 39 && entity instanceof LivingEntity){
            int z = 1;
            for (int x = -2; x != 2; x ++) {
                if (x > -2) {
                    summonGust(entity, x, z);
                    summonGust(entity, x, -z);
                    if (z <= 1) {
                        z++;
                    } else {
                        z--;
                    }
                }
            }
            if (entity instanceof Player) {
                summonGust(entity, 2, 0);
                summonGust(entity, -2, 0);
            }
        }
    }

    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration > 0;
    }


    public String getDescriptionId() {
        if (AMInteractionConfig.CHARGE_STUN_ENABLED) {
            return "alexsmobsinteraction.potion.gusting";
        } else {
            return "alexscavesexemplified.feature_disabled";
        }
    }

    private void summonGust(LivingEntity entity,double x, double z){
        Entity entityToSpawn = (AMEntityRegistry.GUST.get()).spawn((ServerLevel) entity.level(), BlockPos.containing(entity.getX() + x, entity.getY() + 0.2, entity.getZ() + z), MobSpawnType.MOB_SUMMONED);
        if (entityToSpawn != null) {
            entityToSpawn.tickCount = 250;
        }
    }

}