package com.crimsoncrips.alexsmobsinteraction;

import com.crimsoncrips.alexsmobsinteraction.client.AMIClientEvents;
import com.crimsoncrips.alexsmobsinteraction.server.entity.AMIEntityRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = AlexsMobsInteraction.MODID, value = Dist.CLIENT)
public class AMIClientProxy extends AMICommonProxy {

    public static Map<UUID, Integer> bossBarRenderTypes = new HashMap<>();

    public void init() {
        MinecraftForge.EVENT_BUS.register(new AMIClientEvents());
    }
    public void clientInit() {
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
