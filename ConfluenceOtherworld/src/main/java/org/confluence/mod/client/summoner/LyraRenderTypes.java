package org.confluence.mod.client.summoner;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import org.confluence.mod.Confluence;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

/**
 * Lyra 渲染类型工厂（1.21.1，26.2 对齐）。
 * <p>
 * 模型管线：items atlas + entity translucent emissive（NEW_ENTITY 格式）。
 * 标记等 alwaysVisible 贴图使用无深度测试的半透明管线（可透视，不被方块遮挡）。
 * </p>
 */
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
     * 1.20.1 的 {@code RenderType.entityTranslucentEmissive(贴图)} 默认开启 outline 变体
     * （{@code CompositeState#createCompositeState(true)}），该变体会被实体描边通道复用，
     * 导致拖尾在描边/物品实体通道里以不透明方式再画一次。这里显式关闭 outline，
     * 只保留「半透明 + 不写深度 + 无剔除 + 全亮」的发光管线。
     * </p>
     */
    public static final RenderType TRAIL = create(
            "lyra_trail",
            DefaultVertexFormat.NEW_ENTITY,
            VertexFormat.Mode.QUADS,
            1536,
            true,
            true,
            CompositeState.builder()
                    .setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
                    // 每个丝带四边形都完整采样一次贴图，必须用 Lyra 的纵向渐变条，用粒子点状贴图会变成断续斑点
                    .setTextureState(new TextureStateShard(Confluence.asResource("textures/trail.png"), false, false))
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setCullState(NO_CULL)
                    .setOverlayState(OVERLAY)
                    .setWriteMaskState(COLOR_WRITE)
                    .createCompositeState(false)
    );

    /**
     * 模型渲染类型。
     * <p>
     * 1.20.1 的 {@code Sheets.translucentItemSheet()} 实际是 {@code itemEntityTranslucentCull}，
     * 带有 ITEM_ENTITY_TARGET 输出状态：在光影/高性能图形下会被画进物品实体缓冲，
     * 世界中的模型于是出现颜色错乱（紫黑块）。这里改用同源但不带特殊输出的
     * 半透明剔除管线，行为与 1.21.1 的 translucentItemSheet 对齐。
     * </p>
     */
    public static final RenderType MODEL = RenderType.entityTranslucentCull(TextureAtlas.LOCATION_BLOCKS);

    public static RenderType getTrail() {
        return TRAIL;
    }

    public static RenderType getModel() {
        return MODEL;
    }

    /**
     * 通用贴图渲染类型（1.21.1 对应 26.2 LyraRenderTypes.texture）。
     *
     * @param texture       贴图路径
     * @param alwaysVisible true = 无深度测试变体（可透视，用于召唤标记等）
     */
    public static RenderType texture(ResourceLocation texture, boolean alwaysVisible) {
        return alwaysVisible ? NO_DEPTH_TEXTURE.apply(texture) : RenderType.entityTranslucent(texture);
    }
}
