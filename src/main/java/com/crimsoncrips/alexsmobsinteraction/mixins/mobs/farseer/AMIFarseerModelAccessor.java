package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.farseer;

import com.github.alexthe666.alexsmobs.client.model.ModelFarseer;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ModelFarseer.class, remap = false)
public interface AMIFarseerModelAccessor {

    @Accessor("head")
    AdvancedModelBox alexsMobsInteraction$getHead();

    @Accessor("bodyCube1")
    AdvancedModelBox alexsMobsInteraction$getBodyCube1();

    @Accessor("bodyCube2")
    AdvancedModelBox alexsMobsInteraction$getBodyCube2();
}
