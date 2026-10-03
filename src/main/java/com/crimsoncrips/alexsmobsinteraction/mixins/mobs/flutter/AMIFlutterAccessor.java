package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.flutter;

import com.github.alexthe666.alexsmobs.entity.EntityFlutter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = EntityFlutter.class, remap = false)
public interface AMIFlutterAccessor {

    @Invoker("setupShooting")
    void alexsMobsInteraction$setupShooting();
}
