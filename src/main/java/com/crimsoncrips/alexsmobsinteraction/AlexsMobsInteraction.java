package com.crimsoncrips.alexsmobsinteraction;

import com.crimsoncrips.alexsmobsinteraction.client.renderer.AMIRendering;
import com.crimsoncrips.alexsmobsinteraction.config.AMIConfigHolder;
import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.crimsoncrips.alexsmobsinteraction.enchantment.AMIEnchantmentRegistry;
import com.crimsoncrips.alexsmobsinteraction.event.AMInteractionEvents;
import com.crimsoncrips.alexsmobsinteraction.item.AMIItemRegistry;
import com.crimsoncrips.alexsmobsinteraction.networking.AMIPacketHandler;
import com.mojang.logging.LogUtils;
import misc.AMICreativeTab;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.util.Locale;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(AlexsMobsInteraction.MODID)
public class AlexsMobsInteraction {




    // Define mod id in a common place for everything to reference
    public static final String MODID = "alexsmobsinteraction";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "alexsmobsinteraction" namespace

    public AlexsMobsInteraction() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        AMIItemRegistry.DEF_REG.register(modEventBus);
        AMICreativeTab.DEF_REG.register(modEventBus);
        AMIEnchantmentRegistry.DEF_REG.register(modEventBus);
        modEventBus.addListener(this::onModConfigEvent);
        MinecraftForge.EVENT_BUS.register(new AMInteractionEvents());
        MinecraftForge.EVENT_BUS.register(this);

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


    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            LOGGER.info("THE CRIMSON SHALL GUIDE YOU.");
            LOGGER.info("CRIMSON HYPHAE >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.CRIMSON_HYPHAE));
        }
    }

    public static ResourceLocation prefix(String name) {
        return new ResourceLocation(MODID, name.toLowerCase(Locale.ROOT));
    }
}
