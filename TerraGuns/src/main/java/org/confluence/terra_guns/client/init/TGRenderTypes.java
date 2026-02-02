package org.confluence.terra_guns.client.init;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;

import static net.minecraft.client.renderer.RenderStateShard.*;

public class TGRenderTypes {
    public static RenderType TRAIL_RENDER_TYPE = RenderType.create(
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
}
