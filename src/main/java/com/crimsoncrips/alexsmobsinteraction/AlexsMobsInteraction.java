package com.crimsoncrips.alexsmobsinteraction;

import com.crimsoncrips.alexsmobsinteraction.config.AInteractionConfig;
import com.crimsoncrips.alexsmobsinteraction.config.ConfigHolder;
import com.crimsoncrips.alexsmobsinteraction.enchantment.AIEnchantmentRegistry;
import com.crimsoncrips.alexsmobsinteraction.event.InteractionEvents;
import com.crimsoncrips.alexsmobsinteraction.item.AIItemRegistry;
import com.mojang.logging.LogUtils;
import misc.AICreativeTab;
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
        AIItemRegistry.DEF_REG.register(modEventBus);
        AICreativeTab.DEF_REG.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(new InteractionEvents());
        AIEnchantmentRegistry.DEF_REG.register(modEventBus);
        modEventBus.addListener(this::onModConfigEvent);
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);


        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHolder.INTERACT_SPEC, "alexsinteraction.toml");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("ENEMIES OF THE CRIMSON SHALL BURN.");
        LOGGER.info("CRIMSON HYPHAE >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.CRIMSON_HYPHAE));

    }

    @SubscribeEvent
    public void onModConfigEvent(final ModConfigEvent event) {
        final ModConfig config = event.getConfig();
        // Rebake the configs when they change
        if (config.getSpec() == ConfigHolder.INTERACT_SPEC) {
            AInteractionConfig.bake();
        }
    }


    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("SUBMIT YOURSELF.");
        LOGGER.info("CRIMSON HYPHAE >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.CRIMSON_HYPHAE));
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
}
