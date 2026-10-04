package org.confluence.mod.client.summoner;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import org.confluence.mod.Confluence;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class LyraRenderTypes extends RenderType {

    public LyraRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
    }

    private static final Function<ResourceLocation, RenderType> NO_DEPTH_TEXTURE = Util.memoize(texture -> {
        CompositeState state = CompositeState.builder()
                .setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
                .setTextureState(new TextureStateShard(texture, false, false))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setCullState(NO_CULL)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .setDepthTestState(NO_DEPTH_TEST)
                .setWriteMaskState(COLOR_WRITE)
                .createCompositeState(true);
        return create("lyra_texture_no_depth", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, true, state);
    });

    /**
     * 拖尾渲染类型。
     * <p>
     * 直接复用原版 {@link RenderType#entityTranslucentEmissive(ResourceLocation)}，与 Lyra 的
     * {@code LyraRenderTypes.TRAIL} 做法完全一致：该类型自带
     * {@code shader + texture + TRANSLUCENT + NO_CULL + COLOR_WRITE + OVERLAY}、不取光照图、
     * {@code setupRenderState} 里显式关闭剔除，是经过验证可用的一条路径。
     * </p>
     * <p>
     * 之前这里是手搓的 {@code create(...)} 等价实现，各项参数虽逐项对齐，但属于自己拼的
     * 组合状态；拖尾长期不显示又查不出原因，故改回原版方法，消除这一处不确定性。
     * </p>
     */
    public static final RenderType TRAIL = RenderType.entityTranslucentEmissive(Confluence.asResource("textures/trail.png"));

    public static final RenderType MODEL = RenderType.entityTranslucentCull(TextureAtlas.LOCATION_BLOCKS);

    public static RenderType getTrail() {
        return RenderType.entityTranslucent(Confluence.asResource("textures/trail.png"));
    }

    public static RenderType getModel() {
        return MODEL;
    }

    public static RenderType texture(ResourceLocation texture, boolean alwaysVisible) {
        return alwaysVisible ? NO_DEPTH_TEXTURE.apply(texture) : RenderType.entityTranslucent(texture);
    }

    private static final Function<ResourceLocation, RenderType> TEXT_TEXTURE = Util.memoize(texture -> create(
            "lyra_texture_text",
            DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
            VertexFormat.Mode.QUADS,
            1536,
            false,
            true,
            CompositeState.builder()
                    .setShaderState(RENDERTYPE_TEXT_SHADER)
                    .setTextureState(new TextureStateShard(texture, false, false))
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setCullState(NO_CULL)
                    .setLightmapState(LIGHTMAP)
                    .createCompositeState(false)
    ));

    public static RenderType textTexture(ResourceLocation texture) {
        return TEXT_TEXTURE.apply(texture);
    }
}
