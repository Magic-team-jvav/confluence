package org.confluence.mod.client.renderer.entity.bullet;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 子弹拖尾专用渲染类型。
 *
 * <p>仅供枪弹渲染器使用，避免把枪械 VFX 的混合模式扩散到全局渲染工具类。</p>
 */
final class BulletRenderTypes extends RenderStateShard {
    private static final Map<String, RenderType> TRAILS = new ConcurrentHashMap<>();

    private BulletRenderTypes() {
        super(null, null, null);
    }

    static RenderType trail(ResourceLocation texture, boolean additive) {
        String key = texture + (additive ? ":additive" : ":translucent");
        return TRAILS.computeIfAbsent(key, ignored -> RenderType.create(
                "confluence_bullet_trail_" + Integer.toHexString(key.hashCode()),
                DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
                VertexFormat.Mode.QUADS,
                1536,
                false,
                true,
                RenderType.CompositeState.builder()
                        .setShaderState(RENDERTYPE_TEXT_SHADER)
                        .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                        .setTransparencyState(additive ? LIGHTNING_TRANSPARENCY : TRANSLUCENT_TRANSPARENCY)
                        .setDepthTestState(LEQUAL_DEPTH_TEST)
                        .setCullState(NO_CULL)
                        .setLightmapState(LIGHTMAP)
                        .setWriteMaskState(COLOR_WRITE)
                        .createCompositeState(false)
        ));
    }
}
