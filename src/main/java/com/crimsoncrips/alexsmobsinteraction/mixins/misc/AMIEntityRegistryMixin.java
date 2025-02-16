package com.crimsoncrips.alexsmobsinteraction.mixins.misc;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnPlacements;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;


@Mixin(AMEntityRegistry.class)
public abstract class AMIEntityRegistryMixin {

    @ModifyArg(method = "initializeAttributes", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/SpawnPlacements;register(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/entity/SpawnPlacements$Type;Lnet/minecraft/world/level/levelgen/Heightmap$Types;Lnet/minecraft/world/entity/SpawnPlacements$SpawnPredicate;)V",ordinal = 72), index = 1)
    private static SpawnPlacements.Type alexsMobsInteraction$initializeAttributes(SpawnPlacements.Type pDecoratorType) {
        return SpawnPlacements.Type.NO_RESTRICTIONS;
    }

}