package org.confluence.mod.client.effect;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;

import java.util.function.BiFunction;
import java.util.function.Function;

public class RenderStateShardAccessor extends RenderStateShard {
    public static final ColoredGlintContext GLINT_FF0000 = ColoredGlintContext.create("FF0000", 0xFF0000);
    public static final ColoredGlintContext GLINT_RAINBOW = ColoredGlintContext.create("rainbow", 0, 0, 0);
    public static final RenderType LASER = RenderType.create("confluence_laser", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256, false, false,
            RenderType.CompositeState.builder().setShaderState(new ShaderStateShard(GameRenderer::getPositionColorShader))
                    .setCullState(CULL).setLightmapState(NO_LIGHTMAP).setWriteMaskState(COLOR_DEPTH_WRITE).createCompositeState(false));
    public static final RenderType HILL_OF_FLESH_BOUNDARY = RenderType.entityTranslucentEmissive(Confluence.asResource("textures/gui/noise.png"));
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
                    .createCompositeState(false));
    public static final RenderType ENTITY_TRANSLUCENT_EMISSIVE = RenderType.create("entity_translucent_emissive", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, false,
            RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
                    .setTextureState(new TextureStateShard(Confluence.asResource("textures/mask/sword.png"), true, false))
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setWriteMaskState(COLOR_WRITE)
                    .setCullState(NO_CULL)
                    .setOverlayState(OVERLAY)
                    .createCompositeState(false));
    public static final BiFunction<ResourceLocation, TransparencyStateShard, RenderType> EYES = Util.memoize(
            (texture, transparencyStateShard) -> {
                RenderStateShard.TextureStateShard textureStateShard = new RenderStateShard.TextureStateShard(texture, false, false);
                return RenderType.create("confluence_eyes", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true,
                        RenderType.CompositeState.builder()
                                .setShaderState(RENDERTYPE_EYES_SHADER)
                                .setTextureState(textureStateShard)
                                .setTransparencyState(transparencyStateShard)
                                .setWriteMaskState(COLOR_WRITE)
                                .createCompositeState(false));
            });

    // 使用原版实体自发光着色器读取叠色，保留 eyes 的加色混合、剔除和只写颜色行为。
    private static final Function<ResourceLocation, RenderType> ENTITY_GLOW = Util.memoize(texture ->
            RenderType.create("confluence_entity_glow", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true,
                    RenderType.CompositeState.builder()
                            .setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
                            .setTextureState(new TextureStateShard(texture, false, false))
                            .setTransparencyState(ADDITIVE_TRANSPARENCY)
                            .setCullState(CULL).setWriteMaskState(COLOR_WRITE)
                            .setOverlayState(OVERLAY)
                            .createCompositeState(false)));

    public static RenderType entityGlow(ResourceLocation texture) {
        return ENTITY_GLOW.apply(texture);
    }

    // 1.20.1 的 entityTranslucentCull 着色器不读取叠色；使用普通实体着色器并保留背面剔除。
    private static final Function<ResourceLocation, RenderType> ENTITY_TRANSLUCENT_CULL_OVERLAY = Util.memoize(texture ->
            RenderType.create("confluence_entity_translucent_cull_overlay", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true,
                    RenderType.CompositeState.builder()
                            .setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
                            .setTextureState(new TextureStateShard(texture, false, false))
                            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                            .setCullState(CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY)
                            .createCompositeState(true)));

    public static RenderType entityTranslucentCullOverlay(ResourceLocation texture) {
        return ENTITY_TRANSLUCENT_CULL_OVERLAY.apply(texture);
    }

    private RenderStateShardAccessor() {
        super(null, null, null);
    }

    public static ColoredGlintContext create(String name, float red, float green, float blue) {
        float[] glintColor = {red, green, blue};
        ColoredGlintContext context = new ColoredGlintContext(RenderType.create(
                "colored_glint_" + name,
                DefaultVertexFormat.POSITION_TEX,
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
                                () -> RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F)))
                        .createCompositeState(false)), glintColor);
        ColoredGlintContext.COLORED_GLINT_CONTEXTS.add(context);
        return context;
    }
}
