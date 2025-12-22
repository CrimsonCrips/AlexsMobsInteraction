package com.crimsoncrips.alexsmobsinteraction.misc;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.github.alexmodguy.alexscaves.server.entity.living.DeepOneBaseEntity;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
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
            case "minecraft:nether" -> 2;
            case "minecraft:the_end" -> 3;
            default -> 0;
        };
    }


}
