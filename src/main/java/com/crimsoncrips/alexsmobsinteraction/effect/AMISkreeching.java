package com.crimsoncrips.alexsmobsinteraction.effect;

import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.github.alexmodguy.alexscaves.server.entity.ACFrogRegistry;
import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;

import java.util.ArrayList;
import java.util.List;

public class AMISkreeching extends MobEffect {

    private int lastDuration = -1;

    private float damage = 1;

    public AMISkreeching() {
        super(MobEffectCategory.HARMFUL, 0X142d40);
    }


    public void applyEffectTick(LivingEntity entity, int amplifier) {
        Level level = entity.level();
        if (!AMInteractionConfig.SKREECH_YOUR_LAST_ENABLED)
            return;
        if (!(entity instanceof Player))
            return;

        if (lastDuration == 100 || lastDuration == 95 || lastDuration == 90 || lastDuration == 85) {
            entity.playSound(AMSoundRegistry.SKREECHER_CALL.get(),1 * 3F,1);
            level.addParticle(AMParticleRegistry.SKULK_BOOM.get(), entity.getX(), entity.getY() + 0.1, entity.getZ(), 0, 0, 0);
        }

        if (entity.getRandom().nextDouble() < 0.001 && lastDuration > 100){
            SoundEvent soundEvents = switch (entity.getRandom().nextInt(0, 3)) {
                case 0 -> SoundEvents.WARDEN_NEARBY_CLOSE;
                case 1 -> SoundEvents.WARDEN_NEARBY_CLOSER;
                default -> SoundEvents.WARDEN_NEARBY_CLOSEST;
            };
            level.playSound(null,entity.getOnPos(),soundEvents, SoundSource.AMBIENT, 1, -1);
            entity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60, 0));
        }

        if (lastDuration <= 1){
            if (!entity.onGround()){
                level.playSound(null,entity.getOnPos(),SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.AMBIENT, 2, 1);
                entity.hurt(entity.damageSources().generic(),damage);
                damage = damage * 1.1F;
                System.out.println(damage);
                for (int i = 0; i < 50; ++i) {
                    double d0 = entity.getRandom().nextGaussian() * 0.02D;
                    double d1 = entity.getRandom().nextGaussian() * 0.02D;
                    double d2 = entity.getRandom().nextGaussian() * 0.02D;
                    level.addParticle(ParticleTypes.SCULK_SOUL, entity.getX(), entity.getY() + 0.5, entity.getZ(), d0, d1, d2);
                }
                entity.removeEffect(AMIEffects.SKREECHING.get());
                entity.addEffect(new MobEffectInstance(AMIEffects.SKREECHING.get(), 200, 0));
            } else {
                RandomSource random = entity.getRandom();
                for (int x = 0; x < 5; x++){
                    for (int z = 0; z < 5; z++){
                        BlockPos sculkPos = new BlockPos((int) (entity.getX() + x - 2), (int) (entity.getY() - 1), (int) (entity.getZ() + z - 2));
                        BlockState sculkPosState = level.getBlockState(sculkPos);
                        if (random.nextDouble() < 0.7 && sculkPosState.is(BlockTags.SCULK_REPLACEABLE)) {
                            level.setBlock(sculkPos, Blocks.SCULK.defaultBlockState(),2);
                            level.scheduleTick(sculkPos, sculkPosState.getBlock(), 8);
                            level.playSound((Player)null, sculkPos, SoundEvents.SCULK_CATALYST_BLOOM, SoundSource.BLOCKS, 2.0F, 0.6F + random.nextFloat() * 0.4F);
                            if (random.nextDouble() < 0.6) level.addParticle(ParticleTypes.SCULK_SOUL, sculkPos.getX() + 0.5, sculkPos.getY() + 1.15, sculkPos.getZ() + 0.5,  0.0, 0.05, 0.0);
                            if (random.nextDouble() < 0.6) for (int i = 0; i < random.nextInt(5); i++)level.addParticle(ParticleTypes.SCULK_CHARGE_POP, sculkPos.getX() + 0.5, sculkPos.getY() + 1.15, sculkPos.getZ() + 0.5,  0 + random.nextGaussian() * 0.02, 0.01 + random.nextGaussian() * 0.02, 0 + random.nextGaussian() * 0.02);
                        }
                    }
                }

                if (!level.isClientSide) {
                    LivingEntity entityToSpawn;
                    entityToSpawn = EntityType.WARDEN.spawn((ServerLevel) level, BlockPos.containing(entity.getX() + 0.5, entity.getY() + 1.0, entity.getZ() + 0.5), MobSpawnType.TRIGGERED);
                    if (entityToSpawn instanceof Warden warden) {
                        entity.removeEffect(AMIEffects.SKREECHING.get());
                        warden.setTarget(entity);
                        final CompoundTag emptyNbt = new CompoundTag();
                        warden.addAdditionalSaveData(emptyNbt);
                        emptyNbt.putString("DeathLootTable", BuiltInLootTables.EMPTY.toString());
                        warden.skipDropExperience();
                    }
                }
            }
        }

    }

    public boolean isDurationEffectTick(int duration, int amplifier) {
        lastDuration = duration;
        return duration > 0;
    }

    public String getDescriptionId() {
        if (AMInteractionConfig.CHARGE_STUN_ENABLED) {
            return "alexsmobsinteraction.potion.skreeching";
        } else {
            return "alexscavesexemplified.feature_disabled";
        }
    }

    public List<ItemStack> getCurativeItems() {
        return List.of();
    }
}