package com.crimsoncrips.alexsmobsinteraction.mixins.misc.dictionary;

import com.crimsoncrips.alexsmobsinteraction.client.dictionary.AMIDictionaryCover;
import com.github.alexthe666.alexsmobs.client.gui.GUIAnimalDictionary;
import com.github.alexthe666.citadel.client.gui.GuiBasicBook;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GUIAnimalDictionary.class, remap = false)
public abstract class AMIAnimalDictionaryMixin extends GuiBasicBook {

    @Unique
    private boolean alexsMobsInteraction$opened = false;

    protected AMIAnimalDictionaryMixin(ItemStack bookStack, Component title) {
        super(bookStack, title);
    }

    @Unique
    private int alexsMobsInteraction$bookLeft() {
        return (this.width - this.xSize) / 2;
    }

    @Unique
    private int alexsMobsInteraction$bookTop() {
        return (this.height - this.ySize + 128) / 2;
    }

    @Override
    protected void init() {
        super.init();
        if (!this.alexsMobsInteraction$opened) {
            this.alexsMobsInteraction$opened = true;
            AMIDictionaryCover.onOpen();
        }
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void alexsMobsInteraction$render(GuiGraphics guiGraphics, int x, int y, float partialTicks, CallbackInfo ci) {
        if (AMIDictionaryCover.consumeSwap()) {
            this.prevPageJSON = this.currentPageJSON;
            this.currentPageJSON = this.getRootPage();
            this.currentPageCounter = 0;
            this.preservedPageIndex = 0;
            this.internalPage = null;
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void alexsMobsInteraction$render1(GuiGraphics guiGraphics, int x, int y, float partialTicks, CallbackInfo ci) {
        AMIDictionaryCover.renderSpin(guiGraphics, x, y, this.alexsMobsInteraction$bookLeft(), this.alexsMobsInteraction$bookTop());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (AMIDictionaryCover.isFlipping())
            return true;
        if (button == 0 && AMIDictionaryCover.isOverSpin(mouseX, mouseY, this.alexsMobsInteraction$bookLeft(), this.alexsMobsInteraction$bookTop())) {
            AMIDictionaryCover.startFlip();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
