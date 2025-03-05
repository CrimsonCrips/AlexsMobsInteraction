package com.crimsoncrips.alexsmobsinteraction.mixins.misc;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.AMIBaseInterfaces;
import com.github.alexthe666.alexsmobs.entity.EntityLeafcutterAnt;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityLeafcutterAnthill;
import com.github.alexthe666.alexsmobs.world.FeatureLeafcutterAnthill;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Random;


@Mixin(FeatureLeafcutterAnthill.class)
public abstract class AMIAnthillFeatureMixin {

    int variant;



    @Inject(method = "place", at = @At(value = "HEAD"))
    private void variableAddition(FeaturePlaceContext<NoneFeatureConfiguration> context, CallbackInfoReturnable<Boolean> cir){
        variant = context.level().getRandom().nextBoolean() ? 1 : 2;
    }

    @Inject(method = "place", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/alexsmobs/entity/EntityLeafcutterAnt;setQueen(Z)V"),locals = LocalCapture.CAPTURE_FAILSOFT)
    private void variable(FeaturePlaceContext<NoneFeatureConfiguration> context, CallbackInfoReturnable<Boolean> cir, int x, int z, BlockPos pos, int y, BlockPos heightPos, int outOfGround, Random chunkSeedRandom, BlockEntity tileentity, TileEntityLeafcutterAnthill beehivetileentity, int j, int k, EntityLeafcutterAnt beeentity){
        if (AlexsMobsInteraction.COMMON_CONFIG.LEAFCUTTER_VARIANTS_ENABLED.get()){
            ((AMIBaseInterfaces) beeentity).setVariant(variant);
        } else {
            ((AMIBaseInterfaces) beeentity).setVariant(1);
        }
    }

}