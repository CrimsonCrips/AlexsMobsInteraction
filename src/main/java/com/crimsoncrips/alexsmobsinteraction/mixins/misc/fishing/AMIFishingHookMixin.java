package com.crimsoncrips.alexsmobsinteraction.mixins.misc.fishing;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.datagen.loottables.AMILootTables;
import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityDevilsHolePupfish;
import com.github.alexthe666.alexsmobs.world.AMWorldData;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.storage.loot.LootDataManager;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;


@Mixin(FishingHook.class)
public abstract class AMIFishingHookMixin {

    @ModifyArg(method = "retrieve", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z",ordinal = 0))
    private Entity alexsMobsInteraction$retrieve1(Entity par1, @Local Player player) {
        if(AlexsMobsInteraction.COMMON_CONFIG.DEVILS_FISHING_INDUSTRY.get() && player.getRandom().nextDouble() < 0.80){
            FishingHook hook = (FishingHook) (Object) this;

            EntityDevilsHolePupfish pupfish = AMEntityRegistry.DEVILS_HOLE_PUPFISH.get().create(player.level());
            double d0 = player.getX() - hook.getX();
            double d1 = player.getY() - hook.getY();
            double d2 = player.getZ() - hook.getZ();
            if (pupfish != null) {
                pupfish.setPos(hook.position().add(0, 0.5, 0));
                pupfish.setDeltaMovement(d0 * 0.2D, d1 * 0.2D + Math.sqrt(Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2)) * 0.16D, d2 * 0.2D);
            }
            ChunkPos devilPos = AMWorldData.get(player.level()).getPupfishChunk();

            if (devilPos != null) {
                if (hook.chunkPosition().equals(devilPos)) {
                    AMIUtils.awardAdvancement(player, "devil_fish", "fish");
                    return pupfish;
                }
            }
        }
        return par1;
    }




}
