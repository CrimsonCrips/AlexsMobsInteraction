package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.kangaroo;

import com.github.alexthe666.alexsmobs.client.model.ModelKangaroo;
import com.github.alexthe666.alexsmobs.client.render.layer.LayerKangarooItem;
import com.github.alexthe666.alexsmobs.entity.EntityKangaroo;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;


@Mixin(LayerKangarooItem.class)
public abstract class AMIKangarooItemLayerMixin extends RenderLayer<EntityKangaroo, ModelKangaroo> {


    @Shadow protected abstract void translateToHand(PoseStack matrixStack, boolean left);

    public AMIKangarooItemLayerMixin(RenderLayerParent<EntityKangaroo, ModelKangaroo> pRenderer) {
        super(pRenderer);
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILcom/github/alexthe666/alexsmobs/entity/EntityKangaroo;FFFFFF)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V", ordinal = 1))
    private void registerGoals(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, EntityKangaroo entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci, @Local boolean left, @Local ItemInHandRenderer renderer) {
        ItemStack itemstackOF = entitylivingbaseIn.getItemBySlot(EquipmentSlot.OFFHAND);
        this.translateToHand(matrixStackIn, !left);
        matrixStackIn.translate(0F, 0.75F, -0.125F);

        matrixStackIn.mulPose(Axis.XP.rotationDegrees(-110F));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(180F));
        matrixStackIn.scale(0.8F, 0.8F, 0.8F);
        renderer.renderItem(entitylivingbaseIn, itemstackOF, left ? ItemDisplayContext.THIRD_PERSON_LEFT_HAND : ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, false, matrixStackIn, bufferIn, packedLightIn);


    }


}
