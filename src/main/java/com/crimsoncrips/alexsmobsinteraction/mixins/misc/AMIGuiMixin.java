package com.crimsoncrips.alexsmobsinteraction.mixins.misc;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.AMIFarseerEffects;
import com.github.alexthe666.alexsmobs.entity.EntityAnaconda;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.List;

import static com.crimsoncrips.alexsmobsinteraction.client.renderer.AMIRendering.ALPHA_PROGRESS;

@Debug(export = true)
@Mixin(Gui.class)
public abstract class AMIGuiMixin {

    @Shadow @Final protected Minecraft minecraft;

    @Shadow protected int screenWidth;

    @Shadow protected ItemStack lastToolHighlight;

    @Shadow public abstract Font getFont();

    @Shadow @Final protected RandomSource random;


    @Inject(method = "renderHotbar", at = @At(value = "HEAD"),remap = false)
    private void alexsMobsInteraction$renderHotbar(float pPartialTick, GuiGraphics pGuiGraphics, CallbackInfo ci){
        if(alterGui()){
            double movement = 30 * Math.sin(minecraft.player.tickCount * 0.2) * ALPHA_PROGRESS;
            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate(movement,0,0);
        }
    }

    @Inject(method = "renderHotbar", at = @At(value = "TAIL"),remap = false)
    private void alexsMobsInteraction$renderHotbar1(float pPartialTick, GuiGraphics pGuiGraphics, CallbackInfo ci){
        if(alterGui()){
            pGuiGraphics.pose().popPose();
        }
    }



    @Inject(method = "renderSelectedItemName(Lnet/minecraft/client/gui/GuiGraphics;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)I"), remap = false)
    private void alexsMobsInteraction$renderslectedItemName0(GuiGraphics pGuiGraphics, int yShift, CallbackInfo ci, @Local(ordinal = 1) int i, @Local(ordinal = 2) int j, @Local(ordinal = 3) int k, @Local(ordinal = 4) int l){
        if(alterGui()){
            RandomSource randomSource = minecraft.player.getRandom();
            double movement = Math.sin(randomSource.nextInt(0, 4) * Math.sin((double) (minecraft.level.getGameTime()) / 5) * 1000);
            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate(movement, movement, movement);
        }
    }

    @Inject(method = "renderSelectedItemName(Lnet/minecraft/client/gui/GuiGraphics;I)V", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)I"), remap = false)
    private void alexsMobsInteraction$renderslectedItemName1(GuiGraphics pGuiGraphics, int yShift, CallbackInfo ci){
        if(alterGui()){
            pGuiGraphics.pose().popPose();
        }
    }

    @Inject(method = "renderEffects", at = @At(value = "HEAD"), cancellable = true)
    private void alexsMobsInteraction$r(GuiGraphics pGuiGraphics, CallbackInfo ci){
        ci.cancel();
        Gui gui  = (Gui)(Object)this;

        Collection<MobEffectInstance> collection = this.minecraft.player.getActiveEffects();
        if (!collection.isEmpty()) {
            Screen $$4 = this.minecraft.screen;
            if ($$4 instanceof EffectRenderingInventoryScreen) {
                EffectRenderingInventoryScreen effectrenderinginventoryscreen = (EffectRenderingInventoryScreen)$$4;
                if (effectrenderinginventoryscreen.canSeeEffects()) {
                    return;
                }
            }

            RenderSystem.enableBlend();
            int j1 = 0;
            int k1 = 0;
            MobEffectTextureManager mobeffecttexturemanager = this.minecraft.getMobEffectTextures();
            List<Runnable> list = Lists.newArrayListWithExpectedSize(collection.size());

            for(MobEffectInstance mobeffectinstance : Ordering.natural().reverse().sortedCopy(collection)) {
                MobEffect mobeffect = mobeffectinstance.getEffect();
                IClientMobEffectExtensions renderer = IClientMobEffectExtensions.of(mobeffectinstance);
                if (renderer.isVisibleInGui(mobeffectinstance) && mobeffectinstance.showIcon()) {
                    int i = this.screenWidth;
                    int j = 1;
                    if (this.minecraft.isDemo()) {
                        j += 15;
                    }

                    if (mobeffect.isBeneficial()) {
                        ++j1;
                        i -= 25 * j1;
                    } else {
                        ++k1;
                        i -= 25 * k1;
                        j += 26;
                    }

                    float f;
                    if (mobeffectinstance.isAmbient()) {
                        f = 1.0F;
                        pGuiGraphics.blit(AbstractContainerScreen.INVENTORY_LOCATION, i, j, 165, 166, 24, 24);
                    } else {
                        pGuiGraphics.blit(AbstractContainerScreen.INVENTORY_LOCATION, i, j, 141, 166, 24, 24);
                        if (mobeffectinstance.endsWithin(200)) {
                            int k = mobeffectinstance.getDuration();
                            int l = 10 - k / 20;
                            f = Mth.clamp((float)k / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F) + Mth.cos((float)k * (float)Math.PI / 5.0F) * Mth.clamp((float)l / 10.0F * 0.25F, 0.0F, 0.25F);
                        } else {
                            f = 1.0F;
                        }
                    }

                    final ResourceLocation TEXTURE_POTION = new ResourceLocation("alexsmobsinteraction:textures/entity/ancient_dart_potion.png");

                    if (!renderer.renderGuiIcon(mobeffectinstance, gui, pGuiGraphics, i, j, 0.0F, f)) {
                        TextureAtlasSprite textureatlassprite = mobeffecttexturemanager.get(mobeffect);



                        int finalI = i;
                        int finalJ = j;
                        list.add((Runnable)() -> {
                            pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, f);
                            pGuiGraphics.blit(finalI + 3, finalJ + 3, 0, 18, 18, textureatlassprite);
                            pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
                        });
                    }
                }
            }

            list.forEach(Runnable::run);
        }
    }



    @Inject(method = "renderSelectedItemName(Lnet/minecraft/client/gui/GuiGraphics;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V"), remap = false)
    private void alexsMobsInteraction$renderslectedItemName2(GuiGraphics pGuiGraphics, int yShift, CallbackInfo ci, @Local(ordinal = 1) int i, @Local(ordinal = 2) int j, @Local(ordinal = 3) int k, @Local(ordinal = 4) int l){
        Minecraft minecraft = this.minecraft;
        if (minecraft.player != null && alterGui()){
            //Thanks Drullkus for the help and TerriblyBadCoder for the inspiration

            float time = minecraft.player.tickCount;
            RandomSource randomSource = minecraft.level.getRandom();
            l = l / (randomSource.nextBoolean() ? 4 : 5);

            pGuiGraphics.pose().pushPose();
            pGuiGraphics.fill(j - 2, k - 2, j + i + 2, k + 9 + 2, this.minecraft.options.getBackgroundColor(0));
            Font font = net.minecraftforge.client.extensions.common.IClientItemExtensions.of(lastToolHighlight).getFont(lastToolHighlight, net.minecraftforge.client.extensions.common.IClientItemExtensions.FontContext.SELECTED_ITEM_NAME);

            for (int t = 0; t <= 10; t++) {
                Quaternionf quaternionf = new Quaternionf();
                quaternionf.rotationYXZ(0f, 0, (float) (Math.cos(time / 8f) / 8f + randomSource.nextInt(0, 2)));
                pGuiGraphics.pose().rotateAround(quaternionf, j + i / 2f, k, 0);
                double movement = Math.sin(randomSource.nextInt(0, 4) * Math.sin((double) (time) / 2) * 300);
                pGuiGraphics.pose().translate(movement, movement, movement);
                pGuiGraphics.pose().translate(randomSource.nextInt(-10, 11), randomSource.nextInt(-10, 11), randomSource.nextInt(-10, 11));

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
    }


    @ModifyArg(method = "renderSelectedItemName(Lnet/minecraft/client/gui/GuiGraphics;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)I"),index = 1, remap = false)
    private Component alexsMobsInteraction$renderSelectedItemName(Component pText){
        if (alterGui()) {
            return Component.nullToEmpty("Test");
        }
        return pText;
    }

    @ModifyArg(method = "renderSelectedItemName(Lnet/minecraft/client/gui/GuiGraphics;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)I"),index = 2, remap = false)
    private int alexsMobsInteraction$renderSelectedItemName(int pX){
        if (alterGui()) {
            return (this.screenWidth - this.getFont().width(Component.nullToEmpty("Test"))) / 2;
        }
        return pX;
    }

    @Inject(method = "renderHeart", at = @At(value = "HEAD"), remap = false)
    private void alexsMobsInteraction$renderHeart(GuiGraphics pGuiGraphics, Gui.HeartType pHeartType, int pX, int pY, int pYOffset, boolean pRenderHighlight, boolean pHalfHeart, CallbackInfo ci){
        if (alterGui()){
            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate(randomShake(),randomShake(),randomShake());
        }
    }

    @Inject(method = "renderHeart", at = @At(value = "TAIL"), remap = false)
    private void alexsMobsInteraction$renderHeart1(GuiGraphics pGuiGraphics, Gui.HeartType pHeartType, int pX, int pY, int pYOffset, boolean pRenderHighlight, boolean pHalfHeart, CallbackInfo ci){
        if (alterGui()){
            pGuiGraphics.pose().popPose();
        }
    }



    public boolean alterGui(){
        return (((AMIFarseerEffects)minecraft.player).getFarseerTime() != 0) && AlexsMobsInteraction.CLIENT_CONFIG.FARSEER_EFFECTS_ENABLED.get();
    }

    public double randomShake(){
       return (random.nextInt(-1,2) * Math.sin((minecraft.level.getGameTime() * 0.8)) * ALPHA_PROGRESS);
    }
    



}