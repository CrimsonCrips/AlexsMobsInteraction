package com.crimsoncrips.alexsmobsinteraction.datagen;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.server.AMIPortalTexture;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.List;

public class AMIPortalTextures {

    public static void bootstrap(BootstrapContext<AMIPortalTexture> context) {
        register(context, "overworld", Level.OVERWORLD, "overworld/overworld", List.of(), 0);
        register(context, "nether", Level.NETHER, "nether/nether", List.of(), 0);
        register(context, "end", Level.END, "end/end", List.of(), 0);
        register(context, "better_nether", Level.NETHER, "better_nether/better_nether", List.of("betternether"), 1);
        register(context, "better_end", Level.END, "better_end/better_end", List.of("betterend"), 1);
    }

    private static void register(BootstrapContext<AMIPortalTexture> context, String name, ResourceKey<Level> dimension, String texture, List<String> requiredMods, int priority) {
        context.register(ResourceKey.create(AMIPortalTexture.REGISTRY_KEY, AlexsMobsInteraction.prefix(name)),
                new AMIPortalTexture(dimension, AlexsMobsInteraction.prefix("textures/entity/portal/" + texture), requiredMods, priority));
    }
}
