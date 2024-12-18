//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.vanillamob.frog;

import com.crimsoncrips.alexsmobsinteraction.mobmodification.interfaces.AMITransform;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.FrogModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.FrogRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.world.entity.animal.frog.Frog;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FrogRenderer.class)
public abstract class AMIFrogRendering extends MobRenderer<Frog, FrogModel<Frog>> {


    public AMIFrogRendering(EntityRendererProvider.Context pContext, FrogModel<Frog> pModel, float pShadowRadius) {
        super(pContext, pModel, pShadowRadius);
    }

    public boolean isShaking(Frog frog) {
        AMITransform myAccessor = (AMITransform) frog;
        return myAccessor.isTransforming();
    }


    public void setupRotations(Frog frog, PoseStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
        if (this.isShaking(frog)) {
            rotationYaw += (float)(Math.cos((double)frog.tickCount * 7.0) * Math.PI * 0.8999999761581421);
            float vibrate = 0.05F;
            matrixStackIn.translate((frog.getRandom().nextFloat() - 0.5F) * vibrate, (frog.getRandom().nextFloat() - 0.5F) * vibrate, (frog.getRandom().nextFloat() - 0.5F) * vibrate);
        }

        super.setupRotations(frog, matrixStackIn, ageInTicks, rotationYaw, partialTicks);

    }
}

