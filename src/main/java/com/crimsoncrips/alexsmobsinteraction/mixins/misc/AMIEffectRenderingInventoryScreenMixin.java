package com.crimsoncrips.alexsmobsinteraction.mixins.misc;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.FarseerFx;
import com.crimsoncrips.alexsmobsinteraction.server.effect.AMIEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;


@Mixin(EffectRenderingInventoryScreen.class)
public abstract class AMIEffectRenderingInventoryScreenMixin {


    @Shadow protected abstract Component getEffectName(MobEffectInstance pEffect);

    //BIGGEST HEADACHE TO IMPLEMENT, ft.Drullkus
    @ModifyArg(method = "renderIcons", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/MobEffectTextureManager;get(Lnet/minecraft/world/effect/MobEffect;)Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;"))
    private MobEffect alexsMobsInteraction$renderIcons(MobEffect pEffect){
        if (alterGui()) {
            return AMIEffects.FARSEER_ICON.get();
        }
        return pEffect;
    }

    @ModifyArg(method = "renderLabels", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)I"),index = 1)
    private Component alexsMobsInteraction$renderLabels(Component pText){
        if (alterGui()) {
            return AMIEffects.FARSEER_ICON.get().getDisplayName();
        }
        return pText;
    }

    @ModifyArg(method = "renderEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;II)V"),index = 1)
    private List<Component> alexsMobsInteraction$renderEffects(List<Component> pTooltipLines){
        if (alterGui()) {
            MobEffectInstance mobEffectInstance = new MobEffectInstance(AMIEffects.FARSEER_ICON.get(), 300, 0);
            List<Component> list = List.of(this.getEffectName(mobEffectInstance), MobEffectUtil.formatDuration(mobEffectInstance, 1.0F));

            return list;
        }
        return pTooltipLines;
    }


    public boolean alterGui(){
        if (Minecraft.getInstance().player != null) {
            return (((FarseerFx) Minecraft.getInstance().player).getAlterTime() != 0) && AlexsMobsInteraction.CLIENT_CONFIG.FARSEER_EFFECTS_ENABLED.get();
        }
        return false;
    }



    



}