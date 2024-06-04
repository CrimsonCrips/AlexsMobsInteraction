package com.crimsoncrips.alexsmobsinteraction.client.renderer;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static java.lang.Math.abs;
import static java.lang.Math.random;

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
			int randomSource = minecraft.player.getRandom().nextInt(6) - 3;
			renderFarseerTextEffects(graphics, screenWidth,screenHeight,randomSource);
		});
	}

	public static void renderFarseerText(GuiGraphics graphics, int screenWidth, int screenHeight,int random) {
		float currentAlpha = Math.min((2-abs(4 * ALPHA_PROGRESS -2)),1);
		if (currentAlpha <= 0.0F)
			return;
		int imageWidth = 500;
		int imageHeight = 191;
		double movement = Math.sin(2*Math.sin((double) (ALPHA_PROGRESS) /2) * 300);

		double x1 = ((double) (screenWidth - imageWidth) / 2) + movement + random;
		double y2 = ((double) (screenHeight - imageHeight) / 2) + movement + random;
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, currentAlpha);
		graphics.blit(FARSEER_TEXT, (int) x1, (int) y2, 0,0, 0, 500, 191,500,191);

	}

	public static void renderFarseerTextEffects(GuiGraphics graphics, int screenWidth, int screenHeight, int random) {
		float currentAlpha = Math.min((2-abs(4 * ALPHA_PROGRESS -2)),1);
		if (currentAlpha <= 0.0F)
			return;
		int imageWidth = 500;
		int imageHeight = 191;
		double movement = 10 * Math.sin(((double) (ALPHA_PROGRESS))  /0.1) * 2;

		double x1 = ((double) (screenWidth - imageWidth) / 2 + movement) + movement;
		double y2 = ((double) (screenHeight - imageHeight) / 2 + movement) + movement;
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, currentAlpha / 2);
		graphics.blit(FARSEER_TEXT, (int) x1, (int) y2, 0,0, 0, 500, 191,500,191);

		}

}

