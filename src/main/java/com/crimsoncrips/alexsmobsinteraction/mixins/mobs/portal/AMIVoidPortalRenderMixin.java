package com.crimsoncrips.alexsmobsinteraction.mixins.mobs.portal;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.crimsoncrips.alexsmobsinteraction.misc.interfaces.AMIBaseInterfaces;
import com.github.alexthe666.alexsmobs.client.render.RenderVoidPortal;
import com.github.alexthe666.alexsmobs.entity.EntityVoidPortal;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;


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

    private static final ResourceLocation OVERWORLD_0 = new ResourceLocation("alexsmobsinteraction:textures/entity/portal/overworld/overworld_idle_0.png");
    private static final ResourceLocation OVERWORLD_1 = new ResourceLocation("alexsmobsinteraction:textures/entity/portal/overworld/overworld_idle_1.png");
    private static final ResourceLocation OVERWORLD_2 = new ResourceLocation("alexsmobsinteraction:textures/entity/portal/overworld/overworld_idle_2.png");
    private static final ResourceLocation[] OVERWORLD_PROGRESS = new ResourceLocation[10];

    private static final ResourceLocation NETHER_0 = new ResourceLocation("alexsmobsinteraction:textures/entity/portal/nether/nether_idle_0.png");
    private static final ResourceLocation NETHER_1 = new ResourceLocation("alexsmobsinteraction:textures/entity/portal/nether/nether_idle_1.png");
    private static final ResourceLocation NETHER_2 = new ResourceLocation("alexsmobsinteraction:textures/entity/portal/nether/nether_idle_2.png");
    private static final ResourceLocation[] NETHER_PROGRESS = new ResourceLocation[10];

    private static final ResourceLocation THE_END_0 = new ResourceLocation("alexsmobsinteraction:textures/entity/portal/the_end/the_end_idle_0.png");
    private static final ResourceLocation THE_END_1 = new ResourceLocation("alexsmobsinteraction:textures/entity/portal/the_end/the_end_idle_1.png");
    private static final ResourceLocation THE_END_2 = new ResourceLocation("alexsmobsinteraction:textures/entity/portal/the_end/the_end_idle_2.png");
    private static final ResourceLocation[] THE_END_PROGRESS = new ResourceLocation[10];

    protected AMIVoidPortalRenderMixin(EntityRendererProvider.Context pContext) {
        super(pContext);

        for(int i = 0; i < 10; ++i) {
            OVERWORLD_PROGRESS[i] = new ResourceLocation("alexsmobsinteraction:textures/entity/portal/overworld/overworld_grow_" + i + ".png");
            NETHER_PROGRESS[i] = new ResourceLocation("alexsmobsinteraction:textures/entity/portal/nether/nether_grow_" + i + ".png");
            THE_END_PROGRESS[i] = new ResourceLocation("alexsmobsinteraction:textures/entity/portal/the_end/the_end_grow_" + i + ".png");
        }
    }

    @WrapOperation(method = "renderPortal", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/alexsmobs/client/render/RenderVoidPortal;getIdleTexture(IZ)Lnet/minecraft/resources/ResourceLocation;"),remap = false)
    private ResourceLocation alexsMobsInteraction$renderPortal(RenderVoidPortal instance, int age, boolean shattered, Operation<ResourceLocation> original, @Local (argsOnly = true) EntityVoidPortal entityVoidPortal) {
        return getModifiedIdleTexture(age,shattered,entityVoidPortal);
    }

    @WrapOperation(method = "renderPortal", at = @At(value = "INVOKE", target = "Lcom/github/alexthe666/alexsmobs/client/render/RenderVoidPortal;getGrowingTexture(IZ)Lnet/minecraft/resources/ResourceLocation;"),remap = false)
    private ResourceLocation alexsMobsInteraction$renderPortal1(RenderVoidPortal instance, int age, boolean shattered, Operation<ResourceLocation> original, @Local (argsOnly = true) EntityVoidPortal entityVoidPortal) {
        return getModifiedIdleTexture(age,shattered,entityVoidPortal);
    }


    public ResourceLocation getModifiedIdleTexture(int age, boolean shattered,EntityVoidPortal entityVoidPortal) {
        int portalVariant = shattered ? -1 : ((AMIBaseInterfaces)entityVoidPortal).getVariant();
        if (age < 3) {
            return idlePortalDeterminer(portalVariant,0);
        } else if (age < 6) {
            return idlePortalDeterminer(portalVariant,1);
        } else if (age < 10) {
            return idlePortalDeterminer(portalVariant,2);
        } else {
            return idlePortalDeterminer(portalVariant,0);
        }



    }

    public ResourceLocation getModifiedGrowingTexture(int age, boolean shattered, EntityVoidPortal entityVoidPortal) {
        return portalDeterminer(((AMIBaseInterfaces)entityVoidPortal).getVariant())[Mth.clamp(age, 0, 9)];
    }


    public ResourceLocation[] portalDeterminer(int portalVar){
        return switch (portalVar) {
            case -1 -> TEXTURE_SHATTERED_PROGRESS;
            case 1 -> OVERWORLD_PROGRESS;
            case 2 -> NETHER_PROGRESS;
            case 3 -> THE_END_PROGRESS;
            default -> TEXTURE_PROGRESS;
        };
    }

    public ResourceLocation idlePortalDeterminer(int portalVar,int no){
        switch (portalVar) {
            case -1 -> {
                return no == 0 ? TEXTURE_SHATTERED_0 : (no == 1 ? TEXTURE_SHATTERED_1 : TEXTURE_SHATTERED_2);
            }
            case 1 -> {
                return no == 0 ? OVERWORLD_0 : (no == 1 ? OVERWORLD_1 : OVERWORLD_2);
            }
            case 2 -> {
                return no == 0 ? NETHER_0 : (no == 1 ? NETHER_1 : NETHER_2);
            }
            case 3 -> {
                return no == 0 ? THE_END_0 : (no == 1 ? THE_END_1 : THE_END_2);
            }
            default -> {
                return no == 0 ? TEXTURE_0 : (no == 1 ? TEXTURE_1 : TEXTURE_2);
            }
        }
    }
}
