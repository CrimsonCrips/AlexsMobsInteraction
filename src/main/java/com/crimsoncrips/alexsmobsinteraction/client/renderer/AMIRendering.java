package com.crimsoncrips.alexsmobsinteraction.client.renderer;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AlexsMobsInteraction.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AMIRendering {

	//Thank you, Twilight Forest (aka Drullkus) for the help [and code]


	public static boolean renderText = false;

	public static double alpha = 1.0;



	private static final ResourceLocation FARSEER_TEXT = AlexsMobsInteraction.prefix("textures/gui/farseer_text.png");

	@SubscribeEvent
	public static void registerOverlays(RegisterGuiOverlaysEvent event) {
		event.registerAbove(VanillaGuiOverlay.CROSSHAIR.id(), "farseer_text", (gui, graphics, partialTick, screenWidth, screenHeight) -> {
			Minecraft minecraft = Minecraft.getInstance();
			if (minecraft.player == null)
				return;
			textRenderings(minecraft, graphics, screenWidth,screenHeight);

		});
	}

	public static void textRendering(Minecraft minecraft, GuiGraphics graphics, int screenWidth, int screenHeight) {
		if (!minecraft.options.getCameraType().isFirstPerson())
			return;
		if (!renderText)
			return;

		int y = (screenHeight / 2) - 130;
		int x = (screenWidth / 2) - 200;
		graphics.blit(FARSEER_TEXT, x, y, 0, 0, 400, 250);

	}

	public static void textRenderings(Minecraft minecraft, GuiGraphics graphics, int screenWidth, int screenHeight) {
		if (!minecraft.options.getCameraType().isFirstPerson())
			return;
		if (!renderText)
			return;




		System.out.println(alpha);


		int y = (screenHeight / 2) - 130;
		int x = (screenWidth / 2) - 200;
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, (float) alpha);
		graphics.blit(FARSEER_TEXT, x, y, 0, 0, 400, 250);

		if (!(alpha <= 0)) alpha = alpha - 0.01;
		else {
			alpha = 1;
			renderText = false;
		}
	}

}

