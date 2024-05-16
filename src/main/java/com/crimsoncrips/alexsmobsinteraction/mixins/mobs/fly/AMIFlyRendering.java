package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.fly;


import com.crimsoncrips.alexsmobsinteraction.mobmodification.interfaces.AMITransform;
import com.github.alexthe666.alexsmobs.client.model.ModelFly;
import com.github.alexthe666.alexsmobs.client.render.RenderFly;
import com.github.alexthe666.alexsmobs.entity.EntityFly;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RenderFly.class)

public class AMIFlyRendering  extends MobRenderer<EntityFly, ModelFly> {


    public AMIFlyRendering(EntityRendererProvider.Context pContext, ModelFly pModel, float pShadowRadius) {
        super(pContext, pModel, pShadowRadius);
    }

    @Override
    @Shadow
    public ResourceLocation getTextureLocation(EntityFly pEntity) {
        return null;
    }



    protected boolean isShaking(EntityFly fly) {
        AMITransform myAccessor = (AMITransform) fly;
        boolean isTransforming = myAccessor.isTransforming();
        return isTransforming || fly.isInNether();
    }

    protected void setupRotations(EntityFly entityLiving, PoseStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
        if (this.isShaking(entityLiving)) {
            rotationYaw += (float)(Math.cos((double)entityLiving.tickCount * 7.0) * Math.PI * 0.8999999761581421);
            float vibrate = 0.05F;
            matrixStackIn.translate((entityLiving.getRandom().nextFloat() - 0.5F) * vibrate, (entityLiving.getRandom().nextFloat() - 0.5F) * vibrate, (entityLiving.getRandom().nextFloat() - 0.5F) * vibrate);
        }

        super.setupRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
    }

}
