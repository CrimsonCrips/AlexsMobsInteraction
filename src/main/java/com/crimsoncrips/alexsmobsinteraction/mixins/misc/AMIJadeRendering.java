package com.crimsoncrips.alexsmobsinteraction.mixins.misc;

import com.crimsoncrips.alexsmobsinteraction.server.AMIAttachments;
import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import snownee.jade.JadeClient;
import snownee.jade.addon.vanilla.VanillaPlugin;
import snownee.jade.api.Accessor;
import snownee.jade.api.BlockAccessor;


@Mixin(JadeClient.class)
public abstract class AMIJadeRendering {

    @ModifyReturnValue(method = "builtInOverrides", at = @At("RETURN"),remap = false)
    private static Accessor<?> alexsMobsInteraction$builtInOverrides(Accessor<?> original,@Local (ordinal = 0) Accessor<?> accessor) {
        if(!AlexsMobsInteraction.CLIENT_CONFIG.PHOTOSENSITIVITY_ENABLED.get() && Minecraft.getInstance().player.getData(AMIAttachments.ALTER_TIME) != 0 && accessor instanceof BlockAccessor){
            BlockAccessor.Builder builder = VanillaPlugin.CLIENT_REGISTRATION.blockAccessor().from((BlockAccessor) accessor).blockEntity(() -> null);
            return builder.blockState(Blocks.BEDROCK.defaultBlockState()).build();
        }
        return original;
    }

}
