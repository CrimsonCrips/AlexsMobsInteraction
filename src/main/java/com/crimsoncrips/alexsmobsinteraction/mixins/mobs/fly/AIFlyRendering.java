package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.fly;


import com.crimsoncrips.alexsmobsinteraction.interfaces.AIFlyInterface;
import com.github.alexthe666.alexsmobs.client.model.ModelFly;
import com.github.alexthe666.alexsmobs.client.render.RenderFly;
import com.github.alexthe666.alexsmobs.entity.EntityFly;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderFly.class)

public class AIFlyRendering  extends MobRenderer<EntityFly, ModelFly> {


    public AIFlyRendering(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelFly(), 0.2F);
    }

    @Override
    @Shadow
    public ResourceLocation getTextureLocation(EntityFly pEntity) {
        return null;
    }



    protected boolean isShaking(EntityFly fly) {
        AIFlyInterface myAccessor = (AIFlyInterface) fly;
        boolean flyIsSick = myAccessor.flyIsSick();
        return flyIsSick || fly.isInNether();
    }
}
