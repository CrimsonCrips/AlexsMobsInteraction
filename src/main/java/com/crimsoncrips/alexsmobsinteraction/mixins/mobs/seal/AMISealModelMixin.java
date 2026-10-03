package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.seal;

import com.crimsoncrips.alexsmobsinteraction.server.AMIAttachments;
import com.github.alexthe666.alexsmobs.client.model.ModelSeal;
import com.github.alexthe666.alexsmobs.entity.EntitySeal;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelSeal.class)
public abstract class AMISealModelMixin {

    @Inject(method = "setupAnim(Lcom/github/alexthe666/alexsmobs/entity/EntitySeal;FFFFF)V", at = @At("TAIL"), remap = false)
    private void alexsMobsInteraction$setupAnim(EntitySeal seal, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (!seal.getData(AMIAttachments.SPINNING_SEAL))
            return;
        ModelSeal model = (ModelSeal) (Object) this;
        model.resetToDefaultPose();
        alexsMobsInteraction$pose(model.body, 0, 0, 0);
        alexsMobsInteraction$pose(model.tail, 0, 0, 0);
        alexsMobsInteraction$pose(model.head, Mth.HALF_PI, 0, 0);
        alexsMobsInteraction$pivot(model.head, 0.0F, -5.5F, -12.0F);
        alexsMobsInteraction$pose(model.leftArm, Mth.HALF_PI, -Mth.HALF_PI, 0);
        alexsMobsInteraction$pivot(model.leftArm, 7.0F, -1.5F, -4.0F);
        alexsMobsInteraction$pose(model.rightArm, Mth.HALF_PI, Mth.HALF_PI, 0);
        alexsMobsInteraction$pivot(model.rightArm, -7.0F, -1.5F, -4.0F);
    }

    private static void alexsMobsInteraction$pose(AdvancedModelBox box, float x, float y, float z) {
        box.rotateAngleX = x;
        box.rotateAngleY = y;
        box.rotateAngleZ = z;
    }

    private static void alexsMobsInteraction$pivot(AdvancedModelBox box, float x, float y, float z) {
        box.rotationPointX = x;
        box.rotationPointY = y;
        box.rotationPointZ = z;
    }
}
