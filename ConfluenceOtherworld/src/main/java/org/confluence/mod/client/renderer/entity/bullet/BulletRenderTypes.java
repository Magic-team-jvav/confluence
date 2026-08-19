package org.confluence.mod.client.renderer.entity.bullet;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class BulletRenderTypes extends RenderStateShard {
    private static final Map<String, RenderType> TRAILS = new ConcurrentHashMap<>();
    private static final RenderType COLORED_TRANSLUCENT = RenderType.create(
            "confluence_gun_colored_translucent", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS,
            1536, false, true, RenderType.CompositeState.builder()
                    .setShaderState(POSITION_COLOR_SHADER)
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setDepthTestState(LEQUAL_DEPTH_TEST)
                    .setCullState(NO_CULL)
                    .setWriteMaskState(COLOR_WRITE)
                    .setOutputState(TRANSLUCENT_TARGET)
                    .createCompositeState(false));

    private BulletRenderTypes() {
        super(null, null, null);
    }

    public static RenderType trail(ResourceLocation texture, boolean additive) {
        String key = texture + (additive ? ":additive" : ":translucent");
        return TRAILS.computeIfAbsent(key, ignored -> RenderType.create(
                "confluence_bullet_trail_" + Integer.toHexString(key.hashCode()),
                DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
                VertexFormat.Mode.QUADS,
                1536,
                false,
                true,
                RenderType.CompositeState.builder()
                        .setShaderState(POSITION_COLOR_TEX_LIGHTMAP_SHADER)
                        .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                        .setTransparencyState(additive ? ADDITIVE_TRANSPARENCY : TRANSLUCENT_TRANSPARENCY)
                        .setDepthTestState(LEQUAL_DEPTH_TEST)
                        .setCullState(NO_CULL)
                        .setLightmapState(LIGHTMAP)
                        .setWriteMaskState(COLOR_WRITE)
                        .setOutputState(TRANSLUCENT_TARGET)
                        .createCompositeState(false)
        ));
    }

    public static RenderType confetti() {
        return COLORED_TRANSLUCENT;
    }

    public static RenderType coloredTrail() {
        return COLORED_TRANSLUCENT;
    }
}
