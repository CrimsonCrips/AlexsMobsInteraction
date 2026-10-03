package com.crimsoncrips.alexsmobsinteraction.server;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public record AMIPortalTexture(ResourceKey<Level> dimension, ResourceLocation texture, List<String> requiredMods, int priority) {

    public static final ResourceKey<Registry<AMIPortalTexture>> REGISTRY_KEY = ResourceKey.createRegistryKey(AlexsMobsInteraction.prefix("portal_texture"));

    public static final Codec<AMIPortalTexture> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(AMIPortalTexture::dimension),
            ResourceLocation.CODEC.fieldOf("texture").forGetter(AMIPortalTexture::texture),
            Codec.STRING.listOf().optionalFieldOf("required_mods", List.of()).forGetter(AMIPortalTexture::requiredMods),
            Codec.INT.optionalFieldOf("priority", 0).forGetter(AMIPortalTexture::priority)
    ).apply(instance, AMIPortalTexture::new));

    public static final int MODE_AUTO = 0;

    public static final int MODE_BASE = 1;

    public static final int MODE_ALTERNATE = 2;

    public ResourceLocation idleTexture(int frame) {
        return this.texture.withSuffix("_idle_" + frame + ".png");
    }

    public ResourceLocation growTexture(int frame) {
        return this.texture.withSuffix("_grow_" + frame + ".png");
    }

    public static Optional<AMIPortalTexture> resolve(RegistryAccess registryAccess, ResourceLocation dimension, int mode) {
        return registryAccess.registry(REGISTRY_KEY).flatMap(registry -> registry.stream()
                .filter(entry -> entry.dimension().location().equals(dimension))
                .filter(entry -> switch (mode) {
                    case MODE_BASE -> entry.requiredMods().isEmpty();
                    case MODE_ALTERNATE -> true;
                    default -> entry.requiredMods().stream().allMatch(ModList.get()::isLoaded);
                })
                .max(Comparator.comparingInt(AMIPortalTexture::priority)));
    }
}
