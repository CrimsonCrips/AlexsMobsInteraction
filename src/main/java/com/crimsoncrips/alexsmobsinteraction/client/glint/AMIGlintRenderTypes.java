package com.crimsoncrips.alexsmobsinteraction.client.glint;

import com.crimsoncrips.alexsmobsinteraction.AlexsMobsInteraction;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public abstract class AMIGlintRenderTypes extends RenderType {

    private static final ResourceLocation MIMIC_GLINT_ITEM = AlexsMobsInteraction.prefix("textures/misc/mimic_glint_item.png");
    private static final ResourceLocation MIMIC_GLINT_ENTITY = AlexsMobsInteraction.prefix("textures/misc/mimic_glint_entity.png");

    public static final RenderType MIMIC_GLINT = create("alexsmobsinteraction_mimic_glint", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 1536,
            CompositeState.builder()
                    .setShaderState(RENDERTYPE_GLINT_SHADER)
                    .setTextureState(new TextureStateShard(MIMIC_GLINT_ITEM, true, false))
                    .setWriteMaskState(COLOR_WRITE)
                    .setCullState(NO_CULL)
                    .setDepthTestState(EQUAL_DEPTH_TEST)
                    .setTransparencyState(GLINT_TRANSPARENCY)
                    .setTexturingState(GLINT_TEXTURING)
                    .createCompositeState(false));

    public static final RenderType MIMIC_GLINT_TRANSLUCENT = create("alexsmobsinteraction_mimic_glint_translucent", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 1536,
            CompositeState.builder()
                    .setShaderState(RENDERTYPE_GLINT_TRANSLUCENT_SHADER)
                    .setTextureState(new TextureStateShard(MIMIC_GLINT_ITEM, true, false))
                    .setWriteMaskState(COLOR_WRITE)
                    .setCullState(NO_CULL)
                    .setDepthTestState(EQUAL_DEPTH_TEST)
                    .setTransparencyState(GLINT_TRANSPARENCY)
                    .setTexturingState(GLINT_TEXTURING)
                    .setOutputState(ITEM_ENTITY_TARGET)
                    .createCompositeState(false));

    public static final RenderType MIMIC_ENTITY_GLINT = create("alexsmobsinteraction_mimic_entity_glint", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 1536,
            CompositeState.builder()
                    .setShaderState(RENDERTYPE_ENTITY_GLINT_SHADER)
                    .setTextureState(new TextureStateShard(MIMIC_GLINT_ENTITY, true, false))
                    .setWriteMaskState(COLOR_WRITE)
                    .setCullState(NO_CULL)
                    .setDepthTestState(EQUAL_DEPTH_TEST)
                    .setTransparencyState(GLINT_TRANSPARENCY)
                    .setOutputState(ITEM_ENTITY_TARGET)
                    .setTexturingState(ENTITY_GLINT_TEXTURING)
                    .createCompositeState(false));

    public static final RenderType MIMIC_ENTITY_GLINT_DIRECT = create("alexsmobsinteraction_mimic_entity_glint_direct", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 1536,
            CompositeState.builder()
                    .setShaderState(RENDERTYPE_ENTITY_GLINT_DIRECT_SHADER)
                    .setTextureState(new TextureStateShard(MIMIC_GLINT_ENTITY, true, false))
                    .setWriteMaskState(COLOR_WRITE)
                    .setCullState(NO_CULL)
                    .setDepthTestState(EQUAL_DEPTH_TEST)
                    .setTransparencyState(GLINT_TRANSPARENCY)
                    .setTexturingState(ENTITY_GLINT_TEXTURING)
                    .createCompositeState(false));

    public static boolean renderingMimicked = false;

    private AMIGlintRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
    }

    public static RenderType swap(RenderType original) {
        if (!renderingMimicked)
            return original;
        if (original == RenderType.glint())
            return MIMIC_GLINT;
        if (original == RenderType.glintTranslucent())
            return MIMIC_GLINT_TRANSLUCENT;
        if (original == RenderType.entityGlint())
            return MIMIC_ENTITY_GLINT;
        if (original == RenderType.entityGlintDirect())
            return MIMIC_ENTITY_GLINT_DIRECT;
        return original;
    }
}
