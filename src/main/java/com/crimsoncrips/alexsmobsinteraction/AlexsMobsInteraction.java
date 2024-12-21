package com.crimsoncrips.alexsmobsinteraction;

import com.crimsoncrips.alexsmobsinteraction.client.event.AMIClientEvents;
import com.crimsoncrips.alexsmobsinteraction.config.AMIConfigHolder;
import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.crimsoncrips.alexsmobsinteraction.effect.AMIEffects;
import com.crimsoncrips.alexsmobsinteraction.enchantment.AMIEnchantmentRegistry;
import com.crimsoncrips.alexsmobsinteraction.entity.AMIEntityRegistry;
import com.crimsoncrips.alexsmobsinteraction.event.AMInteractionEvents;
import com.crimsoncrips.alexsmobsinteraction.item.AMIItemRegistry;
import com.crimsoncrips.alexsmobsinteraction.misc.CrimsonAdvancementTriggerRegistry;
import com.crimsoncrips.alexsmobsinteraction.networking.AMIPacketHandler;
import com.github.alexthe666.alexsmobs.ClientProxy;
import com.github.alexthe666.alexsmobs.CommonProxy;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.message.*;
import com.github.alexthe666.alexsmobs.misc.AMAdvancementTriggerRegistry;
import com.github.alexthe666.alexsmobs.misc.AMRecipeRegistry;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
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
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.util.Locale;

@Mod(AlexsMobsInteraction.MODID)
public class AlexsMobsInteraction {

    public static final String MODID = "alexsmobsinteraction";
    public static final AMICommonProxy PROXY = DistExecutor.runForDist(() -> AMIClientProxy::new, () -> AMICommonProxy::new);

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
