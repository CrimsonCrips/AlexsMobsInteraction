package com.crimsoncrips.alexsmobsinteraction.mixins.misc;

import com.crimsoncrips.alexsmobsinteraction.server.AMIAttachments;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.core.component.DataComponents;
import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.github.alexthe666.alexsmobs.entity.EntityVoidPortal;
import com.github.alexthe666.alexsmobs.item.ItemDimensionalCarver;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ItemDimensionalCarver.class)
public abstract class AMIDimensionalCarverMixin extends Item {




    public AMIDimensionalCarverMixin(Properties pProperties) {
        super(pProperties);
    }

    @Inject(method = "onPortalOpen", at = @At("HEAD"),cancellable = true,remap = false)
    private void alexsMobsInteraction$onPortalOpen(Level worldIn, LivingEntity player, EntityVoidPortal portal, Direction dir, CallbackInfo ci) {
        ci.cancel();
        portal.setLifespan(AMIUtils.seconds(60));
        ItemStack itemStack = player.getOffhandItem();
        if (player instanceof ServerPlayer serverPlayer && itemStack.getItem() instanceof CompassItem && AlexsMobsInteraction.COMMON_CONFIG.DIMENSIONAL_LODESTONE_ENABLED.get()){
            LodestoneTracker lodestoneTracker = itemStack.get(DataComponents.LODESTONE_TRACKER);
            if (lodestoneTracker != null && lodestoneTracker.target().isPresent()){
                if(!serverPlayer.isCreative() && AlexsMobsInteraction.COMMON_CONFIG.CONSUME_COMPASS_ENABLED.get()){
                    itemStack.shrink(1);
                }
                GlobalPos globalLodestone = lodestoneTracker.target().get();
                portal.exitDimension = globalLodestone.dimension();
                portal.setData(AMIAttachments.PORTAL_DIMENSION, portal.exitDimension.location().toString());
                portal.setDestination(globalLodestone.pos().above(3));
                if (globalLodestone.dimension() != player.level().dimension()){
                    AMIUtils.awardAdvancement(player, "multidimensional_lodestone", "dimension");
                }
                AMIUtils.awardAdvancement(player, "dimensional_lodestone", "lodestone");
                String result = portal.exitDimension.location().toString();

                //Used to determine string for dimension
                System.out.println(result);
            }
        } else {
            ResourceKey<Level> respawnDimension = Level.OVERWORLD;
            BlockPos respawnPosition = player.getSleepingPos().isPresent() ? player.getSleepingPos().get() : player.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, BlockPos.ZERO);
            if (player instanceof ServerPlayer serverPlayer) {
                respawnDimension = serverPlayer.getRespawnDimension();
                if (serverPlayer.getRespawnPosition() != null) {
                    respawnPosition = serverPlayer.getRespawnPosition();
                }
            }

            portal.exitDimension = respawnDimension;
            portal.setData(AMIAttachments.PORTAL_DIMENSION, portal.exitDimension.location().toString());
            portal.setDestination(respawnPosition.above(2));
        }

    }

    @ModifyConstant(method = "onUseTick",constant = @Constant(intValue = 1))
    private int alexsMobsInteraction$onUseTick(int amount, @Local(ordinal = 0, argsOnly = true) LivingEntity player) {
        MobEffectInstance haste = player.getEffect(MobEffects.DIG_SPEED);
        if (AlexsMobsInteraction.COMMON_CONFIG.HASTY_CARVING_ENABLED.get()) {
            AMIUtils.awardAdvancement(player, "hasty_carving", "haste");
            return amount + (haste != null ? (haste.getAmplifier() == 0 ? 100 : 150) : 0);
        }
        return amount;
    }




}
