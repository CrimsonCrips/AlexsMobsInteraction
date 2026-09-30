package com.crimsoncrips.alexsmobsinteraction;

import com.crimsoncrips.alexsmobsinteraction.client.AMIClientConfig;
import com.crimsoncrips.alexsmobsinteraction.client.AMISoundRegistry;
import com.crimsoncrips.alexsmobsinteraction.datagen.AMIDatagen;
import com.crimsoncrips.alexsmobsinteraction.datagen.loottables.AMILootModifiers;
import com.crimsoncrips.alexsmobsinteraction.networking.AMIPacketHandler;
import com.crimsoncrips.alexsmobsinteraction.server.*;
import com.crimsoncrips.alexsmobsinteraction.server.effect.AMIEffects;
import com.crimsoncrips.alexsmobsinteraction.server.entity.AMIEntityRegistry;
import com.crimsoncrips.alexsmobsinteraction.server.item.AMIItemRegistry;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Locale;

@Mod(AlexsMobsInteraction.MODID)
public class AlexsMobsInteraction {

    public static final String MODID = "alexsmobsinteraction";
    public static final AMICommonProxy PROXY = FMLEnvironment.dist.isClient() ? new AMIClientProxy() : new AMICommonProxy();

    public static final AMIServerConfig COMMON_CONFIG;
    public static final ModConfigSpec COMMON_CONFIG_SPEC;

    public static final AMIAddTargetsConfig TARGETS_CONFIG;
    public static final ModConfigSpec TARGETS_CONFIG_SPEC;

    public static final AMIClientConfig CLIENT_CONFIG;
    public static final ModConfigSpec CLIENT_CONFIG_SPEC;

    static {
        final Pair<AMIServerConfig, ModConfigSpec> serverPair = new ModConfigSpec.Builder().configure(AMIServerConfig::new);
        COMMON_CONFIG = serverPair.getLeft();
        COMMON_CONFIG_SPEC = serverPair.getRight();
        final Pair<AMIAddTargetsConfig, ModConfigSpec> targetPair = new ModConfigSpec.Builder().configure(AMIAddTargetsConfig::new);
        TARGETS_CONFIG = targetPair.getLeft();
        TARGETS_CONFIG_SPEC = targetPair.getRight();
        final Pair<AMIClientConfig, ModConfigSpec> clientPair = new ModConfigSpec.Builder().configure(AMIClientConfig::new);
        CLIENT_CONFIG = clientPair.getLeft();
        CLIENT_CONFIG_SPEC = clientPair.getRight();
    }

    public AlexsMobsInteraction(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, COMMON_CONFIG_SPEC, "alexsmobsinteraction-general.toml");
        modContainer.registerConfig(ModConfig.Type.COMMON, TARGETS_CONFIG_SPEC, "alexsmobsinteraction-add_targets.toml");
        modContainer.registerConfig(ModConfig.Type.CLIENT, CLIENT_CONFIG_SPEC, "alexsmobsinteraction-client.toml");

        NeoForge.EVENT_BUS.register(new AMIServerEvents());
        NeoForge.EVENT_BUS.addListener(AMIEffects::registerBrewingRecipes);
        NeoForge.EVENT_BUS.addListener(AMIAddTargets::onEntityJoinLevel);
        NeoForge.EVENT_BUS.addListener(AMIAddGoals::onEntityJoinLevel);


        AMIEffects.EFFECT_REGISTER.register(modEventBus);
        AMIEffects.POTION_REGISTER.register(modEventBus);
        AMIItemRegistry.DEF_REG.register(modEventBus);
        AMISoundRegistry.DEF_REG.register(modEventBus);
        AMILootModifiers.LOOT_MODIFIERS.register(modEventBus);
        AMIEntityRegistry.DEF_REG.register(modEventBus);
        AMIAttachments.ATTACHMENT_TYPES.register(modEventBus);
        modEventBus.addListener(AMIDatagen::generateData);
        modEventBus.addListener(AMIModEvents::addCreativeTabs);
        modEventBus.addListener(AMIModEvents::registerSpawnPlacements);
        modEventBus.addListener(AMIPacketHandler::register);
        modEventBus.addListener(this::setupClient);
        PROXY.init(modEventBus);
        if (FMLEnvironment.dist.isClient()) {
            AMIClientProxy.registerConfigScreen(modContainer);
        }
    }

    private void setupClient(FMLClientSetupEvent event) {
        event.enqueueWork(PROXY::clientInit);
    }

    public static ResourceLocation prefix(String name) {
        return ResourceLocation.fromNamespaceAndPath(MODID, name.toLowerCase(Locale.ROOT));
    }

    public static void sendNonLocal(CustomPacketPayload payload, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, payload);
    }
}
