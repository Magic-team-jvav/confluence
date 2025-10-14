package org.confluence.mod.client.effect;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;
import org.confluence.mod.Confluence;

import java.util.List;

import static net.minecraft.client.renderer.RenderStateShard.*;

public final class ColoredGlintContext {
    public static final List<ColoredGlintContext> COLORED_GLINT_CONTEXTS = Lists.newArrayList();
    public static final TextureStateShard COLORED_GLINT_TEXTURE_STATE_SHARD = new TextureStateShard(Confluence.asResource("textures/misc/white_glint.png"), true, false);
    private final RenderType renderType;
    private final float[] glintColor;

    ColoredGlintContext(RenderType renderType, float[] glintColor) {
        this.renderType = renderType;
        this.glintColor = glintColor;
    }

    public static ColoredGlintContext create(String name, float red, float green, float blue) {
        float[] glintColor = {red, green, blue};
        ColoredGlintContext context = new ColoredGlintContext(RenderType.create("colored_glint_" + name, DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS, 1536, RenderType.CompositeState.builder()
                .setShaderState(RENDERTYPE_GLINT_SHADER)
                .setTextureState(COLORED_GLINT_TEXTURE_STATE_SHARD)
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
        COLORED_GLINT_CONTEXTS.add(context);
        return context;
    }

    public static ColoredGlintContext create(String name, int rgb) {
        return create(name, (rgb >> 16 & 255) / 255.0F, (rgb >> 8 & 255) / 255.0F, (rgb & 255) / 255.0F);
    }

    public void setGlintColor(float red, float green, float blue) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> _setGlintColor(red, green, blue));
        } else {
            _setGlintColor(red, green, blue);
        }
    }

    public void setGlintColor(int red, int green, int blue) {
        float r = red / 255F;
        float g = green / 255F;
        float b = blue / 255F;
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> _setGlintColor(r, g, b));
        } else {
            _setGlintColor(r, g, b);
        }
    }

    private void _setGlintColor(float red, float green, float blue) {
        glintColor[0] = red;
        glintColor[1] = green;
        glintColor[2] = blue;
    }

    public RenderType renderType() {
        return renderType;
    }
}
