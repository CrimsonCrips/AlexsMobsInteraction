package com.crimsoncrips.alexsmobsinteraction.mixins.misc.dictionary;

import com.crimsoncrips.alexsmobsinteraction.client.dictionary.AMIDictionaryCover;
import com.github.alexthe666.alexsmobs.client.gui.GUIAnimalDictionary;
import com.github.alexthe666.citadel.client.gui.GuiBasicBook;
import com.github.alexthe666.citadel.client.gui.data.RecipeData;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiBasicBook.class, remap = false)
public abstract class AMIGuiBasicBookMixin extends Screen {

    @Shadow protected Component writtenTitle;
    @Shadow protected ResourceLocation currentPageJSON;
    @Shadow protected int xSize;
    @Shadow protected int ySize;

    @Shadow public abstract ResourceLocation getRootPage();

    protected AMIGuiBasicBookMixin(Component title) {
        super(title);
    }

    @Unique
    private boolean alexsMobsInteraction$isDictionary() {
        return (Object) this instanceof GUIAnimalDictionary;
    }

    @Unique
    private float alexsMobsInteraction$contentScale() {
        return this.alexsMobsInteraction$isDictionary() ? AMIDictionaryCover.contentScale() : 1.0F;
    }

    @Unique
    private void alexsMobsInteraction$scaledAround(GuiGraphics guiGraphics, float centerX, float centerY, Runnable draw) {
        float scale = this.alexsMobsInteraction$contentScale();
        if (scale >= 1.0F) {
            draw.run();
            return;
        }
        if (scale <= 0.01F)
            return;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(centerX, centerY, 0.0F);
        guiGraphics.pose().scale(scale, scale, 1.0F);
        guiGraphics.pose().translate(-centerX, -centerY, 0.0F);
        draw.run();
        guiGraphics.pose().popPose();
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/citadel/client/gui/BookBlit;blitWithColor(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/resources/ResourceLocation;IIFFIIIIIIII)V"))
    private void alexsMobsInteraction$render(GuiGraphics guiGraphics, ResourceLocation texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight, int r, int g, int b, int a, Operation<Void> original) {
        float rotation = this.alexsMobsInteraction$isDictionary() ? AMIDictionaryCover.bookRotation() : 0.0F;
        if (rotation == 0.0F) {
            original.call(guiGraphics, texture, x, y, u, v, width, height, textureWidth, textureHeight, r, g, b, a);
            return;
        }
        float pivotX = (this.width - this.xSize) / 2.0F + AMIDictionaryCover.BOOK_CENTER_X;
        float pivotY = (this.height - this.ySize + 128) / 2.0F + AMIDictionaryCover.BOOK_CENTER_Y;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(pivotX, pivotY, 0.0F);
        guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(rotation));
        guiGraphics.pose().translate(-pivotX, -pivotY, 0.0F);
        original.call(guiGraphics, texture, x, y, u, v, width, height, textureWidth, textureHeight, r, g, b, a);
        guiGraphics.pose().popPose();
    }

    @WrapOperation(method = "writePageText", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I"))
    private int alexsMobsInteraction$writePageText(GuiGraphics guiGraphics, Font font, String text, int x, int y, int color, boolean shadow, Operation<Integer> original) {
        int[] drawn = {0};
        this.alexsMobsInteraction$scaledAround(guiGraphics, x + font.width(text) / 2.0F, y + font.lineHeight / 2.0F, () -> drawn[0] = original.call(guiGraphics, font, text, x, y, color, shadow));
        return drawn[0];
    }

    @WrapOperation(method = "writePageText", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIIZ)I"))
    private int alexsMobsInteraction$writePageText1(GuiGraphics guiGraphics, Font font, Component text, int x, int y, int color, boolean shadow, Operation<Integer> original) {
        int[] drawn = {0};
        this.alexsMobsInteraction$scaledAround(guiGraphics, x + font.width(text) / 2.0F, y + font.lineHeight / 2.0F, () -> drawn[0] = original.call(guiGraphics, font, text, x, y, color, shadow));
        return drawn[0];
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V"))
    private void alexsMobsInteraction$render1(GuiBasicBook instance, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, Operation<Void> original) {
        if (this.alexsMobsInteraction$contentScale() >= 1.0F) {
            original.call(instance, guiGraphics, mouseX, mouseY, partialTick);
            return;
        }
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        for (Renderable renderable : this.renderables) {
            if (renderable instanceof AbstractWidget widget) {
                this.alexsMobsInteraction$scaledAround(guiGraphics, widget.getX() + widget.getWidth() / 2.0F, widget.getY() + widget.getHeight() / 2.0F, () -> widget.render(guiGraphics, mouseX, mouseY, partialTick));
            } else {
                renderable.render(guiGraphics, mouseX, mouseY, partialTick);
            }
        }
    }

    @WrapOperation(method = "renderOtherWidgets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V"))
    private void alexsMobsInteraction$renderOtherWidgets(GuiGraphics guiGraphics, ResourceLocation texture, int x, int y, int u, int v, int width, int height, Operation<Void> original) {
        this.alexsMobsInteraction$scaledAround(guiGraphics, x + width / 2.0F, y + height / 2.0F, () -> original.call(guiGraphics, texture, x, y, u, v, width, height));
    }

    @WrapOperation(method = "renderOtherWidgets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderItem(Lnet/minecraft/world/item/ItemStack;II)V"))
    private void alexsMobsInteraction$renderOtherWidgets1(GuiGraphics guiGraphics, ItemStack stack, int x, int y, Operation<Void> original) {
        this.alexsMobsInteraction$scaledAround(guiGraphics, x + 8.0F, y + 8.0F, () -> original.call(guiGraphics, stack, x, y));
    }

    @WrapOperation(method = "renderOtherWidgets", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/citadel/client/gui/GuiBasicBook;renderRecipe(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/item/crafting/Recipe;Lcom/github/alexthe666/citadel/client/gui/data/RecipeData;II)V"))
    private void alexsMobsInteraction$renderOtherWidgets2(GuiBasicBook instance, GuiGraphics guiGraphics, Recipe recipe, RecipeData recipeData, int k, int l, Operation<Void> original) {
        float scale = (float) recipeData.scale();
        this.alexsMobsInteraction$scaledAround(guiGraphics, k + recipeData.x() + 58.0F * scale, l + recipeData.y() + 26.5F * scale, () -> original.call(instance, guiGraphics, recipe, recipeData, k, l));
    }

    @ModifyArg(method = "renderOtherWidgets", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/citadel/client/gui/GuiBasicBook;drawEntityOnScreen(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/renderer/MultiBufferSource;IIFFZDDDFFLnet/minecraft/world/entity/Entity;)V"), index = 5)
    private float alexsMobsInteraction$renderOtherWidgets3(float scale) {
        return scale * this.alexsMobsInteraction$contentScale();
    }

    @ModifyArg(method = "renderOtherWidgets", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/citadel/client/gui/GuiBasicBook;drawTabulaModelOnScreen(Lnet/minecraft/client/gui/GuiGraphics;Lcom/github/alexthe666/citadel/client/model/TabulaModel;Lnet/minecraft/resources/ResourceLocation;IIFZDDDFF)V"), index = 5)
    private float alexsMobsInteraction$renderOtherWidgets4(float scale) {
        return scale * this.alexsMobsInteraction$contentScale();
    }

    @Inject(method = "writePageText", at = @At("HEAD"))
    private void alexsMobsInteraction$writePageText2(GuiGraphics guiGraphics, int x, int y, CallbackInfo ci) {
        if (this.alexsMobsInteraction$isDictionary() && AMIDictionaryCover.interactionCover && this.getRootPage().equals(this.currentPageJSON)) {
            this.writtenTitle = Component.translatable("misc.alexsmobsinteraction.animal_interaction");
        }
    }
}
