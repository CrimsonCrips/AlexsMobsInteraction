package com.crimsoncrips.alexsmobsinteraction.client.renderer;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AlexsMobsInteraction.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AMIRendering {
	private static final ResourceLocation FARSEER_TEXT = AlexsMobsInteraction.prefix("textures/gui/farseer_text.png");

	//Thank you, Twilight Forest (aka Drullkus) for the help [and code]

	public static float ALPHA_PROGRESS = 1.0F;

	@SubscribeEvent
	public static void registerOverlays(RegisterGuiOverlaysEvent event) {
		event.registerAbove(VanillaGuiOverlay.CROSSHAIR.id(), "farseer_text", (gui, graphics, partialTick, screenWidth, screenHeight) -> {
			Minecraft minecraft = Minecraft.getInstance();
			if (minecraft.player == null)
				return;
			renderFarseerText(graphics, screenWidth,screenHeight);
		});
	}

	public static void renderFarseerText(GuiGraphics graphics, int screenWidth, int screenHeight) {
		float currentAlpha = Math.min(-3.0F * ALPHA_PROGRESS + 3.0F, 1.0F);
		if (currentAlpha <= 0.0F)
			return;

		int y = (screenHeight / 2) - 130;
		int x = (screenWidth / 2) - 200;
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, currentAlpha);
		graphics.blit(FARSEER_TEXT, x, y, 0, 0, 400, 250);
	}

}

