package org.confluence.mod.client.summoner.rendererHelper;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.confluence.mod.client.summoner.LyraRenderTypes;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * 雷电/闪电链渲染器（链式 MinionWeaponItemBuilder）。
 * <p>
 * 在两个绝对世界坐标点之间绘制带扭曲效果的闪电链。整体形态借鉴 {@link LaserRendererHelper} 的
 * “多层 alpha 梯度叠加”思路：内层窄而亮（锐利核心），外层宽而散（泛光边缘），多层标准半透明
 * 叠加即得到“实心核心 + 散开边缘”的视觉。
 * </p>
 * <p>
 * 与 {@link LaserRendererHelper} 的差异：
 * <ul>
 *   <li><b>坐标</b>：接收两个绝对世界坐标 {@code start}/{@code end}，以及 PoseStack 原点对应的
 *       世界坐标 {@code renderOrigin}。内部将世界点换算为相对原点的局部点后发射顶点，
 *       不依赖实体朝向 PoseStack 约定。</li>
 *   <li><b>几何</b>：沿 start→end 方向铺设，长度 = 两点距离，半径在垂直于方向的平面内展开。</li>
 *   <li><b>扭曲</b>：沿轴向按 {@code segments} 分段，每段在垂直方向上叠加由 {@link RandomSource}
 *       决定的位移，形成锯齿/抖动。同一 {@link RandomSource} 实例（同种子）产生确定性扭曲，便于复现。</li>
 *   <li><b>分叉</b>：可选沿主链随机生成分支（{@link #branches}），由同一 RandomSource 决定位置/方向/长度，
 *       分支起点复用主链已扭曲采样点，确保从主链实际位置发出。</li>
 * </ul>
 * </p>
 * <p>
 * 使用原版 {@code entity_translucent_emissive} 渲染类型（{@link LyraRenderTypes#getTrail()}），
 * 原版与光影（Iris/Oculus）环境下效果一致。所有视觉效果在 Java 侧预乘进顶点色。
 * </p>
 *
 * <pre>{@code
 * LightningRendererHelper.builder()
 *     .from(startWorld)          // 起点世界坐标
 *     .to(endWorld)              // 终点世界坐标
 *     .renderOrigin(originWorld) // PoseStack 原点对应的世界坐标（附件实体上下文传 visualNode.pos()）
 *     .layers(4)
 *     .segments(16)
 *     .branches(3)
 *     .jitter(0.3f)
 *     .branchLength(0.4f)
 *     .radius(0.12f, 0.02f)      // 核心半径, 泛光外层半径
 *     .color(0xFFFFFFFF)
 *     .alpha(0.9f)
 *     .render(poseStack, bufferSource, randomSource);
 * }</pre>
 */
public class LightningRendererHelper {

    // ===================== 参数 =====================
    private Vec3 start = Vec3.ZERO;
    private Vec3 end = Vec3.ZERO;
    private Vec3 renderOrigin = Vec3.ZERO;
    private int layers = 4;
    private int segments = 16;
    private int branches = 0;
    private float jitter = 0.3f;        // 垂直扭曲幅度（相对长度的比例）
    private float branchLength = 0.4f;  // 分支长度（相对主链长度的比例）
    private float radiusCore = 0.05f;   // 最内层半径
    private float radiusOuter = 0.20f;  // 最外层半径
    private int colorRGB = 0xFFFFFFFF;
    private float alpha = 0.9f;
    private float innerRatio = 0.25f;   // 最内层相对半径(0~1)

    private LightningRendererHelper() {
    }

    /**
     * 创建 MinionWeaponItemBuilder 实例。
     */
    public static LightningRendererHelper builder() {
        return new LightningRendererHelper();
    }

    // -------------------- 链式参数 --------------------

    /**
     * 闪电起点（绝对世界坐标）。
     */
    public LightningRendererHelper from(Vec3 start) {
        this.start = start;
        return this;
    }

    /**
     * 闪电终点（绝对世界坐标）。
     */
    public LightningRendererHelper to(Vec3 end) {
        this.end = end;
        return this;
    }

    /**
     * PoseStack 原点对应的绝对世界坐标。
     * <p>
     * 内部将 {@code start}/{@code end} 减去此值得到局部坐标。
     * 在附件实体渲染上下文中传入 {@code visualNode.pos()}；其他场景按调用方 PoseStack 平移传入。
     * </p>
     */
    public LightningRendererHelper renderOrigin(Vec3 renderOrigin) {
        this.renderOrigin = renderOrigin;
        return this;
    }

    /**
     * 同心壳层数，越多泛光越强（建议 3~8）。
     */
    public LightningRendererHelper layers(int layers) {
        this.layers = Math.max(1, layers);
        return this;
    }

    /**
     * 沿轴向分段数，越多锯齿越细密（建议 8~24）。每段在垂直方向叠加随机位移形成扭曲。
     */
    public LightningRendererHelper segments(int segments) {
        this.segments = Math.max(2, segments);
        return this;
    }

    /**
     * 分支数量（0=无分叉）。分支位置/方向/长度由 {@link RandomSource} 决定。
     */
    public LightningRendererHelper branches(int branches) {
        this.branches = Math.max(0, branches);
        return this;
    }

    /**
     * 主链垂直扭曲幅度（相对主链长度的比例，0=直线）。
     */
    public LightningRendererHelper jitter(float jitter) {
        this.jitter = jitter;
        return this;
    }

    /**
     * 分支长度（相对主链长度的比例）。
     */
    public LightningRendererHelper branchLength(float branchLength) {
        this.branchLength = branchLength;
        return this;
    }

    /**
     * 两端半径：core 为最内层（核心），outer 为最外层（泛光边缘）。
     */
    public LightningRendererHelper radius(float core, float outer) {
        this.radiusCore = core;
        this.radiusOuter = outer;
        return this;
    }

    /**
     * 颜色 ARGB（如 0xFFFFFFFF）。
     */
    public LightningRendererHelper color(int argb) {
        this.colorRGB = argb;
        return this;
    }

    /**
     * 整体基础透明度 0~1。
     */
    public LightningRendererHelper alpha(float alpha) {
        this.alpha = alpha;
        return this;
    }

    /**
     * 最内层相对半径 0~1（内层越细，核心越锐利）。
     */
    public LightningRendererHelper innerRatio(float ratio) {
        this.innerRatio = Math.max(0f, Math.min(1f, ratio));
        return this;
    }

    // -------------------- 渲染 --------------------

    /**
     * 提交渲染。
     *
     * @param poseStack    姿态栈
     * @param bufferSource 缓冲源
     * @param random       决定扭曲样式（分支位置/方向、各段垂直位移）。同种子实例产生确定性结果。
     */
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, RandomSource random) {
        VertexConsumer consumer = bufferSource.getBuffer(LyraRenderTypes.getTrail());
        Matrix4f pose = poseStack.last().pose();

        // 世界坐标 -> 相对 renderOrigin 的局部坐标
        Vector3f sLocal = worldToLocal(start, renderOrigin);
        Vector3f eLocal = worldToLocal(end, renderOrigin);

        // 主链方向与长度
        Vector3f dir = new Vector3f(eLocal).sub(sLocal);
        float length = dir.length();
        if (length < 1.0E-4f) {
            return; // 两点重合，不绘制
        }
        dir.div(length); // 单位方向

        int baseR = FastColor.ARGB32.red(colorRGB);
        int baseG = FastColor.ARGB32.green(colorRGB);
        int baseB = FastColor.ARGB32.blue(colorRGB);
        int baseA = Math.max(0, Math.min(255, Math.round(alpha * 255)));

        // 1) 采样主链扭曲点（消费一段随机序列）
        Vector3f perpA = perpendicular(dir, random); // 主链垂直基 A（消费 random）
        Vector3f perpB = new Vector3f();
        dir.cross(perpA, perpB);
        if (perpB.lengthSquared() < 1.0E-8f) {
            perpB.set(0, 1, 0);
        } else {
            perpB.normalize();
        }
        Vector3f[] points = sampleBoltPoints(sLocal, dir, length, perpA, perpB, random);

        // 2) 渲染主链多层壳
        for (int layer = 0; layer < layers; layer++) {
            float layerRatio = layers == 1 ? 0f : (float) layer / (layers - 1);
            float radius = mix(radiusCore, radiusOuter, mix(innerRatio, 1.0f, layerRatio));
            float layerAlpha = mix(1.0f, 0.15f, layerRatio);
            int a = Math.max(0, Math.min(255, Math.round(baseA * layerAlpha)));
            int vertexColor = FastColor.ARGB32.color(a, baseR, baseG, baseB);
            renderLayer(consumer, pose, points, perpA, perpB, radius, vertexColor);
        }

        // 3) 渲染分支（消费后续随机序列），起点复用主链采样点
        for (int b = 0; b < branches; b++) {
            int idx = 1 + random.nextInt(points.length - 2); // 避开首尾端点
            Vector3f branchStart = points[idx];
            // 分支方向：以主链段方向为基底，叠加随机垂直偏转
            Vector3f segDir = new Vector3f(points[idx + 1]).sub(points[idx - 1]);
            if (segDir.lengthSquared() < 1.0E-8f) {
                segDir.set(dir);
            } else {
                segDir.normalize();
            }
            Vector3f bpa = perpendicular(segDir, random);
            Vector3f bpb = new Vector3f();
            segDir.cross(bpa, bpb);
            if (bpb.lengthSquared() < 1.0E-8f) {
                bpb.set(0, 1, 0);
            } else {
                bpb.normalize();
            }
            float along = (random.nextFloat() * 2f - 1f) * 0.3f; // 沿主链方向漂移
            float orthoA = random.nextFloat() * 2f - 1f;
            float orthoB = random.nextFloat() * 2f - 1f;
            Vector3f branchDir = new Vector3f(segDir).mul(along)
                    .add(new Vector3f(bpa).mul(orthoA))
                    .add(new Vector3f(bpb).mul(orthoB));
            if (branchDir.lengthSquared() < 1.0E-6f) {
                continue;
            }
            branchDir.normalize();
            float branchLen = length * branchLength;

            // 分支自身扭曲采样（沿 branchDir，用 bpa/bpb 作垂直基）
            Vector3f[] bPoints = sampleBoltPoints(branchStart, branchDir, branchLen, bpa, bpb, random);
            for (int layer = 0; layer < layers; layer++) {
                float layerRatio = layers == 1 ? 0f : (float) layer / (layers - 1);
                float radius = mix(radiusCore, radiusOuter, mix(innerRatio, 1.0f, layerRatio)) * 0.6f; // 分支更细
                float layerAlpha = mix(1.0f, 0.15f, layerRatio);
                int a = Math.max(0, Math.min(255, Math.round(baseA * 0.7f * layerAlpha)));
                int vertexColor = FastColor.ARGB32.color(a, baseR, baseG, baseB);
                renderLayer(consumer, pose, bPoints, bpa, bpb, radius, vertexColor);
            }
        }
    }

    /**
     * 沿 {@code dir} 方向按 segments 分段采样扭曲点列。端点不扭曲（保证起止精确），
     * 中段按 (1 - |2t-1|) 包络缩放，避免端点处扭曲跳变。每点消费 2 个 random（A/B 两向位移）。
     */
    private Vector3f[] sampleBoltPoints(Vector3f origin, Vector3f dir, float length,
                                        Vector3f perpA, Vector3f perpB, RandomSource random) {
        Vector3f[] points = new Vector3f[segments + 1];
        float jitterAmt = length * jitter;
        for (int i = 0; i <= segments; i++) {
            float t = (float) i / segments;
            float axial = t * length;
            float envelope = (1f - Math.abs(2f * t - 1f));
            float offA = (random.nextFloat() * 2f - 1f) * jitterAmt * envelope;
            float offB = (random.nextFloat() * 2f - 1f) * jitterAmt * envelope;
            points[i] = new Vector3f(origin)
                    .add(new Vector3f(dir).mul(axial))
                    .add(new Vector3f(perpA).mul(offA))
                    .add(new Vector3f(perpB).mul(offB));
        }
        return points;
    }

    /**
     * 渲染单层扭曲圆柱壳：连接相邻采样点为四边形条带，周向按 {@code radial} 个纵向条带。
     */
    private void renderLayer(VertexConsumer consumer, Matrix4f pose, Vector3f[] points,
                             Vector3f perpA, Vector3f perpB,
                             float radius, int vertexColor) {
        int radial = 6; // 周向分段（闪电为细线，少量即可）
        float[][] ring = new float[radial][2];
        for (int k = 0; k < radial; k++) {
            float ang = (float) k / radial * (float) (Math.PI * 2.0);
            ring[k][0] = (float) Math.cos(ang);
            ring[k][1] = (float) Math.sin(ang);
        }

        for (int i = 0; i < segments; i++) {
            Vector3f p0 = points[i];
            Vector3f p1 = points[i + 1];
            Vector3f segDir = new Vector3f(p1).sub(p0);
            float segLen = segDir.length();
            if (segLen < 1.0E-6f) {
                continue;
            }
            segDir.div(segLen);

            for (int k = 0; k < radial; k++) {
                int k2 = (k + 1) % radial;
                float ca1 = ring[k][0], sa1 = ring[k][1];
                float ca2 = ring[k2][0], sa2 = ring[k2][1];

                float u = ((float) k + 0.5f) / radial;
                float v0 = (float) i / segments;
                float v1 = (float) (i + 1) / segments;

                Vector3f v00 = ringOffset(p0, perpA, perpB, ca1, sa1, radius);
                Vector3f v01 = ringOffset(p0, perpA, perpB, ca2, sa2, radius);
                Vector3f v11 = ringOffset(p1, perpA, perpB, ca2, sa2, radius);
                Vector3f v10 = ringOffset(p1, perpA, perpB, ca1, sa1, radius);

                emitVertex(consumer, pose, v00, vertexColor, u, v0, segDir);
                emitVertex(consumer, pose, v01, vertexColor, u, v0, segDir);
                emitVertex(consumer, pose, v11, vertexColor, u, v1, segDir);
                emitVertex(consumer, pose, v10, vertexColor, u, v1, segDir);
            }
        }
    }

    // -------------------- 工具 --------------------

    /**
     * 求一个与 {@code dir} 正交的单位向量（任意一个），用 {@code random} 在 ± 间挑选以保证确定性。
     */
    private Vector3f perpendicular(Vector3f dir, RandomSource random) {
        Vector3f ref = Math.abs(dir.x) < 0.9f ? new Vector3f(1, 0, 0) : new Vector3f(0, 1, 0);
        Vector3f perp = new Vector3f();
        dir.cross(ref, perp);
        if (perp.lengthSquared() < 1.0E-8f) {
            perp.set(0, 0, 1);
        } else {
            perp.normalize();
        }
        if (random.nextBoolean()) {
            perp.negate();
        }
        return perp;
    }

    private Vector3f ringOffset(Vector3f center, Vector3f perpA, Vector3f perpB, float ca, float sa, float radius) {
        return new Vector3f(center)
                .add(new Vector3f(perpA).mul(ca * radius))
                .add(new Vector3f(perpB).mul(sa * radius));
    }

    private void emitVertex(VertexConsumer consumer, Matrix4f pose, Vector3f v, int color, float u, float vCoord, Vector3f normal) {
        consumer.vertex(pose, v.x, v.y, v.z)
                .color(color)
                .uv(u, vCoord)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(LightTexture.FULL_BRIGHT)
                .normal(normal.x, normal.y, normal.z)
                .endVertex();
    }

    private static Vector3f worldToLocal(Vec3 world, Vec3 origin) {
        return new Vector3f((float) (world.x - origin.x), (float) (world.y - origin.y), (float) (world.z - origin.z));
    }

    private static float mix(float a, float b, float t) {
        return a + (b - a) * t;
    }
}
