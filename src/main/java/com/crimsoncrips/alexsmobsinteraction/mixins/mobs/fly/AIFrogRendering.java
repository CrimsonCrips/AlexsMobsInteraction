package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.fly;


import com.crimsoncrips.alexsmobsinteraction.interfaces.AITransform;
import com.github.alexthe666.alexsmobs.client.model.ModelFly;
import com.github.alexthe666.alexsmobs.client.render.RenderFly;
import com.github.alexthe666.alexsmobs.entity.EntityFly;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.FrogModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.FrogRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.frog.Frog;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FrogRenderer.class)

public class AIFrogRendering extends MobRenderer<Frog, FrogModel<Frog>> {
    public AIFrogRendering(EntityRendererProvider.Context p_234619_) {
        super(p_234619_, new FrogModel(p_234619_.bakeLayer(ModelLayers.FROG)), 0.3F);
    }

    @Shadow
    public ResourceLocation getTextureLocation(Frog p_234623_) {
        return p_234623_.getVariant().texture();
    }

    protected boolean isShaking(Frog frog) {
        AITransform myAccessor = (AITransform) frog;
        boolean isTransforming = myAccessor.isTransforming();
        return isTransforming;
    }

    protected void setupRotations(Frog entityLiving, PoseStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
        if (this.isShaking(entityLiving)) {
            rotationYaw += (float)(Math.cos((double)entityLiving.tickCount * 7.0) * Math.PI * 0.8999999761581421);
            float vibrate = 0.05F;
            matrixStackIn.translate((entityLiving.getRandom().nextFloat() - 0.5F) * vibrate, (entityLiving.getRandom().nextFloat() - 0.5F) * vibrate, (entityLiving.getRandom().nextFloat() - 0.5F) * vibrate);
        }

        super.setupRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
    }
}
