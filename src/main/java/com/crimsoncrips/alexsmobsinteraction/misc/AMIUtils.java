package com.crimsoncrips.alexsmobsinteraction.misc;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.ClipContext;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import com.github.alexthe666.alexsmobs.entity.EntityCrocodile;
import com.github.alexthe666.alexsmobs.entity.EntityBoneSerpentPart;
import com.github.alexthe666.alexsmobs.entity.EntityBoneSerpent;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.resources.ResourceKey;
import net.minecraft.advancements.AdvancementHolder;
import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.networking.ToastPacket;
import com.crimsoncrips.alexsmobsinteraction.server.AMIAttachments;
import net.minecraft.util.Mth;
import com.github.alexthe666.alexsmobs.entity.EntityGrizzlyBear;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.advancements.Advancement;
import com.github.alexthe666.alexsmobs.entity.EntityCockroach;
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.item.component.CustomData;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;

import java.util.Random;


public class AMIUtils {

    public static final int TRANSFORM_DURATION = seconds(8);

    public static int seconds(float seconds) {
        return Math.round(seconds * 20.0F);
    }

    public static boolean isTransforming(LivingEntity livingEntity) {
        return livingEntity.getData(AMIAttachments.TRANSFORMING_TIME) > 0;
    }

    public static boolean tickTransforming(LivingEntity livingEntity) {
        int transformTime = livingEntity.getData(AMIAttachments.TRANSFORMING_TIME);
        if (transformTime <= 0) {
            return false;
        }
        livingEntity.setData(AMIAttachments.TRANSFORMING_TIME, transformTime - 1);
        return transformTime == 1;
    }

    public static void awardAdvancement(Entity entity, String advancementName, String criteria){
        if(entity instanceof ServerPlayer serverPlayer){
            AdvancementHolder advancement = serverPlayer.serverLevel().getServer().getAdvancements().get(ResourceLocation.fromNamespaceAndPath(AlexsMobsInteraction.MODID, advancementName));
            if (advancement != null) {
                serverPlayer.getAdvancements().award(advancement, criteria);
            }
        }
    }

    public static void sendToast(Player player, Component message, long displayTimeMs) {
        if (player instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer, new ToastPacket(message, displayTimeMs));
        }
    }

    public static void makeServant(EntityCockroach servant, Entity asmon) {
        AMIAttachments.setUUID(servant, AMIAttachments.WORSHIPING_UUID, asmon.getUUID());
        servant.setCustomName(Component.nullToEmpty("Servant"));
        servant.setData(AMIAttachments.SERVANT_GLOW_START, servant.level().getGameTime());
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

    private static final Object2IntMap<String> POTION_COLORS = new Object2IntOpenHashMap<>();

    @Nullable
    public static Entity getWorshiping(Entity entity) {
        if (entity.level() instanceof ServerLevel serverLevel) {
            UUID id = AMIAttachments.getUUID(entity, AMIAttachments.WORSHIPING_UUID);
            return id == null ? null : serverLevel.getEntity(id);
        }
        return null;
    }

    @Nullable
    public static Entity getBoneChild(Entity entity) {
        if (entity instanceof EntityBoneSerpent head)
            return head.getChild();
        if (entity.level() instanceof ServerLevel serverLevel) {
            UUID id = AMIAttachments.getUUID(entity, AMIAttachments.CHILD_UUID);
            return id == null ? null : serverLevel.getEntity(id);
        }
        return null;
    }

    public static void detectBoneChildLoop(EntityBoneSerpentPart part) {
        if (getBoneChild(part) instanceof EntityBoneSerpentPart child) {
            if (child.isTail()) {
                if (part.getParent() instanceof EntityBoneSerpent)
                    return;
                child.discard();
                part.setTail(true);
            } else {
                detectBoneChildLoop(child);
            }
        }
    }

    @Nullable
    public static MobEffect getPotionEffect(Entity entity) {
        String id = entity.getData(AMIAttachments.POTION_ID);
        return id == null || id.isEmpty() ? null : BuiltInRegistries.MOB_EFFECT.get(ResourceLocation.parse(id));
    }

    public static int getPotionColor(Entity entity) {
        String id = entity.getData(AMIAttachments.POTION_ID);
        if (id == null || id.isEmpty())
            return -1;
        if (!POTION_COLORS.containsKey(id)) {
            MobEffect effect = getPotionEffect(entity);
            if (effect == null)
                return -1;
            POTION_COLORS.put(id, effect.getColor());
        }
        return POTION_COLORS.getInt(id);
    }

    public static boolean isWally(Entity entity) {
        return entity instanceof EntityCrocodile crocodile && crocodile.isTame() && crocodile.getName().getString().toLowerCase().contains("wally") && AlexsMobsInteraction.COMMON_CONFIG.EMOTIONAL_REMEMEMBRANCE_ENABLED.get();
    }

    public static boolean isBlueKoopa(Entity entity) {
        String name = ChatFormatting.stripFormatting(entity.getName().getString());
        if (name == null || !AlexsMobsInteraction.COMMON_CONFIG.BLUE_SHELL_ENABLED.get())
            return false;
        name = name.toLowerCase();
        return name.contains("blue shell") || name.contains("blue koopa");
    }

    @Nullable
    public static BlockPos findPollinatablePlant(Entity entity, int horizontalRange, int verticalRange) {
        Level level = entity.level();
        Vec3 eye = entity.getEyePosition();
        BlockPos origin = entity.blockPosition();
        BlockPos closest = null;
        double closestDistance = Double.MAX_VALUE;
        for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-horizontalRange, -verticalRange, -horizontalRange), origin.offset(horizontalRange, verticalRange, horizontalRange))) {
            BlockState state = level.getBlockState(pos);
            if (!(state.getBlock() instanceof BonemealableBlock bonemealable) || state.is(BlockTags.DIRT) || state.is(BlockTags.NYLIUM) || !level.getFluidState(pos).isEmpty() || !bonemealable.isValidBonemealTarget(level, pos, state))
                continue;
            double distance = pos.distToCenterSqr(eye);
            if (distance >= closestDistance)
                continue;
            Vec3 center = Vec3.atCenterOf(pos);
            BlockHitResult hit = level.clip(new ClipContext(eye, center, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));
            if (hit.getType() == HitResult.Type.MISS || hit.getBlockPos().equals(pos)) {
                closest = pos.immutable();
                closestDistance = distance;
            }
        }
        return closest;
    }

    public static void flingFromChamber(Entity entity) {
        RandomSource random = entity.getRandom();
        float angle = random.nextFloat() * Mth.TWO_PI;
        double horizontal = 0.15 + random.nextDouble() * 0.15;
        entity.setDeltaMovement(Mth.cos(angle) * horizontal, 0.45 + random.nextDouble() * 0.2, Mth.sin(angle) * horizontal);
        entity.hurtMarked = true;
    }

    public static boolean canBrushGrizzly(EntityGrizzlyBear grizzlyBear, Player player) {
        return AlexsMobsInteraction.COMMON_CONFIG.BRUSHED_ENABLED.get() && grizzlyBear.isAlive() && grizzlyBear.isHoneyed() && !grizzlyBear.getData(AMIAttachments.URSA) && !(grizzlyBear.isTame() && grizzlyBear.isOwnedBy(player));
    }

    public static void spawnLoot (ResourceKey<LootTable> location, LivingEntity entity, Entity owner, int loop){
        if (!entity.level().isClientSide){
            LootParams ctx = new LootParams.Builder((ServerLevel) entity.level()).withParameter(LootContextParams.THIS_ENTITY, entity).create(LootContextParamSets.EMPTY);
            ObjectArrayList<ItemStack> rewards = entity.level().getServer().reloadableRegistries().getLootTable(location).getRandomItems(ctx);
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



    public static final String PUPA_VARIANT = "AMIAntVariant";

    public static int getPupaVariant(ItemStack pupa) {
        return pupa.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getInt(PUPA_VARIANT);
    }

    public static int pickPupaVariant(ItemStack pupa, RandomSource random) {
        int variant = getPupaVariant(pupa);
        return variant == 1 || variant == 2 ? variant : (random.nextBoolean() ? 1 : 2);
    }
}
