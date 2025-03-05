package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.capuchin;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.AncientDartPotion;
import com.github.alexthe666.alexsmobs.client.model.ModelAncientDart;
import com.github.alexthe666.alexsmobs.client.model.ModelCapuchinMonkey;
import com.github.alexthe666.alexsmobs.client.render.AMRenderTypes;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerCapuchinItem;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityCapuchinMonkey;
import com.github.alexthe666.alexsmobs.entity.ai.EntityAINearestTarget3D;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;


@Mixin(LayerCapuchinItem.class)
public abstract class AMILayerCapuchinItem extends RenderLayer<EntityCapuchinMonkey, ModelCapuchinMonkey> {


    @Shadow @Final public static ModelAncientDart DART_MODEL;

    @Unique
    private static final ResourceLocation TEXTURE_POTION = new ResourceLocation("alexsmobsinteraction:textures/entity/ancient_dart_potion.png");

    public AMILayerCapuchinItem(RenderLayerParent<EntityCapuchinMonkey, ModelCapuchinMonkey> pRenderer) {
        super(pRenderer);
    }

    @ModifyVariable(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILcom/github/alexthe666/alexsmobs/entity/EntityCapuchinMonkey;FFFFFF)V", at = @At(value = "STORE"), ordinal = 0,remap = false)
    private ItemStack alexsMobsInteraction$render(ItemStack itemStack) {
        return AlexsMobsInteraction.COMMON_CONFIG.GOOFY_CAPUCHIN_BOMB_ENABLED.get() ? new ItemStack(Items.TNT) : new ItemStack(Items.COBBLESTONE);
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILcom/github/alexthe666/alexsmobs/entity/EntityCapuchinMonkey;FFFFFF)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V",ordinal = 0))
    private void alexsMobsInteraction$render2(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, EntityCapuchinMonkey entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        int color = ((AncientDartPotion) entitylivingbaseIn).getPotionColor();
        if (color != -1 && AlexsMobsInteraction.COMMON_CONFIG.ANCIENT_EFFECTS_ENABLED.get()) {
            float r = (float) (color >> 16 & 255) / 255.0F;
            float g = (float) (color >> 8 & 255) / 255.0F;
            float b = (float) (color & 255) / 255.0F;
            DART_MODEL.renderToBuffer(matrixStackIn, bufferIn.getBuffer(AMRenderTypes.entityCutoutNoCull(TEXTURE_POTION)), packedLightIn, OverlayTexture.NO_OVERLAY, r, g, b, 1.0F);
        }
    }

}
