package com.crimsoncrips.alexsmobsinteraction.server;

import com.crimsoncrips.alexsmobsinteraction.server.entity.EntityVoidWormDummy;
import com.crimsoncrips.alexsmobsinteraction.server.entity.AMIEntityRegistry;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.datagen.loottables.AMILootTables;
import com.crimsoncrips.alexsmobsinteraction.misc.AMIUtils;
import com.crimsoncrips.alexsmobsinteraction.server.item.AMIItemRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityBananaSlug;
import com.github.alexthe666.alexsmobs.entity.EntityCosmaw;
import com.github.alexthe666.alexsmobs.misc.AMCreativeTabRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntitySkelewag;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

public class AMIModEvents {

    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(AMIEntityRegistry.VOID_WORM_DUMMY.get(), EntityVoidWormDummy.bakeAttributes().build());
    }

    public static void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(AMIPortalTexture.REGISTRY_KEY, AMIPortalTexture.CODEC, AMIPortalTexture.CODEC);
    }


    public static final SpawnPlacementType IN_WATER_OR_LAVA = (level, pos, type) -> {
        if (type == null || !level.getWorldBorder().isWithinBounds(pos))
            return false;
        FluidState fluidState = level.getFluidState(pos);
        BlockPos above = pos.above();
        return (fluidState.is(FluidTags.WATER) || fluidState.is(FluidTags.LAVA)) && !level.getBlockState(above).isRedstoneConductor(level, above);
    };

    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(AMEntityRegistry.SKELEWAG.get(), IN_WATER_OR_LAVA, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (type, level, spawnType, pos, random) -> EntitySkelewag.canSkelewagSpawn(type, level, spawnType, pos, random) || canWitheredSkelewagSpawn(level, pos), RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

    private static boolean canWitheredSkelewagSpawn(ServerLevelAccessor level, BlockPos pos) {
        return AlexsMobsInteraction.COMMON_CONFIG.WITHERED_SKELEWAG_ENABLED.get()
                && level.getDifficulty() != Difficulty.PEACEFUL
                && level.getFluidState(pos).is(FluidTags.LAVA)
                && level.getFluidState(pos.below()).is(FluidTags.LAVA)
                && level.getBiome(pos).is(BiomeTags.IS_NETHER);
    }

    public static void addCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(AMCreativeTabRegistry.TAB.getKey())) {
            event.accept(AMIItemRegistry.ASMON_CROWN.get());
            event.accept(AMIItemRegistry.ASCENDER.get());
        }
    }

}
