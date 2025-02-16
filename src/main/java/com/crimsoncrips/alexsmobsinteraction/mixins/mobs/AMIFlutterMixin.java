package com.crimsoncrips.alexsmobsinteraction.mixins.mobs;

import com.github.alexthe666.alexsmobs.entity.EntityFlutter;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;


@Mixin(EntityFlutter.class)
public abstract class AMIFlutterMixin extends TamableAnimal {


    protected AMIFlutterMixin(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

}
