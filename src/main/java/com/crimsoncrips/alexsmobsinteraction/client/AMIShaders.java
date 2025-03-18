package com.crimsoncrips.alexsmobsinteraction.client;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.IEventBus;

import java.io.IOException;
import java.util.function.Consumer;

public class AMIShaders {

	public static ShaderInstance FARSEER_EFFECTS;

	public static void init(IEventBus bus) {
		bus.addListener((Consumer<RegisterShadersEvent>) event -> {
			try {
				event.registerShader(new ShaderInstance(event.getResourceProvider(), AlexsMobsInteraction.prefix("farseer_effects/farseer_effects"), DefaultVertexFormat.BLOCK),
						shader -> FARSEER_EFFECTS = shader);

			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

}
