package com.crimsoncrips.alexsmobsinteraction.mixins.external_mobs.vanilla.player;

import com.crimsoncrips.alexsmobsinteraction.client.layer.EndergradePlayerLayer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(PlayerRenderer.class)
public abstract class AMIRenderPlayerMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public AMIRenderPlayerMixin(EntityRendererProvider.Context pContext, PlayerModel<AbstractClientPlayer> pModel, float pShadowRadius) {
        super(pContext, pModel, pShadowRadius);
    }

    @Inject(method = "<init>", at = @At("TAIL"),remap = false)
    private void alexsMobsInteraction$getItem(EntityRendererProvider.Context pContext, boolean pUseSlimModel, CallbackInfo ci) {
        this.addLayer(new EndergradePlayerLayer(this));
    }

}
