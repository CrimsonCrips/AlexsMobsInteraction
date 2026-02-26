package com.crimsoncrips.alexsmobsinteraction.mixins.misc.fishing;

import com.crimsoncrips.alexsmobsinteraction.datagen.loottables.AMILootTables;
import com.crimsoncrips.alexsmobsinteraction.server.enchantment.AMIEnchantmentRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityBaldEagle;
import com.github.alexthe666.alexsmobs.item.ItemEcholocator;
import com.github.alexthe666.alexsmobs.world.AMWorldData;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootDataManager;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;


@Mixin(FishingHook.class)
public abstract class AMIFishingHookMixin {

    @WrapOperation(method = "retrieve", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/LootDataManager;getLootTable(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/world/level/storage/loot/LootTable;"),remap = false)
    private LootTable alexsMobsInteraction$retrieve(LootDataManager instance, ResourceLocation resourceLocation, Operation<LootTable> original) {
        FishingHook hook = (FishingHook) (Object)this;
        ChunkPos devilPos = AMWorldData.get(hook.level()).getPupfishChunk();
        LootTable devilLootTable = hook.level().getServer().getLootData().getLootTable(AMILootTables.DEVIL_LOOTTABLE);

        if (devilPos != null) {
            if (hook.chunkPosition().equals(devilPos)) {
                return devilLootTable;
            }
        }
        return original.call(instance, resourceLocation);
    }


}
