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
 * 球体渲染器（链式 MinionWeaponItemBuilder）。
 * <p>
 * 以 PoseStack 原点为球心绘制多层同心球壳，通过 <b>alpha 梯度叠加</b>实现体积发光感：
 * 内层小而实（高 alpha，构成锐利核心），外层大而散（低 alpha，构成泛光边缘）。
 * 多层标准半透明叠加即得到“实心核心 + 散开边缘”的视觉，思路与 {@link LaserRendererHelper}
 * 一致，仅几何由圆柱改为球面。
 * </p>
 * <p>
 * 使用原版 {@code entity_translucent_emissive} 渲染类型（{@link LyraRenderTypes#getTrail()}），
 * 原版与光影（Iris/Oculus）环境下效果一致。所有视觉效果在 Java 侧预乘进顶点色。
 * </p>
 * <p>
 * 坐标约定：球心位于 PoseStack 原点，半径在 XYZ 三轴均匀展开。无需实体朝向 PoseStack 约定，
 * 调用方负责把 PoseStack 平移到目标世界位置（附件实体上下文中由调度器完成）。
 * </p>
 *
 * <pre>{@code
 * SphereRendererHelper.builder()
 *     .radius(0.3f)              // 基础半径
 *     .layers(4)                 // 同心壳层数
 *     .sides(12)                 // 总面数控制：少则棱角，多则圆润
 *     .color(0xFFAA88FF)
 *     .alpha(0.9f)
 *     .innerRatio(0.4f)          // 最内层相对半径
 *     .render(poseStack, bufferSource);
 * }</pre>
 */
public class SphereRendererHelper {

    // ===================== 参数 =====================
    private float radius = 0.3f;
    private int layers = 4;
    private int sides = 12;    // 面数控制：经线分段数，纬线分段按比例派生。少则棱角分明，多则圆润
    private int colorRGB = 0xFFFFFFFF;
    private float alpha = 0.9f;
    private float innerRatio = 0.4f; // 最内层相对半径(0~1)

    private SphereRendererHelper() {
    }

    /**
     * 创建 MinionWeaponItemBuilder 实例。
     */
    public static SphereRendererHelper builder() {
        return new SphereRendererHelper();
    }

    // -------------------- 链式参数 --------------------

    /**
     * 球体基础半径（最外层半径）。
     */
    public SphereRendererHelper radius(float radius) {
        this.radius = radius;
        return this;
    }

    /**
     * 同心壳层数，越多泛光越强（建议 3~6）。
     */
    public SphereRendererHelper layers(int layers) {
        this.layers = Math.max(1, layers);
        return this;
    }

    /**
     * 形状面数控制。经线分段数 = {@code sides}，纬线分段数按比例派生为 {@code max(2, sides/2)}。
     * 数值小则棱角分明（低多边形棱球），数值大则趋近光滑球面（建议 6~16）。
     */
    public SphereRendererHelper sides(int sides) {
        this.sides = Math.max(3, sides);
        return this;
    }

    /**
     * 颜色 ARGB（如 0xFFAA88FF）。
     */
    public SphereRendererHelper color(int argb) {
        this.colorRGB = argb;
        return this;
    }

    /**
     * 整体基础透明度 0~1。
     */
    public SphereRendererHelper alpha(float alpha) {
        this.alpha = alpha;
        return this;
    }

    /**
     * 最内层相对半径 0~1（内层越小，核心越锐利）。
     */
    public SphereRendererHelper innerRatio(float ratio) {
        this.innerRatio = net.minecraft.util.Mth.clamp(ratio, 0f, 1f);
        return this;
    }

    // -------------------- 渲染 --------------------

    /**
     * 提交渲染。
     *
     * @param poseStack    姿态栈，原点为球心
     * @param bufferSource 缓冲源
     */
    public void render(PoseStack poseStack, MultiBufferSource bufferSource) {
        VertexConsumer consumer = bufferSource.getBuffer(LyraRenderTypes.getTrail());
        Matrix4f pose = poseStack.last().pose();

        // 基础颜色分量：RGB 全层一致，仅 alpha 按层渐变
        int baseR = FastColor.ARGB32.red(colorRGB);
        int baseG = FastColor.ARGB32.green(colorRGB);
        int baseB = FastColor.ARGB32.blue(colorRGB);
        int baseA = net.minecraft.util.Mth.clamp(Math.round(alpha * 255), 0, 255);

        for (int layer = 0; layer < layers; layer++) {
            // 层级比例: 0=最内层, 1=最外层
            float layerRatio = layers == 1 ? 0f : (float) layer / (layers - 1);
            // 该层半径系数: 内层(innerRatio) ~ 外层(1.0)
            float radiusScale = mix(innerRatio, 1.0f, layerRatio);
            // 层级 alpha：内层高（实核心），外层低（散边缘）—— alpha 梯度叠加出体积发光
            float layerAlpha = mix(1.0f, 0.15f, layerRatio);
            float r = radius * radiusScale;

            renderLayer(consumer, pose, r, layerAlpha, baseR, baseG, baseB, baseA, sides);
        }
    }

    /**
     * 渲染单层球壳：经纬网格四边形条带。
     * <p>
     * 经线分段 = {@code sides}，纬线分段 = {@code max(2, sides/2)}。
     * 每层所有顶点共享同一颜色：RGB = 基础色，alpha = 基础 alpha × 层级 alpha。
     * 体积发光感由多层 alpha 梯度叠加产生。
     * </p>
     */
    private void renderLayer(VertexConsumer consumer, Matrix4f pose, float r,
                             float layerAlpha, int baseR, int baseG, int baseB, int baseA, int sides) {
        int a = net.minecraft.util.Mth.clamp(Math.round(baseA * layerAlpha), 0, 255);
        int vertexColor = FastColor.ARGB32.color(a, baseR, baseG, baseB);

        int stacks = Math.max(2, sides / 2);

        // 预算每条纬线的顶点环（共 stacks+1 个环，每环 slices+1 个点，末点与首点重合以闭合）
        // sinPhi/cosPhi 为纬度方向（Y 高度与水平半径），sinTheta/cosTheta 为经度方向
        for (int ring = 0; ring < stacks; ring++) {
            float phi0 = (float) ring / stacks * (float) Math.PI;        // 纬度 0~π（北极→南极）
            float phi1 = (float) (ring + 1) / stacks * (float) Math.PI;
            float y0 = (float) Math.cos(phi0);
            float y1 = (float) Math.cos(phi1);
            float xz0 = (float) Math.sin(phi0);
            float xz1 = (float) Math.sin(phi1);

            for (int seg = 0; seg < sides; seg++) {
                float theta0 = (float) seg / sides * (float) (Math.PI * 2.0);
                float theta1 = (float) (seg + 1) / sides * (float) (Math.PI * 2.0);
                float ct0 = (float) Math.cos(theta0), st0 = (float) Math.sin(theta0);
                float ct1 = (float) Math.cos(theta1), st1 = (float) Math.sin(theta1);

                // 四边形四顶点（两相邻纬线 × 两相邻经线），球心为原点
                float u0 = (float) seg / sides;
                float u1 = (float) (seg + 1) / sides;
                float v0 = (float) ring / stacks;
                float v1 = (float) (ring + 1) / stacks;

                // 法线 = 顶点归一化方向（球面法线），由位置直接给出
                float nx0y0 = xz0 * ct0, ny0y0 = y0, nz0y0 = xz0 * st0;
                float nx1y0 = xz0 * ct1, ny1y0 = y0, nz1y0 = xz0 * st1;
                float nx1y1 = xz1 * ct1, ny1y1 = y1, nz1y1 = xz1 * st1;
                float nx0y1 = xz1 * ct0, ny0y1 = y1, nz0y1 = xz1 * st0;

                emitVertex(consumer, pose, nx0y0 * r, ny0y0 * r, nz0y0 * r, vertexColor, u0, v0, nx0y0, ny0y0, nz0y0);
                emitVertex(consumer, pose, nx1y0 * r, ny1y0 * r, nz1y0 * r, vertexColor, u1, v0, nx1y0, ny1y0, nz1y0);
                emitVertex(consumer, pose, nx1y1 * r, ny1y1 * r, nz1y1 * r, vertexColor, u1, v1, nx1y1, ny1y1, nz1y1);
                emitVertex(consumer, pose, nx0y1 * r, ny0y1 * r, nz0y1 * r, vertexColor, u0, v1, nx0y1, ny0y1, nz0y1);
            }
        }
    }

    private void emitVertex(VertexConsumer consumer, Matrix4f pose, float x, float y, float z, int color, float u, float v, float nx, float ny, float nz) {
        consumer.vertex(pose, x, y, z)
                .color(color)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(nx, ny, nz)
                .endVertex();
    }

    private static float mix(float a, float b, float t) {
        return a + (b - a) * t;
    }
}
