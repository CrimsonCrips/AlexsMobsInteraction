//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.rainfrog;

import com.crimsoncrips.alexsmobsinteraction.mobmodification.interfaces.AMITransform;
import com.github.alexthe666.alexsmobs.client.model.ModelRainFrog;
import com.github.alexthe666.alexsmobs.client.render.RenderFly;
import com.github.alexthe666.alexsmobs.client.render.RenderRainFrog;
import com.github.alexthe666.alexsmobs.entity.EntityRainFrog;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.FrogRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderRainFrog.class)
public abstract class AMIRainfrogRendering extends MobRenderer<EntityRainFrog, ModelRainFrog> {

    public AMIRainfrogRendering(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelRainFrog(), 0.2F);
    }

    @Override
    @Shadow
    public ResourceLocation getTextureLocation(EntityRainFrog rainFrog) {
        return null;
    }


    public boolean isShaking(EntityRainFrog frog) {
        AMITransform myAccessor = (AMITransform) frog;
        return myAccessor.isTransforming();
    }

    @Override
    public void setupRotations(EntityRainFrog entityLiving, PoseStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
        if (this.isShaking(entityLiving)) {
            rotationYaw += (float)(Math.cos((double)entityLiving.tickCount * 7.0) * Math.PI * 0.8999999761581421);
            float vibrate = 0.05F;
            matrixStackIn.translate((entityLiving.getRandom().nextFloat() - 0.5F) * vibrate, (entityLiving.getRandom().nextFloat() - 0.5F) * vibrate, (entityLiving.getRandom().nextFloat() - 0.5F) * vibrate);
        }

        super.setupRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);

    }

}
