package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.void_worm;

import com.github.alexthe666.alexsmobs.entity.EntityVoidWorm;
import net.minecraft.server.level.ServerBossEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = EntityVoidWorm.class, remap = false)
public interface AMIVoidWormAccessor {

    @Accessor("bossInfo")
    ServerBossEvent alexsMobsInteraction$getBossInfo();
}
