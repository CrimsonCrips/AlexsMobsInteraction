package com.crimsoncrips.alexsmobsinteraction;

import com.crimsoncrips.alexsmobsinteraction.client.event.AMIClientEvents;
import com.crimsoncrips.alexsmobsinteraction.server.entity.AMIEntityRegistry;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = AlexsMobsInteraction.MODID, value = Dist.CLIENT)
public class AMIClientProxy extends AMICommonProxy {

    public void init() {
        MinecraftForge.EVENT_BUS.register(new AMIClientEvents());
    }
    public void clientInit() {
        EntityRenderers.register(AMIEntityRegistry.LEAFCUTTER_PUPA.get(), (render) -> {
            return new ThrownItemRenderer<>(render, 0.75F, true);
        });
    }



    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onRegisterEntityRenders(EntityRenderersEvent.RegisterLayerDefinitions event) {
    }


}
