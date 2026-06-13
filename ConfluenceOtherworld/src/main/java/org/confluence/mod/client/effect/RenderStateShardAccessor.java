package org.confluence.mod.client.effect;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import org.confluence.mod.Confluence;

public class RenderStateShardAccessor extends RenderStateShard {
    public static final RenderType TRAIL_RENDER_TYPE = RenderType.create(
            "trail_render_type",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.QUADS,
            1536,
            false,
            true,
            RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_LIGHTNING_SHADER)
                    .setWriteMaskState(COLOR_DEPTH_WRITE)
                    .setTransparencyState(LIGHTNING_TRANSPARENCY)
                    .setLightmapState(NO_LIGHTMAP)
                    .setOutputState(WEATHER_TARGET)
                    .createCompositeState(false)
    );
    public static final RenderType ENTITY_TRANSLUCENT_EMISSIVE = RenderType.create("entity_translucent_emissive", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, false,
            RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
                    .setTextureState(new TextureStateShard(Confluence.asResource("textures/mask/sword.png"), true, false))
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setWriteMaskState(COLOR_WRITE)
                    .setCullState(NO_CULL)
                    .setOverlayState(OVERLAY)
                    .createCompositeState(false));

    private RenderStateShardAccessor() {
        super(null, null, null);
    }

    public static ColoredGlintContext create(String name, float red, float green, float blue) {
        float[] glintColor = {red, green, blue};
        ColoredGlintContext context = new ColoredGlintContext(RenderType.create(
                "colored_glint_" + name,
                DefaultVertexFormat.POSITION_TEX_COLOR,
                VertexFormat.Mode.QUADS,
                1536,
                false,
                false,
                RenderType.CompositeState.builder()
                        .setShaderState(RENDERTYPE_GLINT_SHADER)
                        .setTextureState(ColoredGlintContext.COLORED_GLINT_TEXTURE_STATE_SHARD)
                        .setWriteMaskState(COLOR_WRITE)
                        .setCullState(NO_CULL)
                        .setDepthTestState(EQUAL_DEPTH_TEST)
                        .setTransparencyState(GLINT_TRANSPARENCY)
                        .setTexturingState(GLINT_TEXTURING)
                        .setColorLogicState(new ColorLogicStateShard("set_color",
                                () -> RenderSystem.setShaderColor(glintColor[0], glintColor[1], glintColor[2], 1.0F),
                                () -> RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F)
                        ))
                        .createCompositeState(false)
        ), glintColor);
        ColoredGlintContext.COLORED_GLINT_CONTEXTS.add(context);
        return context;
    }
}
