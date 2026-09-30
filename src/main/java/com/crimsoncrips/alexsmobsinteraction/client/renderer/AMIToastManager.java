package com.crimsoncrips.alexsmobsinteraction.client.renderer;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class AMIToastManager {

    public static final ResourceLocation DEFAULT_SPRITE = AlexsMobsInteraction.prefix("toast/title_box");

    private static final long ANIMATION_TIME_MS = 600L;
    private static final int GAP = 0;
    private static final int TOP_MARGIN = 8;
    private static final int LEFT_MARGIN = 8;
    private static final float TEXT_SCALE = 0.85f;
    private static final int LINE_SPACING = 11;

    private static final int PADDING_X = 10;
    private static final int PADDING_Y = 16;
    private static final int MAX_BOX_WIDTH = 220;
    private static final int MIN_BOX_WIDTH = 60;
    private static final int MIN_BOX_HEIGHT = 24;

    private static final List<ToastEntry> active = new ArrayList<>();

    private static class ToastEntry {
        final ResourceLocation sprite;
        final long displayTimeMs;
        final List<FormattedCharSequence> lines;
        final int boxWidth;
        final int boxHeight;

        long shownAt = -1L;
        boolean hiding = false;
        long hideStartedAt = -1L;

        ToastEntry(Component message, ResourceLocation sprite, long displayTimeMs) {
            this.sprite = sprite;
            this.displayTimeMs = displayTimeMs;

            Font font = Minecraft.getInstance().font;
            int wrapWidth = (int) ((MAX_BOX_WIDTH - PADDING_X * 2) / TEXT_SCALE);
            this.lines = font.split(message, wrapWidth);

            int textWidth = 0;
            for (FormattedCharSequence line : this.lines) {
                textWidth = Math.max(textWidth, font.width(line));
            }

            this.boxWidth = Math.max(MIN_BOX_WIDTH, Math.round(textWidth * TEXT_SCALE) + PADDING_X * 2);
            this.boxHeight = Math.max(MIN_BOX_HEIGHT, Math.round(this.lines.size() * LINE_SPACING * TEXT_SCALE) + PADDING_Y * 2);
        }
    }

    private AMIToastManager() {
    }

    public static void addToast(Component message, long displayTimeMs) {
        addToast(message, DEFAULT_SPRITE, displayTimeMs);
    }

    public static void addToast(Component message, ResourceLocation sprite, long displayTimeMs) {
        active.add(new ToastEntry(message, sprite, displayTimeMs));
    }

    public static void render(GuiGraphics graphics) {
        if (active.isEmpty()) return;

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.hideGui) return;

        Font font = minecraft.font;
        long now = Util.getMillis();
        int y = TOP_MARGIN;

        Iterator<ToastEntry> iterator = active.iterator();
        while (iterator.hasNext()) {
            ToastEntry entry = iterator.next();
            if (entry.shownAt < 0L) {
                entry.shownAt = now;
            }

            long elapsed = now - entry.shownAt;
            if (!entry.hiding && elapsed >= entry.displayTimeMs) {
                entry.hiding = true;
                entry.hideStartedAt = now;
            }

            float visibility;
            if (entry.hiding) {
                long hideElapsed = now - entry.hideStartedAt;
                if (hideElapsed >= ANIMATION_TIME_MS) {
                    iterator.remove();
                    continue;
                }
                float f = Mth.clamp(hideElapsed / (float) ANIMATION_TIME_MS, 0f, 1f);
                visibility = 1f - f * f;
            } else {
                float f = Mth.clamp(elapsed / (float) ANIMATION_TIME_MS, 0f, 1f);
                visibility = f * f;
            }

            int x = LEFT_MARGIN - Math.round(entry.boxWidth * (1f - visibility));

            graphics.pose().pushPose();
            graphics.pose().translate(x, y, 800.0F);

            graphics.blitSprite(entry.sprite, 0, 0, entry.boxWidth, entry.boxHeight);

            graphics.pose().pushPose();
            graphics.pose().scale(TEXT_SCALE, TEXT_SCALE, 1f);

            int textY = Math.round((entry.boxHeight / 2f - entry.lines.size() * LINE_SPACING / 2f) / TEXT_SCALE);
            int textX = Math.round(PADDING_X / TEXT_SCALE);
            for (FormattedCharSequence line : entry.lines) {
                graphics.drawString(font, line, textX, textY, 0xFFFFFF, false);
                textY += LINE_SPACING;
            }

            graphics.pose().popPose();
            graphics.pose().popPose();

            y += entry.boxHeight + GAP;
        }
    }
}
