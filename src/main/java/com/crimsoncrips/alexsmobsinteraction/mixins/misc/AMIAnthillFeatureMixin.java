package com.crimsoncrips.alexsmobsinteraction.mixins.misc;

import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.crimsoncrips.alexsmobsinteraction.entity.EntityLeafcutterPupa;
import com.crimsoncrips.alexsmobsinteraction.mobmodification.interfaces.AMIVariant;
import com.github.alexthe666.alexsmobs.entity.EntityLeafcutterAnt;
import com.github.alexthe666.alexsmobs.item.ItemLeafcutterPupa;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityLeafcutterAnthill;
import com.github.alexthe666.alexsmobs.world.FeatureLeafcutterAnthill;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
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



    @Inject(method = "place", at = @At(value = "HEAD"),remap = false)
    private void variableAddition(FeaturePlaceContext<NoneFeatureConfiguration> context, CallbackInfoReturnable<Boolean> cir){
        variant = context.level().getRandom().nextBoolean() ? 1 : 2;
    }

    @Inject(method = "place", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/alexsmobs/entity/EntityLeafcutterAnt;setQueen(Z)V"),remap = false,locals = LocalCapture.CAPTURE_FAILSOFT)
    private void variable(FeaturePlaceContext<NoneFeatureConfiguration> context, CallbackInfoReturnable<Boolean> cir, int x, int z, BlockPos pos, int y, BlockPos heightPos, int outOfGround, Random chunkSeedRandom, BlockEntity tileentity, TileEntityLeafcutterAnthill beehivetileentity, int j, int k, EntityLeafcutterAnt beeentity){
        if (AMInteractionConfig.LEAFCUTTER_VARIANTS_ENABLED){
            ((AMIVariant) beeentity).setVariant(variant);
        } else {
            ((AMIVariant) beeentity).setVariant(1);
        }
    }

}