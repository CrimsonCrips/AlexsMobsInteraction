package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.capuchin;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.AncientDartPotion;
import com.github.alexthe666.alexsmobs.client.model.ModelAncientDart;
import com.github.alexthe666.alexsmobs.client.model.ModelCapuchinMonkey;
import com.github.alexthe666.alexsmobs.client.render.AMRenderTypes;
import com.github.alexthe666.alexsmobs.client.render.RenderTossedItem;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerCapuchinItem;
import com.github.alexthe666.alexsmobs.entity.EntityCapuchinMonkey;
import com.github.alexthe666.alexsmobs.entity.EntityTossedItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(RenderTossedItem.class)
public abstract class AMIRenderTossedItemMixin extends EntityRenderer<EntityTossedItem> {

    @Shadow @Final public static ModelAncientDart DART_MODEL;
    @Unique
    private static final ResourceLocation TEXTURE_POTION = new ResourceLocation("alexsmobsinteraction:textures/entity/ancient_dart_potion.png");

    protected AMIRenderTossedItemMixin(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Inject(method = "render(Lcom/github/alexthe666/alexsmobs/entity/EntityTossedItem;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V",ordinal = 0))
    private void alexsMobsInteraction$render(EntityTossedItem entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, CallbackInfo ci) {
        int color = ((AncientDartPotion) entityIn).getPotionColor();
        if (color != -1 && AlexsMobsInteraction.COMMON_CONFIG.DART_EFFECTS_ENABLED.get()) {
            float r = (float) (color >> 16 & 255) / 255.0F;
            float g = (float) (color >> 8 & 255) / 255.0F;
            float b = (float) (color & 255) / 255.0F;
            DART_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(AMRenderTypes.entityCutoutNoCull(TEXTURE_POTION)), packedLightIn, OverlayTexture.NO_OVERLAY, r, g, b, 1.0F);
        }
    }

}
