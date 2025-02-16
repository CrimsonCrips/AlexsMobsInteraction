package com.crimsoncrips.alexsmobsinteraction.mixins.misc;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.server.enchantment.AMIEnchantmentRegistry;
import com.github.alexmodguy.alexscaves.server.entity.living.WatcherEntity;
import com.github.alexthe666.alexsmobs.world.AMWorldRegistry;
import com.github.alexthe666.citadel.config.biome.SpawnBiomeData;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.fml.ModList;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;


@Mixin(AMWorldRegistry.class)
public abstract class AMIWorldRegistryMixin {

    @WrapOperation(method = "addBiomeSpawns", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/alexsmobs/world/AMWorldRegistry;testBiome(Lorg/apache/commons/lang3/tuple/Pair;Lnet/minecraft/core/Holder;)Z",ordinal = 73),remap = false)
    private static boolean variableAddition(Pair<String, SpawnBiomeData> e, Holder<Biome> entry, Operation<Boolean> original, @Local(argsOnly = true) Holder<Biome> biome){
        if (AlexsMobsInteraction.COMMON_CONFIG.WITHERED_SKELEWAG_ENABLED.get()) {
            return original.call(e, entry) || biome.is(BiomeTags.IS_NETHER);
        }
        return original.call(e, entry);
    }


}