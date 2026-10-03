package com.crimsoncrips.alexsmobsinteraction.client.renderer;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.server.item.AMIItemRegistry;
import com.github.alexthe666.alexsmobs.client.render.AMRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.model.pipeline.VertexConsumerWrapper;

@OnlyIn(Dist.CLIENT)
public class AscenderItemRenderer extends BlockEntityWithoutLevelRenderer {

    public static final ModelResourceLocation STATIC_MODEL = ModelResourceLocation.standalone(AlexsMobsInteraction.prefix("item/ascender_static"));
    public static final ModelResourceLocation BORDER_MODEL = ModelResourceLocation.standalone(AlexsMobsInteraction.prefix("item/ascender_border"));
    private static final float BORDER_DEPTH_SCALE = 1.02F;
    private static final float STATIC_ZOOM = 1.0F;

    private static AscenderItemRenderer instance;

    public AscenderItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    public static void registerModels(ModelEvent.RegisterAdditional event) {
        event.register(STATIC_MODEL);
        event.register(BORDER_MODEL);
    }

    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (instance == null) {
                    instance = new AscenderItemRenderer();
                }
                return instance;
            }
        }, AMIItemRegistry.ASCENDER.get());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ModelManager modelManager = Minecraft.getInstance().getModelManager();
        BakedModel staticModel = modelManager.getModel(STATIC_MODEL);
        BakedModel borderModel = modelManager.getModel(BORDER_MODEL);

        itemRenderer.renderModelLists(staticModel, stack, packedLight, packedOverlay, poseStack, buffer.getBuffer(Sheets.cutoutBlockSheet()));
        if (!AlexsMobsInteraction.CLIENT_CONFIG.PHOTOSENSITIVITY_ENABLED.get()) {
            itemRenderer.renderModelLists(staticModel, stack, packedLight, packedOverlay, poseStack, new SpriteLocalUvConsumer(buffer.getBuffer(AMRenderTypes.STATIC_ENTITY), staticModel.getParticleIcon(), STATIC_ZOOM));
        }

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.scale(1.0F, 1.0F, BORDER_DEPTH_SCALE);
        poseStack.translate(-0.5F, -0.5F, -0.5F);
        itemRenderer.renderModelLists(borderModel, stack, packedLight, packedOverlay, poseStack, buffer.getBuffer(Sheets.cutoutBlockSheet()));
        poseStack.popPose();
    }

    private static class SpriteLocalUvConsumer extends VertexConsumerWrapper {
        private final TextureAtlasSprite sprite;
        private final float zoom;

        private SpriteLocalUvConsumer(VertexConsumer parent, TextureAtlasSprite sprite, float zoom) {
            super(parent);
            this.sprite = sprite;
            this.zoom = zoom;
        }

        @Override
        public VertexConsumer setUv(float u, float v) {
            float localU = (u - this.sprite.getU0()) / (this.sprite.getU1() - this.sprite.getU0());
            float localV = (v - this.sprite.getV0()) / (this.sprite.getV1() - this.sprite.getV0());
            return super.setUv(localU * this.zoom, localV * this.zoom);
        }
    }
}
