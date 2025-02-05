package com.crimsoncrips.alexsmobsinteraction;

import com.crimsoncrips.alexsmobsinteraction.client.event.AMIClientEvents;
import com.crimsoncrips.alexsmobsinteraction.config.AMIConfigHolder;
import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.crimsoncrips.alexsmobsinteraction.server.effect.AMIEffects;
import com.crimsoncrips.alexsmobsinteraction.server.enchantment.AMIEnchantmentRegistry;
import com.crimsoncrips.alexsmobsinteraction.server.entity.AMIEntityRegistry;
import com.crimsoncrips.alexsmobsinteraction.server.AMInteractionEvents;
import com.crimsoncrips.alexsmobsinteraction.server.item.AMIItemRegistry;
import com.crimsoncrips.alexsmobsinteraction.misc.CrimsonAdvancementTriggerRegistry;
import com.crimsoncrips.alexsmobsinteraction.networking.AMIPacketHandler;
import com.github.alexmodguy.alexscaves.client.config.ACClientConfig;
import com.github.alexmodguy.alexscaves.server.config.ACServerConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Mod(AlexsMobsInteraction.MODID)
public class AlexsMobsInteraction {

    public static final String MODID = "alexsmobsinteraction";
    public static final AMICommonProxy PROXY = DistExecutor.runForDist(() -> AMIClientProxy::new, () -> AMICommonProxy::new);

    public static final ACServerConfig COMMON_CONFIG;
    private static final ForgeConfigSpec COMMON_CONFIG_SPEC;
    public static final ACClientConfig CLIENT_CONFIG;
    private static final ForgeConfigSpec CLIENT_CONFIG_SPEC;

    static {
        final Pair<ACServerConfig, ForgeConfigSpec> serverPair = new ForgeConfigSpec.Builder().configure(ACServerConfig::new);
        COMMON_CONFIG = serverPair.getLeft();
        COMMON_CONFIG_SPEC = serverPair.getRight();
        final Pair<ACClientConfig, ForgeConfigSpec> clientPair = new ForgeConfigSpec.Builder().configure(ACClientConfig::new);
        CLIENT_CONFIG = clientPair.getLeft();
        CLIENT_CONFIG_SPEC = clientPair.getRight();
    }

    public AlexsMobsInteraction() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::onModConfigEvent);
        AMIEnchantmentRegistry.DEF_REG.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(new AMInteractionEvents());
        MinecraftForge.EVENT_BUS.register(this);
        AMIEffects.EFFECT_REGISTER.register(modEventBus);
        AMIEffects.POTION_REGISTER.register(modEventBus);
        AMIItemRegistry.DEF_REG.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(new AMIClientEvents());
        PROXY.init();
        modEventBus.addListener(this::setupClient);
        AMIEntityRegistry.DEF_REG.register(modEventBus);

        modEventBus.addListener(this::setup);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, AMIConfigHolder.INTERACT_SPEC, "alexsmobsinteraction.toml");

        AMIPacketHandler.init();
    }



    @SubscribeEvent
    public void onModConfigEvent(final ModConfigEvent event) {
        final ModConfig config = event.getConfig();
        // Rebake the configs when they change
        if (config.getSpec() == AMIConfigHolder.INTERACT_SPEC) {
            AMInteractionConfig.bake();
        }
    }

    private void setup(final FMLCommonSetupEvent event) {
        AMIEffects.init();
        CrimsonAdvancementTriggerRegistry.init();
    }

    private void setupClient(FMLClientSetupEvent event) {
        event.enqueueWork(PROXY::clientInit);
    }


    public static ResourceLocation prefix(String name) {
        return new ResourceLocation(MODID, name.toLowerCase(Locale.ROOT));
    }
}
