package com.crimsoncrips.alexsmobsinteraction.client.dictionary;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AMIDictionaryCover {

    private static final ResourceLocation SPIN_TEXTURE = AlexsMobsInteraction.prefix("textures/gui/book_spin.png");
    private static final int SPIN_SIZE = 21;
    private static final int SPIN_X = 2;
    private static final int SPIN_Y = 206;
    public static final int BOOK_CENTER_X = 195;
    public static final int BOOK_CENTER_Y = 106;

    private static final long INTRO_TIME = 450L;
    private static final long SHRINK_TIME = 250L;
    private static final long FLIP_TIME = 800L;
    private static final long GROW_TIME = 250L;
    private static final float HOVER_SCALE = 1.25F;

    private static final Path SEEN_FLAG = FMLPaths.CONFIGDIR.get().resolve("alexsmobsinteraction-dictionary-seen");

    public static boolean interactionCover = false;

    private static long introStart = -1L;
    private static long flipStart = -1L;
    private static boolean swapped = true;
    private static float hoverScale = 1.0F;
    private static long lastFrame = -1L;

    public static void onOpen() {
        flipStart = -1L;
        swapped = true;
        hoverScale = 1.0F;
        lastFrame = -1L;
        introStart = -1L;
        if (!Files.exists(SEEN_FLAG)) {
            introStart = Util.getMillis();
            try {
                Files.createFile(SEEN_FLAG);
            } catch (IOException ignored) {
            }
        }
    }

    public static boolean isFlipping() {
        return flipStart >= 0L && Util.getMillis() - flipStart < SHRINK_TIME + FLIP_TIME + GROW_TIME;
    }

    public static void startFlip() {
        flipStart = Util.getMillis();
        swapped = false;
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 0.8F));
    }

    public static boolean consumeSwap() {
        if (swapped || flipStart < 0L || Util.getMillis() - flipStart < SHRINK_TIME || spinProgress() < 0.5F)
            return false;
        swapped = true;
        interactionCover = !interactionCover;
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.2F));
        return true;
    }

    public static float contentScale() {
        if (flipStart < 0L)
            return 1.0F;
        long elapsed = Util.getMillis() - flipStart;
        if (elapsed < SHRINK_TIME)
            return 1.0F - ease(elapsed / (float) SHRINK_TIME);
        if (elapsed < SHRINK_TIME + FLIP_TIME)
            return 0.0F;
        return ease(Math.min(1.0F, (elapsed - SHRINK_TIME - FLIP_TIME) / (float) GROW_TIME));
    }

    private static float spinProgress() {
        if (flipStart < 0L)
            return 0.0F;
        long elapsed = Util.getMillis() - flipStart - SHRINK_TIME;
        if (elapsed <= 0L)
            return 0.0F;
        if (elapsed >= FLIP_TIME)
            return 1.0F;
        return easeOutBack(elapsed / (float) FLIP_TIME);
    }

    public static float bookRotation() {
        float progress = spinProgress();
        return progress <= 0.0F || progress >= 1.0F && Util.getMillis() - flipStart >= SHRINK_TIME + FLIP_TIME ? 0.0F : progress * 360.0F;
    }

    private static float ease(float t) {
        return t * t * (3.0F - 2.0F * t);
    }

    private static float easeOutBack(float t) {
        float c1 = 1.70158F;
        float c3 = c1 + 1.0F;
        float u = t - 1.0F;
        return 1.0F + c3 * u * u * u + c1 * u * u;
    }

    private static float introScale() {
        if (introStart < 0L)
            return 1.0F;
        float t = Math.min(1.0F, (Util.getMillis() - introStart) / (float) INTRO_TIME);
        return t < 1.0F ? ease(t) * (1.0F + 0.2F * Mth.sin((float) Math.PI * t)) : 1.0F;
    }

    public static boolean isOverSpin(double mouseX, double mouseY, int bookLeft, int bookTop) {
        double radius = SPIN_SIZE / 2.0D * hoverScale * introScale();
        double dx = mouseX - (bookLeft + SPIN_X);
        double dy = mouseY - (bookTop + SPIN_Y);
        return radius > 4.0D && dx * dx + dy * dy <= radius * radius;
    }

    public static void renderSpin(GuiGraphics guiGraphics, int mouseX, int mouseY, int bookLeft, int bookTop) {
        long now = Util.getMillis();
        float frameTime = lastFrame < 0L ? 0.0F : Math.min(0.1F, (now - lastFrame) / 1000.0F);
        lastFrame = now;
        float target = !isFlipping() && isOverSpin(mouseX, mouseY, bookLeft, bookTop) ? HOVER_SCALE : 1.0F;
        hoverScale += (target - hoverScale) * Math.min(1.0F, frameTime * 12.0F);

        float scale = hoverScale * introScale();
        if (scale <= 0.01F)
            return;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(bookLeft + SPIN_X, bookTop + SPIN_Y, 400.0F);
        guiGraphics.pose().scale(scale, scale, 1.0F);
        guiGraphics.blit(SPIN_TEXTURE, -SPIN_SIZE / 2, -SPIN_SIZE / 2, 0.0F, 0.0F, SPIN_SIZE, SPIN_SIZE, SPIN_SIZE, SPIN_SIZE);
        guiGraphics.pose().popPose();
    }
}
