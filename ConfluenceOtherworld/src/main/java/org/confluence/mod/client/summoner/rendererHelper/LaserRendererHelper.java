package org.confluence.mod.client.summoner.rendererHelper;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.confluence.mod.client.summoner.LyraRenderTypes;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.FastColor;
import org.joml.Matrix4f;

/**
 * 激光/光柱渲染器（链式 MinionWeaponItemBuilder）。
 * <p>
 * 在两点之间绘制多层同心圆柱壳，通过 <b>alpha 梯度叠加</b>实现体积雾感：
 * 内层窄而实（高 alpha，构成锐利核心），外层宽而散（低 alpha，构成泛光边缘）。
 * 多层标准半透明叠加即得到“实心核心 + 散开边缘”的视觉，类似旧版“内核+外晕”思路的泛化。
 * </p>
 * <p>
 * 使用原版 {@code entity_translucent_emissive} 渲染类型（{@link LyraRenderTypes#getTrail()}），
 * 该类型在原版与光影（Iris/Oculus）环境下效果完全一致。
 * 所有视觉效果在 Java 侧预乘进顶点色，着色器只读取顶点色与位置变换。
 * </p>
 * <p>
 * 坐标约定：激光沿 -Z 方向铺设（z=0 为近端起点，z=-length 为远端终点），
 * 与现有渲染器的 {@code rotationOffset(180,0,0)} 翻转约定一致。半径在 XY 平面内展开。
 * </p>
 *
 * <pre>{@code
 * LaserRendererHelper.builder()
 *     .length(5.0f)
 *     .radius(0.15f, 0.05f)   // 近端半径, 远端半径
 *     .layers(4)
 *     .segments(12)
 *     .color(0xFF5599FF)
 *     .alpha(0.8f)
 *     .render(poseStack, bufferSource);
 * }</pre>
 */
public class LaserRendererHelper {

    // ===================== 参数 =====================
    private float length = 1.0f;
    private float radiusStart = 0.1f;   // 近端(z=0)半径
    private float radiusEnd = 0.1f;     // 远端(z=-length)半径
    private int layers = 4;
    private int segments = 12;
    private int colorRGB = 0xFFFFFFFF;
    private float alpha = 0.8f;
    private float innerRatio = 0.3f;    // 最内层相对半径(0~1)

    private LaserRendererHelper() {
    }

    /**
     * 创建 MinionWeaponItemBuilder 实例。
     */
    public static LaserRendererHelper builder() {
        return new LaserRendererHelper();
    }

    // -------------------- 链式参数 --------------------

    /**
     * 激光长度（沿 -Z 方向）。
     */
    public LaserRendererHelper length(float length) {
        this.length = length;
        return this;
    }

    /**
     * 两端半径：start 为近端(z=0)，end 为远端(z=-length)。
     */
    public LaserRendererHelper radius(float start, float end) {
        this.radiusStart = start;
        this.radiusEnd = end;
        return this;
    }

    /**
     * 同心壳层数，越多体积雾感越强（建议 3~8）。
     */
    public LaserRendererHelper layers(int layers) {
        this.layers = Math.max(1, layers);
        return this;
    }

    /**
     * 圆周分段数，越多越圆滑（建议 8~16）。
     */
    public LaserRendererHelper segments(int segments) {
        this.segments = Math.max(3, segments);
        return this;
    }

    /**
     * 颜色 ARGB（如 0xFF5599FF）。
     */
    public LaserRendererHelper color(int argb) {
        this.colorRGB = argb;
        return this;
    }

    /**
     * 整体基础透明度 0~1。
     */
    public LaserRendererHelper alpha(float alpha) {
        this.alpha = alpha;
        return this;
    }

    /**
     * 最内层相对半径 0~1（内层越细，核心越锐利）。
     */
    public LaserRendererHelper innerRatio(float ratio) {
        this.innerRatio = Math.max(0f, Math.min(1f, ratio));
        return this;
    }

    // -------------------- 渲染 --------------------

    /**
     * 提交渲染。
     *
     * @param poseStack    姿态栈
     * @param bufferSource 缓冲源
     */
    public void render(PoseStack poseStack, MultiBufferSource bufferSource) {
        VertexConsumer consumer = bufferSource.getBuffer(LyraRenderTypes.getTrail());
        Matrix4f pose = poseStack.last()
                .pose();

        // 基础颜色分量：RGB 全层一致（保证圆柱连续，不压黑接缝），仅 alpha 按层渐变
        int baseR = FastColor.ARGB32.red(colorRGB);
        int baseG = FastColor.ARGB32.green(colorRGB);
        int baseB = FastColor.ARGB32.blue(colorRGB);
        int baseA = net.minecraft.util.Mth.clamp(Math.round(alpha * 255), 0, 255);

        for (int layer = 0; layer < layers; layer++) {
            // 层级比例: 0=最内层, 1=最外层
            float layerRatio = layers == 1 ? 0f : (float) layer / (layers - 1);
            // 该层半径系数: 内层(innerRatio) ~ 外层(1.0)
            float radiusScale = mix(innerRatio, 1.0f, layerRatio);
            // 层级 alpha：内层高（实核心），外层低（散边缘）—— alpha 梯度叠加出体积雾
            // 内层 1.0，外层 0.15，多层标准半透明叠加即得到“实心核心 + 散开泛光边缘”
            float layerAlpha = mix(1.0f, 0.15f, layerRatio);

            renderLayer(consumer, pose, radiusScale, layerAlpha, baseR, baseG, baseB, baseA);
        }
    }

    /**
     * 渲染单层圆柱壳。
     * <p>
     * 每层所有顶点共享同一颜色：RGB = 基础色（全层一致，圆柱连续无接缝断裂），
     * alpha = 基础 alpha × 层级 alpha（内层实、外层散）。体积雾感由多层 alpha 梯度叠加产生，
     * 不再用 per-segment 径向衰减（那会压黑接缝导致面片断裂）。
     * </p>
     */
    private void renderLayer(VertexConsumer consumer, Matrix4f pose, float radiusScale, float layerAlpha, int baseR, int baseG, int baseB, int baseA) {
        // 近端(z=0)半径, 远端(z=-length)半径
        float rNear = radiusStart * radiusScale;
        float rFar = radiusEnd * radiusScale;

        // 该层统一顶点色：RGB 不衰减，alpha 按层渐变
        int a = Math.max(0, Math.min(255, Math.round(baseA * layerAlpha)));
        int vertexColor = FastColor.ARGB32.color(a, baseR, baseG, baseB);

        for (int j = 0; j < segments; j++) {
            float angle1 = (float) (j) / segments * (float) (Math.PI * 2.0);
            float angle2 = (float) (j + 1) / segments * (float) (Math.PI * 2.0);
            float cos1 = (float) Math.cos(angle1), sin1 = (float) Math.sin(angle1);
            float cos2 = (float) Math.cos(angle2), sin2 = (float) Math.sin(angle2);

            // 周向 u 仅作纹理坐标占位（薄着色器不依赖）
            float u = ((float) j + 0.5f) / segments;

            // 四个顶点构成一个纵向四边形(沿轴向): 近角1, 近角2, 远角2, 远角1
            emitVertex(consumer, pose, cos1 * rNear, sin1 * rNear, 0, vertexColor, u, 0f);
            emitVertex(consumer, pose, cos2 * rNear, sin2 * rNear, 0, vertexColor, u, 0f);
            emitVertex(consumer, pose, cos2 * rFar, sin2 * rFar, -length, vertexColor, u, 1f);
            emitVertex(consumer, pose, cos1 * rFar, sin1 * rFar, -length, vertexColor, u, 1f);
        }
    }

    /**
     * 提交单个顶点（{@code NEW_ENTITY} 格式）。
     * <p>
     * 调用顺序对齐 {@code TrailConfig.emitQuad}：setNormal 在最后以触发顶点提交。
     * UV0 保留 (周向u, 轴向v) 供纹理坐标使用（emissive 着色器会读取，无纹理时仅占位）。
     * </p>
     */
    private void emitVertex(VertexConsumer consumer, Matrix4f pose, float x, float y, float z, int color, float u, float v) {
        consumer.vertex(pose, x, y, z)
                .color(color)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(0, 0, 1)
                .endVertex();
    }

    private static float mix(float a, float b, float t) {
        return a + (b - a) * t;
    }
}
