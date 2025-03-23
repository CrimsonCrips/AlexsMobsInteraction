package com.crimsoncrips.alexsmobsinteraction.client.renderer;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.client.AMIShaders;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static java.lang.Math.abs;

@Mod.EventBusSubscriber(modid = AlexsMobsInteraction.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AMIRendering {
	private static final ResourceLocation FARSEER_TEXT = AlexsMobsInteraction.prefix("textures/gui/farseer_text.png");

	private static final ResourceLocation FARSEER_TEXT_SHADOW = AlexsMobsInteraction.prefix("textures/gui/farseer_text_shadow.png");

	private static final ResourceLocation FARSEER_PORTAL = AlexsMobsInteraction.prefix("textures/gui/farseer_portal.png");

	//Thank you, Twilight Forest (aka Drullkus) for the help [and code]

	public static float ALTER_PROGRESS = 0.0F;

	public static float STALK_PROGRESS = 0.0F;

	@SubscribeEvent
	public static void registerOverlays(RegisterGuiOverlaysEvent event) {
		Minecraft minecraft = Minecraft.getInstance();
		event.registerAbove(VanillaGuiOverlay.CROSSHAIR.id(), "farseer_text", (gui, graphics, partialTick, screenWidth, screenHeight) -> {
			if (minecraft.player == null)
				return;
			if (!AlexsMobsInteraction.CLIENT_CONFIG.FARSEER_EFFECTS_ENABLED.get())
				return;
			renderFarseerTextEffects(graphics, screenWidth,screenHeight,  13 ,8,0.07,1,0,0,3);
			renderFarseerTextEffects(graphics, screenWidth,screenHeight,  -10 ,13,0.17,1,0,0,3);
			renderFarseerTextEffects(graphics, screenWidth,screenHeight,  8 ,-17,0.05,0, 1, 0,3);
			renderFarseerTextEffects(graphics, screenWidth,screenHeight,  17 ,-9,0.12,0 ,1, 0,3);
			renderFarseerTextEffects(graphics, screenWidth,screenHeight,  -13 ,-9,0.1,0, 0, 1,3);
			renderFarseerTextEffects(graphics, screenWidth,screenHeight,  -8 ,17,0.04,0, 0, 1,3);


			renderFarseerText(graphics, screenWidth,screenHeight);
		});

		//Thanks Drullkus for cooking
		final ResourceLocation farseerNoise = AlexsMobsInteraction.prefix("textures/gui/farseer_static.png");
		event.registerAbove(VanillaGuiOverlay.VIGNETTE.id(), "farseer_effects", (gui, graphics, partialTick, screenWidth, screenHeight) -> {
			if (minecraft.player == null)
				return;
			if (!AlexsMobsInteraction.CLIENT_CONFIG.FARSEER_EFFECTS_ENABLED.get())
				return;

			RenderSystem.setShaderColor(1F, 1F, 1F, STALK_PROGRESS);

			RenderSystem.setShaderTexture(0, farseerNoise);

			RenderSystem.disableDepthTest();
			RenderSystem.depthMask(false);
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();

			RenderSystem.setShader(() -> AMIShaders.FARSEER_EFFECTS);
			AMIShaders.FARSEER_EFFECTS.GAME_TIME.set(RenderSystem.getShaderGameTime());

			Tesselator tesselator = Tesselator.getInstance();
			BufferBuilder bufferbuilder = tesselator.getBuilder();
			bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
			bufferbuilder.vertex(0.0D, screenHeight, -90.0D).uv(0.0F, 1.0F).endVertex();
			bufferbuilder.vertex(screenWidth, screenHeight, -90.0D).uv(1.0F, 1.0F).endVertex();
			bufferbuilder.vertex(screenWidth, 0.0D, -90.0D).uv(1.0F, 0.0F).endVertex();
			bufferbuilder.vertex(0.0D, 0.0D, -90.0D).uv(0.0F, 0.0F).endVertex();

			tesselator.end();
			RenderSystem.depthMask(true);
			RenderSystem.enableDepthTest();
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		});

	}

	public static void renderFarseerText(GuiGraphics graphics, int screenWidth, int screenHeight) {
		float currentAlpha = Math.min((2-abs(4 * ALTER_PROGRESS -2)),1);
		if (currentAlpha <= 0.0F)
			return;
		int imageWidth = 500;
		int imageHeight = 191;
		double movement = Math.sin(2*Math.sin((double) (ALTER_PROGRESS) /2) * 300);
		double x1 = ((double) (screenWidth - imageWidth) / 2);
		double y2 = ((double) (screenHeight - imageHeight) / 2);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, currentAlpha);
		graphics.blit(FARSEER_PORTAL, (int) x1, (int) y2, 0,0, 0, 500, 191,500,191);
		graphics.blit(FARSEER_TEXT, (int) (x1 + movement), (int) (y2 + movement), 0,0, 0, 500, 191,500,191);

	}

	public static void renderFarseerTextEffects(GuiGraphics graphics, int screenWidth, int screenHeight,int xRange, int yRange, double speed,float red, float green, float blue, int transparency) {
		float currentAlpha = Math.min((2-abs(4 * ALTER_PROGRESS -2)),1);
		if (currentAlpha <= 0.0F)
			return;
		int imageWidth = 500;
		int imageHeight = 191;
		double movementX = xRange * Math.sin(((double) (ALTER_PROGRESS))  / speed) * 2;
		double movementY = yRange * Math.sin(((double) (ALTER_PROGRESS))  / speed) * 2;

		double x1 = ((double) (screenWidth - imageWidth) / 2 ) + movementX;
		double y2 = ((double) (screenHeight - imageHeight) / 2 ) + movementY;
		RenderSystem.setShaderColor(red, green, blue, currentAlpha / transparency);
		graphics.blit(FARSEER_TEXT_SHADOW, (int)  x1, (int) y2, 0,0, 0, 500, 191,500,191);

		}

}

