package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.grizzly_bear;

import com.github.alexthe666.alexsmobs.client.model.ModelGrizzlyBear;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ModelGrizzlyBear.class, remap = false)
public interface AMIGrizzlyBearModelAccessor {

    @Accessor("hat")
    AdvancedModelBox alexsMobsInteraction$getHat();

    @Accessor("microphone")
    AdvancedModelBox alexsMobsInteraction$getMicrophone();
}
