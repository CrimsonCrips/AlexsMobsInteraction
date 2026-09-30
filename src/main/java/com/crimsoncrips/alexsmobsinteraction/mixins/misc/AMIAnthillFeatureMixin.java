package com.crimsoncrips.alexsmobsinteraction.mixins.misc;

import com.crimsoncrips.alexsmobsinteraction.server.AMIAttachments;
import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.github.alexthe666.alexsmobs.entity.EntityLeafcutterAnt;
import com.github.alexthe666.alexsmobs.world.FeatureLeafcutterAnthill;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.llamalad7.mixinextras.sugar.Local;



@Mixin(FeatureLeafcutterAnthill.class)
public abstract class AMIAnthillFeatureMixin {

    int variant;



    @Inject(method = "place", at = @At(value = "HEAD"))
    private void alexsMobsInteraction$place(FeaturePlaceContext<NoneFeatureConfiguration> context, CallbackInfoReturnable<Boolean> cir){
        variant = context.level().getRandom().nextBoolean() ? 1 : 2;
    }

    @Inject(method = "place", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/alexsmobs/entity/EntityLeafcutterAnt;setQueen(Z)V"))
    private void alexsMobsInteraction$place1(FeaturePlaceContext<NoneFeatureConfiguration> context, CallbackInfoReturnable<Boolean> cir, @Local EntityLeafcutterAnt beeentity){
        if (AlexsMobsInteraction.COMMON_CONFIG.ANT_WAR_ENABLED.get()){
            beeentity.setData(AMIAttachments.VARIANT, variant);
        } else {
            beeentity.setData(AMIAttachments.VARIANT, 1);
        }
    }

}