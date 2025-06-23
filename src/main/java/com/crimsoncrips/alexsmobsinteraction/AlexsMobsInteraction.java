package com.crimsoncrips.alexsmobsinteraction;

import com.crimsoncrips.alexsmobsinteraction.client.AMIClientConfig;
import com.crimsoncrips.alexsmobsinteraction.client.AMIShaders;
import com.crimsoncrips.alexsmobsinteraction.client.AMISoundRegistry;
import com.crimsoncrips.alexsmobsinteraction.datagen.AMIDatagen;
import com.crimsoncrips.alexsmobsinteraction.message.UrsaUpdateBossBarMessage;
import com.crimsoncrips.alexsmobsinteraction.server.AMIServerConfig;
import com.crimsoncrips.alexsmobsinteraction.server.effect.AMIEffects;
import com.crimsoncrips.alexsmobsinteraction.server.enchantment.AMIEnchantmentRegistry;
import com.crimsoncrips.alexsmobsinteraction.server.entity.AMIEntityRegistry;
import com.crimsoncrips.alexsmobsinteraction.server.AMInteractionEvents;
import com.crimsoncrips.alexsmobsinteraction.server.item.AMIItemRegistry;
import com.crimsoncrips.alexsmobsinteraction.networking.AMIPacketHandler;
import com.github.alexmodguy.alexscaves.server.message.UpdateBossBarMessage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.Locale;

@SuppressWarnings("deprecation")
@Mod(AlexsMobsInteraction.MODID)
public class AlexsMobsInteraction {

    public static final String MODID = "alexsmobsinteraction";
    public static final AMICommonProxy PROXY = DistExecutor.runForDist(() -> AMIClientProxy::new, () -> AMICommonProxy::new);

    public static final AMIServerConfig COMMON_CONFIG;
    private static final ForgeConfigSpec COMMON_CONFIG_SPEC;
    public static final AMIClientConfig CLIENT_CONFIG;
    private static final ForgeConfigSpec CLIENT_CONFIG_SPEC;

    private static final String PROTOCOL_VERSION = Integer.toString(1);
    private static final ResourceLocation PACKET_NETWORK_NAME = new ResourceLocation("alexsmobsinteraction:main_channel");
    public static final SimpleChannel NETWORK_WRAPPER = NetworkRegistry.ChannelBuilder
            .named(PACKET_NETWORK_NAME)
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();

    static {
        final Pair<AMIServerConfig, ForgeConfigSpec> serverPair = new ForgeConfigSpec.Builder().configure(AMIServerConfig::new);
        COMMON_CONFIG = serverPair.getLeft();
        COMMON_CONFIG_SPEC = serverPair.getRight();
        final Pair<AMIClientConfig, ForgeConfigSpec> clientPair = new ForgeConfigSpec.Builder().configure(AMIClientConfig::new);
        CLIENT_CONFIG = clientPair.getLeft();
        CLIENT_CONFIG_SPEC = clientPair.getRight();
    }

    public AlexsMobsInteraction() throws IOException {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_CONFIG_SPEC, "alexsmobsinteraction-general.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_CONFIG_SPEC, "alexsmobsinteraction-client.toml");

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        AMIEnchantmentRegistry.DEF_REG.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(new AMInteractionEvents());
        MinecraftForge.EVENT_BUS.register(this);
        AMIEffects.EFFECT_REGISTER.register(modEventBus);
        AMIEffects.POTION_REGISTER.register(modEventBus);
        AMIItemRegistry.DEF_REG.register(modEventBus);
        AMISoundRegistry.DEF_REG.register(modEventBus);
        modEventBus.addListener(AMIDatagen::generateData);

        PROXY.init();
        modEventBus.addListener(this::setupClient);
        AMIEntityRegistry.DEF_REG.register(modEventBus);

        modEventBus.addListener(this::setup);

        AMIPacketHandler.init();
    }

    private void setup(final FMLCommonSetupEvent event) {
        int packetsRegistered = 0;
        NETWORK_WRAPPER.registerMessage(packetsRegistered++, UrsaUpdateBossBarMessage.class, UrsaUpdateBossBarMessage::write, UrsaUpdateBossBarMessage::read, UrsaUpdateBossBarMessage::handle);

        AMIEffects.init();
    }

    private void setupClient(FMLClientSetupEvent event) {
        event.enqueueWork(PROXY::clientInit);
        IEventBus busMod = FMLJavaModLoadingContext.get().getModEventBus();
        AMIShaders.init(busMod);
    }


    public static ResourceLocation prefix(String name) {
        return new ResourceLocation(MODID, name.toLowerCase(Locale.ROOT));
    }

    public static <MSG> void sendNonLocal(MSG msg, ServerPlayer player) {
        NETWORK_WRAPPER.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }
}
