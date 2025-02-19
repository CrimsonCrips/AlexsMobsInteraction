package com.crimsoncrips.alexsmobsinteraction.mixins.misc;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Gui.class)
public abstract class AMIGuiMixin {


    @Shadow protected int screenHeight;

    @Shadow @Final protected static ResourceLocation GUI_ICONS_LOCATION;

    @Shadow @Final protected Minecraft minecraft;

    @Shadow protected abstract void renderSlot(GuiGraphics pGuiGraphics, int pX, int pY, float pPartialTick, Player pPlayer, ItemStack pStack, int pSeed);

    @Shadow @Final protected static ResourceLocation WIDGETS_LOCATION;

    @Shadow protected int screenWidth;

    @Shadow protected abstract Player getCameraPlayer();

    @Shadow protected ItemStack lastToolHighlight;

    @Shadow public abstract Font getFont();


    @Definition(id = "screenWidth", field = "Lnet/minecraft/client/gui/Gui;screenWidth:I")
    @Expression("this.screenWidth / 2")
    @ModifyExpressionValue(method = "renderHotbar", at = @At("MIXINEXTRAS:EXPRESSION"))
    private int yourHandler(int original,@Local(ordinal = 0) Player player,@Local(ordinal = 0) float pPartialTick) {
        RandomSource randomSource = player.getRandom();
        double movement = Math.sin(randomSource.nextInt(0,4) * Math.sin((pPartialTick) / 5) * 1000);
        return (int) (original + movement);
    }

    @Inject(method = "renderSelectedItemName(Lnet/minecraft/client/gui/GuiGraphics;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V"), remap = false)
    private void alexsMobsInteraction$renderslectedItemName(GuiGraphics pGuiGraphics, int yShift, CallbackInfo ci, @Local(ordinal = 1) int i, @Local(ordinal = 2) int j, @Local(ordinal = 3) int k, @Local(ordinal = 4) int l){

        //Thanks Drullkus for the help and TerriblyBadCoder for the inspiration
        Minecraft minecraft = this.minecraft;
        float time = minecraft.level.getGameTime();
        RandomSource randomSource = minecraft.level.getRandom();
        l = l / 6;

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.fill(j - 2, k - 2, j + i + 2, k + 9 + 2, this.minecraft.options.getBackgroundColor(0));
        Font font = net.minecraftforge.client.extensions.common.IClientItemExtensions.of(lastToolHighlight).getFont(lastToolHighlight, net.minecraftforge.client.extensions.common.IClientItemExtensions.FontContext.SELECTED_ITEM_NAME);

        for (int t = 0; t <= 10; t++) {
            Quaternionf quaternionf = new Quaternionf();
            quaternionf.rotationYXZ(0f, 0, (float) (Math.cos(time / 8f) / 8f + randomSource.nextInt(0,2)));
            pGuiGraphics.pose().rotateAround(quaternionf, j + i / 2f, k, 0);
            double movement = Math.sin(randomSource.nextInt(0,4) * Math.sin((double) (time) / 2) * 300);
            pGuiGraphics.pose().translate(movement, movement, movement);
            pGuiGraphics.pose().translate(randomSource.nextInt(-10,11), randomSource.nextInt(-10,11), randomSource.nextInt(-10,11));

            Component component = Component.nullToEmpty("§k-- --");
            int jC = (this.screenWidth - this.getFont().width(component)) / 2;


            if (font == null) {
                pGuiGraphics.drawString(this.getFont(), component, jC, k, 16777215 + (l << 24));
            } else {
                jC = (this.screenWidth - font.width(component)) / 2;
                pGuiGraphics.drawString(font, component, jC, k, 16777215 + (l << 24));
            }
        }

        pGuiGraphics.pose().popPose();
    }


    @ModifyArg(method = "renderSelectedItemName(Lnet/minecraft/client/gui/GuiGraphics;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)I"),index = 1, remap = false)
    private Component alexsMobsInteraction$renderSelectedItemName(Component pText){
        return Component.nullToEmpty("Test");
    }

    @ModifyArg(method = "renderSelectedItemName(Lnet/minecraft/client/gui/GuiGraphics;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)I"),index = 2, remap = false)
    private int alexsMobsInteraction$renderSelectedItemName(int pX){
        return (this.screenWidth - this.getFont().width(Component.nullToEmpty("Test"))) / 2;
    }



}