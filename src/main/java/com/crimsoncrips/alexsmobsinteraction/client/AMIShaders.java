package com.crimsoncrips.alexsmobsinteraction.client;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import java.io.IOException;

public class AMIShaders {

	public static ShaderInstance FARSEER_EFFECTS;

	public static void init(IEventBus bus) {
		bus.addListener(AMIShaders::registerShaders);
	}

	private static void registerShaders(RegisterShadersEvent event) {
		try {
			event.registerShader(new ShaderInstance(event.getResourceProvider(), AlexsMobsInteraction.prefix("farseer_effects/farseer_effects"), DefaultVertexFormat.POSITION_TEX),
					shader -> FARSEER_EFFECTS = shader);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
