package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.bald_eagle;

import com.github.alexthe666.alexsmobs.client.model.ModelBaldEagle;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ModelBaldEagle.class)
public interface AMIBaldEagleModelAccessor {

    @Accessor(value = "root", remap = false)
    AdvancedModelBox alexsMobsInteraction$getRoot();

    @Accessor(value = "body", remap = false)
    AdvancedModelBox alexsMobsInteraction$getBody();
}
