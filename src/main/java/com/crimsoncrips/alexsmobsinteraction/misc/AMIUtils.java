package com.crimsoncrips.alexsmobsinteraction.misc;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.RegistryObject;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;

import java.util.Random;


public class AMIUtils {


    public static void awardAdvancement(Entity entity, String advancementName, String criteria){
        if(entity instanceof ServerPlayer serverPlayer){
            Advancement advancement = serverPlayer.serverLevel().getServer().getAdvancements().getAdvancement(new ResourceLocation(AlexsMobsInteraction.MODID, advancementName));
            if (advancement != null) {
                serverPlayer.getAdvancements().award(advancement, criteria);
            }
        }
    }

    public static boolean chanceTrue(int level,int max) {
        if(level >= max) return true;   // 10 = 100%
        Random r = new Random();
        int roll = r.nextInt(max) + 1;  // 1-10
        return roll <= level;
    }

    public static Entity getClosestLookingAtEntityFor(Player player) {
        Entity closestValid = null;
        HitResult hitresult = ProjectileUtil.getHitResultOnViewVector(player, Entity::isAlive, 32);
        if (hitresult instanceof EntityHitResult) {
            Entity entity = ((EntityHitResult) hitresult).getEntity();
            if (!entity.equals(player) && player.hasLineOfSight(entity)) {
                closestValid = entity;
            }
        }
        return closestValid;
    }

    public static void spawnLoot (ResourceLocation location, LivingEntity entity, Entity owner, int loop){
        if (!entity.level().isClientSide){
            LootParams ctx = new LootParams.Builder((ServerLevel) entity.level()).withParameter(LootContextParams.THIS_ENTITY, entity).create(LootContextParamSets.EMPTY);
            ObjectArrayList<ItemStack> rewards = entity.level().getServer().getLootData().getLootTable(location).getRandomItems(ctx);
            if (!rewards.isEmpty()) {
                for (int i = 0; i <= loop; i++) {
                    rewards.forEach(stack -> BehaviorUtils.throwItem(entity, rewards.get(0), owner.position().add(0.0D, 1.0D, 0.0D)));
                }
            }
        }
    }

    //From Villager Class
    public static void addParticlesAroundSelf(ParticleOptions pParticleOption, LivingEntity entity, int amount, double scale) {
        Level level = entity.level();
        if (level.isClientSide){
            for(int i = 0; i < amount; ++i) {
                double d0 = entity.getRandom().nextGaussian() * 0.02D;
                double d1 = entity.getRandom().nextGaussian() * 0.02D;
                double d2 = entity.getRandom().nextGaussian() * 0.02D;
                level.addParticle(pParticleOption, entity.getRandomX(scale), entity.getRandomY() + scale, entity.getRandomZ(scale), d0, d1, d2);
            }
        } else {
            for(int i = 0; i < amount; ++i) {
                double d0 = entity.getRandom().nextGaussian() * 0.02D;
                double d1 = entity.getRandom().nextGaussian() * 0.02D;
                double d2 = entity.getRandom().nextGaussian() * 0.02D;
                ((ServerLevel) level).sendParticles(pParticleOption, entity.getRandomX(scale),entity.getRandomY() + scale, entity.getRandomZ(scale), 1, d0, d1, d2, 0.5D);
            }
        }

    }

    public static void addParticlesAroundBlock(ParticleOptions pParticleOption, BlockPos pos, Level level, int amount) {
        for(int i = 0; i < amount; ++i) {
            double d0 = level.getRandom().nextGaussian() * 0.02D;
            double d1 = level.getRandom().nextGaussian() * 0.02D;
            double d2 = level.getRandom().nextGaussian() * 0.02D;
            level.addParticle(pParticleOption, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, d0, d1, d2);
        }

    }



    //give it up for reimnop again for the help cus im too retarded to know what the fuck to do
    static void yaw(PoseStack poseStack, float value) {
        poseStack.mulPose(new Quaternionf(new AxisAngle4f(value, 0.0F, 1.0F, 0.0F)));
    }

    static void pitch(PoseStack poseStack, float value) {
        poseStack.mulPose(new Quaternionf(new AxisAngle4f(value, 1.0F, 0.0F, 0.0F)));
    }

    static void roll(PoseStack poseStack, float value) {
        poseStack.mulPose(new Quaternionf(new AxisAngle4f(value, 0.0F, 0.0F, 1.0F)));
    }


    static public int dimensionDeterminer(String string){
        return switch (string) {
            case "minecraft:overworld" -> 1;
            case "minecraft:the_nether" -> {
                //5 means better nether texture. 2 means vanilla nether texture

                int variantNo = AlexsMobsInteraction.CLIENT_CONFIG.NETHER_PORTAL_VARIANT.get();
                if (variantNo == 0){
                   yield ModList.get().isLoaded("betternether") ? 5 : 2;
                } else {
                   yield variantNo == 1 ? 2 : 5;
                }
            }
            case "minecraft:the_end" -> {
                //4 means better end texture. 3 means vanilla end texture

                int variantNo = AlexsMobsInteraction.CLIENT_CONFIG.END_PORTAL_VARIANT.get();
                if (variantNo == 0) {
                    yield ModList.get().isLoaded("betterend") ? 4 : 3;
                } else {
                    yield variantNo == 1 ? 3 : 4;
                }
            }
            default -> 0;
        };
    }


}
