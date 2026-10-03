package com.crimsoncrips.alexsmobsinteraction;

import com.crimsoncrips.alexsmobsinteraction.client.renderer.RenderVoidWormDummy;
import com.crimsoncrips.alexsmobsinteraction.client.renderer.RenderFarseerPortal;
import com.crimsoncrips.alexsmobsinteraction.client.AMIClientEvents;
import com.crimsoncrips.alexsmobsinteraction.client.AMIKeyMappings;
import com.crimsoncrips.alexsmobsinteraction.client.renderer.AscenderItemRenderer;
import com.crimsoncrips.alexsmobsinteraction.client.AMIShaders;
import com.crimsoncrips.alexsmobsinteraction.client.screen.AMIConfigScreen;
import com.crimsoncrips.alexsmobsinteraction.server.entity.AMIEntityRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AMIClientProxy extends AMICommonProxy {

    public static Map<UUID, Integer> bossBarRenderTypes = new HashMap<>();

    public void init(IEventBus modEventBus) {
        NeoForge.EVENT_BUS.register(new AMIClientEvents());
        AMIShaders.init(modEventBus);
        modEventBus.addListener(AMIKeyMappings::register);
        modEventBus.addListener(AscenderItemRenderer::registerModels);
        modEventBus.addListener(AscenderItemRenderer::registerClientExtensions);
        NeoForge.EVENT_BUS.addListener(AMIKeyMappings::onClientTick);
    }

    public static void registerConfigScreen(ModContainer modContainer) {
        modContainer.registerExtensionPoint(IConfigScreenFactory.class,
                (IConfigScreenFactory) (container, modListScreen) -> new AMIConfigScreen(modListScreen));
    }

    public void clientInit() {
        EntityRenderers.register(AMIEntityRegistry.FARSEER_PORTAL.get(), RenderFarseerPortal::new);
        EntityRenderers.register(AMIEntityRegistry.VOID_WORM_DUMMY.get(), RenderVoidWormDummy::new);
        EntityRenderers.register(AMIEntityRegistry.LEAFCUTTER_PUPA.get(), (render) -> {
            return new ThrownItemRenderer<>(render, 0.75F, true);
        });
    }

    public Player getClientSidePlayer() {
        return Minecraft.getInstance().player;
    }

    public void removeBossBarRender(UUID bossBar) {
        bossBarRenderTypes.remove(bossBar);
    }

    public void setBossBarRender(UUID bossBar, int renderType) {
        bossBarRenderTypes.put(bossBar, renderType);
    }
}
