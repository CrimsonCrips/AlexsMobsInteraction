package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.farseer;

import com.github.alexthe666.alexsmobs.entity.EntityFarseer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = EntityFarseer.class, remap = false)
public interface AMIFarseerAccessor {

    @Invoker("canUseLaser")
    boolean alexsMobsInteraction$canUseLaser();

    @Accessor("faceCameraProgress")
    void alexsMobsInteraction$setFaceCameraProgress(float progress);

    @Accessor("prevFaceCameraProgress")
    void alexsMobsInteraction$setPrevFaceCameraProgress(float progress);
}
