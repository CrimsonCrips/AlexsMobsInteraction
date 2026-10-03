package com.crimsoncrips.alexsmobsinteraction.server;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

public class AMIAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, AlexsMobsInteraction.MODID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> SWIPES = ATTACHMENT_TYPES.register("swipes", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT, value -> value != 0).sync((holder, player) -> holder == player, ByteBufCodecs.VAR_INT).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> SWIPE_DELAY = savedInt("swipe_delay", 0);
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> NO_HONEY = savedInt("no_honey", 0);
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> GUST_SPIN = ATTACHMENT_TYPES.register("gust_spin", () -> AttachmentType.builder(() -> 0).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> GUST_HOLDER = ATTACHMENT_TYPES.register("gust_holder", () -> AttachmentType.builder(() -> -1).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> URSA_FLURRY_TIME = savedInt("ursa_flurry_time", 0);
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> URSA_FLURRY_COOLDOWN = savedInt("ursa_flurry_cooldown", 0);
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> URSA_ENRAGE_TIME = syncedInt("ursa_enrage_time", 0);
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> ORIGIN_ID = savedInt("origin_id", 0);
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> SHOOT_COOLDOWN = savedInt("shoot_cooldown", 0);
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> SHOOT_SHOTS = savedInt("shoot_shots", 0);
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> POTION_LEVEL = savedInt("potion_level", 0);
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> URSA_ENRAGED = ATTACHMENT_TYPES.register("ursa_enraged", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL, value -> value).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<UUID>> CHILD_UUID = savedUUID("child_uuid");
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<UUID>> WORSHIPING_UUID = savedUUID("worshiping_uuid");
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Long>> SERVANT_GLOW_START = ATTACHMENT_TYPES.register("servant_glow_start", () -> AttachmentType.builder(() -> 0L).sync(ByteBufCodecs.VAR_LONG).build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> TRANSFORMING_TIME = syncedInt("transform_time", 0);
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> URSA = syncedBool("ursa");
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> IS_GOD = syncedBool("is_god");
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> RELAVA = syncedBool("relava");
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> COSMAW_WEAKENED = syncedBool("cosmaw_weakened");
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> DAY_SLEEPING = syncedBool("day_sleeping");
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> VARIANT = syncedInt("variant", 0);
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> TOTEM_INDEX = syncedInt("totem_index", -1);
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> STALK_DELAY = savedInt("stalk_delay", 0);
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> CONVERSION_TIME = savedInt("conversion_time", 0);
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> ENDERGRADE_BOOST = savedInt("endergrade_boost", 0);
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> WALLY = syncedBool("wally");
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> BLUE_KOOPA = syncedBool("blue_koopa");
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> SPINNING_SEAL = syncedBool("spinning_seal");
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> VOID_WORM_MODE = savedInt("void_worm_mode", 0);
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> SHATTER_TICKS = ATTACHMENT_TYPES.register("shatter_ticks", () -> AttachmentType.builder(() -> 0).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> SHATTER_SEGMENTS = ATTACHMENT_TYPES.register("shatter_segments", () -> AttachmentType.builder(() -> 0).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> SHATTER_HEAD_START = ATTACHMENT_TYPES.register("shatter_head_start", () -> AttachmentType.builder(() -> -1).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> BOSS_SCALED = ATTACHMENT_TYPES.register("boss_scaled", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL, value -> value).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> SPLIT_TICKS = savedInt("split_ticks", 0);
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> RECOMBINE_TICKS = savedInt("recombine_ticks", 0);
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> BARRAGE_SPLITTER = ATTACHMENT_TYPES.register("barrage_splitter", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL, value -> value).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> VOID_SHOT_MODE = syncedInt("void_shot_mode", 0);
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> VOID_WORM_FORCED_ATTACK = ATTACHMENT_TYPES.register("void_worm_forced_attack", () -> AttachmentType.builder(() -> -1).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> VOID_SHOT_LOST = ATTACHMENT_TYPES.register("void_shot_lost", () -> AttachmentType.builder(() -> false).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<UUID>> PORTAL_OWNER = savedUUID("portal_owner");
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> PORTAL_USED = ATTACHMENT_TYPES.register("portal_used", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL, value -> value).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Float>> STALK_TIME = ATTACHMENT_TYPES.register("stalk_time", () -> AttachmentType.builder(() -> 0F).serialize(Codec.FLOAT, value -> value != 0F).sync((holder, player) -> holder == player, ByteBufCodecs.FLOAT).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> ALTER_TIME = ATTACHMENT_TYPES.register("alter_time", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT, value -> value != 0).sync((holder, player) -> holder == player, ByteBufCodecs.VAR_INT).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<String>> POTION_ID = ATTACHMENT_TYPES.register("potion_id", () -> AttachmentType.builder(() -> "").serialize(Codec.STRING, value -> !value.isEmpty()).sync(ByteBufCodecs.STRING_UTF8).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<String>> PORTAL_DIMENSION = ATTACHMENT_TYPES.register("portal_dimension", () -> AttachmentType.builder(() -> "").serialize(Codec.STRING, value -> !value.isEmpty()).sync(ByteBufCodecs.STRING_UTF8).build());
    public static final long NO_BLOCK = Long.MIN_VALUE;
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Long>> BONEMEAL_TARGET = ATTACHMENT_TYPES.register("bonemeal_target", () -> AttachmentType.builder(() -> NO_BLOCK).sync(ByteBufCodecs.VAR_LONG).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Long>> POLLINATE_TARGET = ATTACHMENT_TYPES.register("pollinate_target", () -> AttachmentType.builder(() -> NO_BLOCK).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> POLLINATE_COOLDOWN = ATTACHMENT_TYPES.register("pollinate_cooldown", () -> AttachmentType.builder(() -> 0).build());

    public static <T> void setIfChanged(IAttachmentHolder holder, Supplier<AttachmentType<T>> type, T value) {
        if (!Objects.equals(holder.getData(type), value)) {
            holder.setData(type, value);
        }
    }

    @Nullable
    public static UUID getUUID(IAttachmentHolder holder, Supplier<AttachmentType<UUID>> type) {
        return holder.getExistingData(type).filter(uuid -> !Util.NIL_UUID.equals(uuid)).orElse(null);
    }

    public static void setUUID(IAttachmentHolder holder, Supplier<AttachmentType<UUID>> type, @Nullable UUID uuid) {
        if (uuid == null) {
            holder.removeData(type);
        } else {
            holder.setData(type, uuid);
        }
    }

    private static DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> savedInt(String name, int defaultValue) {
        return ATTACHMENT_TYPES.register(name, () -> AttachmentType.builder(() -> defaultValue).serialize(Codec.INT, value -> value != defaultValue).build());
    }

    private static DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> syncedInt(String name, int defaultValue) {
        return ATTACHMENT_TYPES.register(name, () -> AttachmentType.builder(() -> defaultValue).serialize(Codec.INT, value -> value != defaultValue).sync(ByteBufCodecs.VAR_INT).build());
    }

    private static DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> syncedBool(String name) {
        return ATTACHMENT_TYPES.register(name, () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL, value -> value).sync(ByteBufCodecs.BOOL).build());
    }

    private static DeferredHolder<AttachmentType<?>, AttachmentType<UUID>> savedUUID(String name) {
        Supplier<UUID> nil = () -> Util.NIL_UUID;
        return ATTACHMENT_TYPES.register(name, () -> AttachmentType.builder(nil).serialize(UUIDUtil.CODEC, value -> !Util.NIL_UUID.equals(value)).build());
    }
}
