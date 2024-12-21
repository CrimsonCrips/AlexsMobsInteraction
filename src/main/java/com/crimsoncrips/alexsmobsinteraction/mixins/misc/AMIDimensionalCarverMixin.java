package com.crimsoncrips.alexsmobsinteraction.mixins.misc;

import com.crimsoncrips.alexsmobsinteraction.config.AMInteractionConfig;
import com.github.alexthe666.alexsmobs.entity.EntityVoidPortal;
import com.github.alexthe666.alexsmobs.item.ItemDimensionalCarver;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ItemDimensionalCarver.class)
public abstract class AMIDimensionalCarverMixin extends Item {




    public AMIDimensionalCarverMixin(Properties pProperties) {
        super(pProperties);
    }

    @Inject(method = "onPortalOpen", at = @At("HEAD"),cancellable = true,remap = false)
    private void onPortalOpen(Level worldIn, LivingEntity player, EntityVoidPortal portal, Direction dir, CallbackInfo ci) {
        ci.cancel();
        GlobalPos globalLodestone;

        portal.setLifespan(1200);
        ResourceKey<Level> respawnDimension = Level.OVERWORLD;
        BlockPos respawnPosition = player.getSleepingPos().isPresent() ? player.getSleepingPos().get() : player.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, BlockPos.ZERO);
        if (player instanceof ServerPlayer serverPlayer) {
            ItemStack offhandItem = serverPlayer.getOffhandItem();

            if (AMInteractionConfig.DIMENSIONAL_LODESTONE_ENABLED && offhandItem.getItem() == Items.COMPASS){
                CompoundTag tag = offhandItem.getTag();
                if (tag != null){
                    globalLodestone = CompassItem.getLodestonePosition(tag);
                    if (globalLodestone == null)
                        return;
                    BlockPos lodestonePos = globalLodestone.pos();
                    portal.exitDimension = globalLodestone.dimension();
                    portal.setDestination(lodestonePos.above(2));
                } else {
                    respawnDimension = serverPlayer.getRespawnDimension();
                    if (serverPlayer.getRespawnPosition() != null) {
                        respawnPosition = serverPlayer.getRespawnPosition();
                    }
                }
            } else {
                respawnDimension = serverPlayer.getRespawnDimension();
                if (serverPlayer.getRespawnPosition() != null) {
                    respawnPosition = serverPlayer.getRespawnPosition();
                }
            }
        }

        portal.exitDimension = respawnDimension;
        portal.setDestination(respawnPosition.above(2));


    }




}
