package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.portal;

import java.util.Map;
import java.util.HashMap;
import javax.annotation.Nullable;
import net.minecraft.world.level.Level;
import com.crimsoncrips.alexsmobsinteraction.server.AMIPortalTexture;
import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.server.AMIAttachments;
import com.github.alexthe666.alexsmobs.client.render.RenderVoidPortal;
import com.github.alexthe666.alexsmobs.entity.EntityVoidPortal;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(RenderVoidPortal.class)
public abstract class AMIVoidPortalRenderMixin extends EntityRenderer<EntityVoidPortal> {


    @Shadow @Final private static ResourceLocation TEXTURE_0;
    @Shadow @Final private static ResourceLocation TEXTURE_1;
    @Shadow @Final private static ResourceLocation TEXTURE_2;
    @Shadow @Final private static ResourceLocation[] TEXTURE_PROGRESS;

    @Shadow @Final private static ResourceLocation TEXTURE_SHATTERED_0;
    @Shadow @Final private static ResourceLocation TEXTURE_SHATTERED_1;
    @Shadow @Final private static ResourceLocation TEXTURE_SHATTERED_2;
    @Shadow @Final private static ResourceLocation[] TEXTURE_SHATTERED_PROGRESS;

    private static final Map<ResourceLocation, ResourceLocation[]> IDLE_TEXTURES = new HashMap<>();

    private static final Map<ResourceLocation, ResourceLocation[]> GROW_TEXTURES = new HashMap<>();

    protected AMIVoidPortalRenderMixin(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @WrapOperation(method = "renderPortal", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/alexsmobs/client/render/RenderVoidPortal;getIdleTexture(IZ)Lnet/minecraft/resources/ResourceLocation;"),remap = false)
    private ResourceLocation alexsMobsInteraction$renderPortal(RenderVoidPortal instance, int age, boolean shattered, Operation<ResourceLocation> original, @Local (argsOnly = true) EntityVoidPortal entityVoidPortal) {
        return getModifiedIdleTexture(age,shattered,entityVoidPortal);
    }

    @WrapOperation(method = "renderPortal", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/alexsmobs/client/render/RenderVoidPortal;getGrowingTexture(IZ)Lnet/minecraft/resources/ResourceLocation;"),remap = false)
    private ResourceLocation alexsMobsInteraction$renderPortal1(RenderVoidPortal instance, int age, boolean shattered, Operation<ResourceLocation> original, @Local (argsOnly = true) EntityVoidPortal entityVoidPortal) {
        return getModifiedGrowingTexture(age,shattered,entityVoidPortal);
    }

    public ResourceLocation getModifiedIdleTexture(int age, boolean shattered, EntityVoidPortal entityVoidPortal) {
        int frame = age < 3 ? 0 : age < 6 ? 1 : age < 10 ? 2 : 0;
        if (shattered)
            return frame == 0 ? TEXTURE_SHATTERED_0 : frame == 1 ? TEXTURE_SHATTERED_1 : TEXTURE_SHATTERED_2;
        AMIPortalTexture portalTexture = alexsMobsInteraction$portalTexture(entityVoidPortal);
        if (portalTexture == null)
            return frame == 0 ? TEXTURE_0 : frame == 1 ? TEXTURE_1 : TEXTURE_2;
        return IDLE_TEXTURES.computeIfAbsent(portalTexture.texture(), texture -> {
            ResourceLocation[] frames = new ResourceLocation[3];
            for (int i = 0; i < frames.length; i++)
                frames[i] = portalTexture.idleTexture(i);
            return frames;
        })[frame];
    }

    public ResourceLocation getModifiedGrowingTexture(int age, boolean shattered, EntityVoidPortal entityVoidPortal) {
        int frame = Mth.clamp(age, 0, 9);
        if (shattered)
            return TEXTURE_SHATTERED_PROGRESS[frame];
        AMIPortalTexture portalTexture = alexsMobsInteraction$portalTexture(entityVoidPortal);
        if (portalTexture == null)
            return TEXTURE_PROGRESS[frame];
        return GROW_TEXTURES.computeIfAbsent(portalTexture.texture(), texture -> {
            ResourceLocation[] frames = new ResourceLocation[10];
            for (int i = 0; i < frames.length; i++)
                frames[i] = portalTexture.growTexture(i);
            return frames;
        })[frame];
    }

    @Nullable
    private static AMIPortalTexture alexsMobsInteraction$portalTexture(EntityVoidPortal entityVoidPortal) {
        ResourceLocation dimension = ResourceLocation.tryParse(entityVoidPortal.getData(AMIAttachments.PORTAL_DIMENSION));
        if (dimension == null || entityVoidPortal.getData(AMIAttachments.PORTAL_DIMENSION).isEmpty())
            return null;
        int mode = dimension.equals(Level.NETHER.location()) ? AlexsMobsInteraction.CLIENT_CONFIG.NETHER_PORTAL_VARIANT.get()
                : dimension.equals(Level.END.location()) ? AlexsMobsInteraction.CLIENT_CONFIG.END_PORTAL_VARIANT.get()
                : AMIPortalTexture.MODE_AUTO;
        return AMIPortalTexture.resolve(entityVoidPortal.level().registryAccess(), dimension, mode).orElse(null);
    }
}
