package com.crimsoncrips.alexsmobsinteraction.compat;

import net.minecraft.world.item.Items;
import com.github.alexthe666.alexsmobs.entity.EntityFlutter;
import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import com.crimsoncrips.alexsmobsinteraction.server.goal.AMIMantisMine;
import com.github.alexthe666.alexsmobs.entity.EntityMantisShrimp;
import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.server.AMIAttachments;
import com.github.alexthe666.alexsmobs.entity.EntityBaldEagle;
import com.github.alexthe666.alexsmobs.entity.EntityCapuchinMonkey;
import com.github.alexthe666.alexsmobs.entity.EntityStraddler;
import net.minecraft.util.Mth;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import com.github.alexthe666.alexsmobs.entity.EntityBoneSerpent;
import com.github.alexthe666.alexsmobs.entity.EntityBoneSerpentPart;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElementHelper;

import java.util.List;

@WailaPlugin
public class JadeCompat implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerEntityDataProvider(BoneSerpentShield.INSTANCE, EntityBoneSerpent.class);
        registration.registerEntityDataProvider(BoneSerpentShield.INSTANCE, EntityBoneSerpentPart.class);
        registration.registerEntityDataProvider(MantisMining.INSTANCE, EntityMantisShrimp.class);
        registration.registerEntityDataProvider(CapuchinDart.INSTANCE, EntityCapuchinMonkey.class);
        registration.registerEntityDataProvider(StraddlerAmmo.INSTANCE, EntityStraddler.class);
        registration.registerEntityDataProvider(FlutterPollination.INSTANCE, EntityFlutter.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerEntityComponent(EagleCarrying.INSTANCE, EntityBaldEagle.class);
        registration.registerEntityComponent(BoneSerpentShield.INSTANCE, EntityBoneSerpent.class);
        registration.registerEntityComponent(BoneSerpentShield.INSTANCE, EntityBoneSerpentPart.class);
        registration.registerEntityComponent(MantisMining.INSTANCE, EntityMantisShrimp.class);
        registration.registerEntityComponent(CapuchinDart.INSTANCE, EntityCapuchinMonkey.class);
        registration.registerEntityComponent(StraddlerAmmo.INSTANCE, EntityStraddler.class);
        registration.registerEntityComponent(FlutterPollination.INSTANCE, EntityFlutter.class);
    }

    public enum EagleCarrying implements IEntityComponentProvider {
        INSTANCE;

        private static final ResourceLocation UID = AlexsMobsInteraction.prefix("eagle_carrying");

        @Override
        public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
            ItemStack carried = ((EntityBaldEagle) accessor.getEntity()).getItemInHand(InteractionHand.MAIN_HAND);
            if (carried.isEmpty())
                return;
            IElementHelper helper = IElementHelper.get();
            tooltip.add(List.of(
                    helper.item(carried, 0.75F),
                    helper.text(Component.translatable("misc.alexsmobsinteraction.jade.eagle_carrying", carried.getHoverName()))
            ));
        }

        @Override
        public ResourceLocation getUid() {
            return UID;
        }
    }

    public enum BoneSerpentShield implements IEntityComponentProvider, IServerDataProvider<EntityAccessor> {
        INSTANCE;

        private static final ResourceLocation UID = AlexsMobsInteraction.prefix("bone_serpent_shield");
        private static final String HITS_KEY = "ShieldHits";
        private static final int MAX_SEGMENTS = 256;

        @Override
        public void appendServerData(CompoundTag data, EntityAccessor accessor) {
            if (!AlexsMobsInteraction.COMMON_CONFIG.BODY_SHIELDING_ENABLED.get())
                return;
            EntityBoneSerpent head = findHead(accessor.getEntity());
            if (head != null) {
                data.putInt(HITS_KEY, hitsUntilExposed(head));
            }
        }

        @Override
        public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
            CompoundTag data = accessor.getServerData();
            if (!data.contains(HITS_KEY))
                return;
            int hits = data.getInt(HITS_KEY);
            tooltip.add(hits > 0
                    ? Component.translatable("misc.alexsmobsinteraction.jade.bone_serpent_shielded", hits)
                    : Component.translatable("misc.alexsmobsinteraction.jade.bone_serpent_exposed"));
        }

        @Override
        public ResourceLocation getUid() {
            return UID;
        }

        private static EntityBoneSerpent findHead(Entity entity) {
            for (int i = 0; i < MAX_SEGMENTS && entity != null; i++) {
                if (entity instanceof EntityBoneSerpent head)
                    return head;
                entity = entity instanceof EntityBoneSerpentPart part ? part.getParent() : null;
            }
            return null;
        }

        private static int hitsUntilExposed(EntityBoneSerpent head) {
            int segments = 0;
            Entity segment = head.getChild();
            while (segment instanceof EntityBoneSerpentPart part && segments < MAX_SEGMENTS) {
                segments++;
                segment = AMIUtils.getBoneChild(part);
            }
            return Math.max(0, segments - 2);
        }
    }

    public enum MantisMining implements IEntityComponentProvider, IServerDataProvider<EntityAccessor> {
        INSTANCE;

        private static final ResourceLocation UID = AlexsMobsInteraction.prefix("mantis_mining");
        private static final String COOLDOWN_KEY = "MineCooldown";
        private static final String MINING_KEY = "Mining";

        @Override
        public void appendServerData(CompoundTag data, EntityAccessor accessor) {
            EntityMantisShrimp mantisShrimp = (EntityMantisShrimp) accessor.getEntity();
            for (WrappedGoal wrapped : mantisShrimp.goalSelector.getAvailableGoals()) {
                if (wrapped.getGoal() instanceof AMIMantisMine mine) {
                    data.putInt(COOLDOWN_KEY, mine.getCooldown());
                    data.putBoolean(MINING_KEY, mine.isMining());
                    return;
                }
            }
        }

        @Override
        public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
            EntityMantisShrimp mantisShrimp = (EntityMantisShrimp) accessor.getEntity();
            ItemStack held = mantisShrimp.getMainHandItem();
            if (held.isEmpty())
                return;
            IElementHelper helper = IElementHelper.get();
            tooltip.add(List.of(
                    helper.item(held, 0.75F),
                    helper.text(Component.translatable("misc.alexsmobsinteraction.jade.holding", held.getHoverName()))
            ));
            CompoundTag data = accessor.getServerData();
            if (!(held.getItem() instanceof BlockItem) || !data.contains(COOLDOWN_KEY))
                return;
            int cooldown = data.getInt(COOLDOWN_KEY);
            if (data.getBoolean(MINING_KEY)) {
                tooltip.add(Component.translatable("misc.alexsmobsinteraction.jade.mantis_breaking"));
            } else if (cooldown > 0) {
                tooltip.add(Component.translatable("misc.alexsmobsinteraction.jade.mantis_cooldown", String.format("%.1f", cooldown / 20.0F)));
            } else {
                tooltip.add(Component.translatable("misc.alexsmobsinteraction.jade.mantis_ready"));
            }
        }

        @Override
        public ResourceLocation getUid() {
            return UID;
        }
    }

    public enum CapuchinDart implements IEntityComponentProvider, IServerDataProvider<EntityAccessor> {
        INSTANCE;

        private static final ResourceLocation UID = AlexsMobsInteraction.prefix("capuchin_dart");
        private static final String EFFECT_KEY = "DartEffect";
        private static final String AMPLIFIER_KEY = "DartAmplifier";
        private static final int DART_EFFECT_SECONDS = 5;

        @Override
        public void appendServerData(CompoundTag data, EntityAccessor accessor) {
            EntityCapuchinMonkey capuchin = (EntityCapuchinMonkey) accessor.getEntity();
            String effectId = capuchin.getData(AMIAttachments.POTION_ID);
            if (AlexsMobsInteraction.COMMON_CONFIG.DART_EFFECTS_ENABLED.get() && capuchin.hasDart() && !effectId.isEmpty()) {
                data.putString(EFFECT_KEY, effectId);
                data.putInt(AMPLIFIER_KEY, capuchin.getData(AMIAttachments.POTION_LEVEL));
            }
        }

        @Override
        public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
            CompoundTag data = accessor.getServerData();
            if (!data.contains(EFFECT_KEY))
                return;
            ResourceLocation effectId = ResourceLocation.tryParse(data.getString(EFFECT_KEY));
            MobEffect effect = effectId == null ? null : BuiltInRegistries.MOB_EFFECT.get(effectId);
            if (effect == null)
                return;
            int amplifier = data.getInt(AMPLIFIER_KEY);
            MutableComponent effectName = Component.translatable(effect.getDescriptionId());
            if (amplifier > 0) {
                effectName.append(CommonComponents.SPACE).append(Component.translatable("potion.potency." + amplifier));
            }
            IElementHelper helper = IElementHelper.get();
            tooltip.add(List.of(
                    helper.item(new ItemStack(AMItemRegistry.ANCIENT_DART.get()), 0.75F),
                    helper.text(Component.translatable("misc.alexsmobsinteraction.jade.capuchin_dart", effectName.withStyle(effect.getCategory().getTooltipFormatting()), DART_EFFECT_SECONDS))
            ));
        }

        @Override
        public ResourceLocation getUid() {
            return UID;
        }
    }

    public enum StraddlerAmmo implements IEntityComponentProvider, IServerDataProvider<EntityAccessor> {
        INSTANCE;

        private static final ResourceLocation UID = AlexsMobsInteraction.prefix("straddler_ammo");
        private static final String SHOTS_KEY = "StraddlerShots";
        private static final String MAX_SHOTS_KEY = "StraddlerMaxShots";
        private static final String COOLDOWN_KEY = "StraddlerCooldown";

        @Override
        public void appendServerData(CompoundTag data, EntityAccessor accessor) {
            int maxShots = AlexsMobsInteraction.COMMON_CONFIG.STRADDLER_SHOTS_AMOUNT.get();
            if (maxShots == 0)
                return;
            EntityStraddler straddler = (EntityStraddler) accessor.getEntity();
            data.putInt(SHOTS_KEY, straddler.getData(AMIAttachments.SHOOT_SHOTS));
            data.putInt(MAX_SHOTS_KEY, maxShots);
            data.putInt(COOLDOWN_KEY, straddler.getData(AMIAttachments.SHOOT_COOLDOWN));
        }

        @Override
        public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
            CompoundTag data = accessor.getServerData();
            if (!data.contains(MAX_SHOTS_KEY))
                return;
            int shots = data.getInt(SHOTS_KEY);
            if (shots > 0) {
                tooltip.add(Component.translatable("misc.alexsmobsinteraction.jade.straddler_ammo", shots, data.getInt(MAX_SHOTS_KEY)));
            } else {
                tooltip.add(Component.translatable("misc.alexsmobsinteraction.jade.straddler_reloading", Mth.ceil(Math.max(0, data.getInt(COOLDOWN_KEY)) / 20F)));
            }
        }

        @Override
        public ResourceLocation getUid() {
            return UID;
        }
    }

    public enum FlutterPollination implements IEntityComponentProvider, IServerDataProvider<EntityAccessor> {
        INSTANCE;

        private static final ResourceLocation UID = AlexsMobsInteraction.prefix("flutter_pollination");
        private static final String COOLDOWN_KEY = "PollinateCooldown";
        private static final String PENDING_KEY = "PollinatePending";

        @Override
        public void appendServerData(CompoundTag data, EntityAccessor accessor) {
            EntityFlutter flutter = (EntityFlutter) accessor.getEntity();
            if (!AlexsMobsInteraction.COMMON_CONFIG.FLUTTER_POLLINATION_ENABLED.get() || !flutter.isTame())
                return;
            data.putInt(COOLDOWN_KEY, flutter.getData(AMIAttachments.POLLINATE_COOLDOWN));
            data.putBoolean(PENDING_KEY, flutter.getData(AMIAttachments.POLLINATE_TARGET) != AMIAttachments.NO_BLOCK);
        }

        @Override
        public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
            CompoundTag data = accessor.getServerData();
            if (!data.contains(COOLDOWN_KEY))
                return;
            Component text = data.getBoolean(PENDING_KEY) || data.getInt(COOLDOWN_KEY) <= 0
                    ? Component.translatable("misc.alexsmobsinteraction.jade.flutter_pollinating")
                    : Component.translatable("misc.alexsmobsinteraction.jade.flutter_pollination", Mth.ceil(data.getInt(COOLDOWN_KEY) / 20F));
            IElementHelper helper = IElementHelper.get();
            tooltip.add(List.of(helper.item(new ItemStack(Items.BONE_MEAL), 0.75F), helper.text(text)));
        }

        @Override
        public ResourceLocation getUid() {
            return UID;
        }
    }
}
