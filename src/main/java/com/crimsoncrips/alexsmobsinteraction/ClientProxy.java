package com.crimsoncrips.alexsmobsinteraction;

import com.crimsoncrips.alexsmobsinteraction.mobmodification.mobrendering.AIFrogRendering;

import com.github.alexthe666.alexsmobs.CommonProxy;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = AlexsMobsInteraction.MODID, value = Dist.CLIENT)
public class ClientProxy extends CommonProxy {


    public void clientInit() {
        EntityRenderers.register(EntityType.FROG, AIFrogRendering::new);
    }


}
