package org.confluence.mod.client.summoner.trail;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.confluence.mod.client.summoner.RenderContext;
import org.confluence.mod.client.summoner.RenderUtil;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntity;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FastColor;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 拖尾渲染配置基类。
 * <p>
 * 使用模板方法模式，将渲染逻辑封装在配置类中，子类实现具体渲染。
 * 基类提供共享能力：平滑节点构建、圆周缓存、颜色打包、样板化的渲染上下文与四边形发射。
 * </p>
 * <p>
 * 优化：绕过 PoseStack，直接构造 Matrix4f 并使用 10 参数 addVertex 快速路径，
 * 每四边形从 24 次方法调用降至 4 次。
 * </p>
 *
 * @param <T>    实体类型
 * @param <SELF> 配置类自身类型（用于链式调用）
 */
public abstract class TrailConfig<T extends AttachmentEntity, SELF extends TrailConfig<T, SELF>> {

    // ===================== 基础参数 =====================

    /** 拖尾计时器值，>0 时显示拖尾 */
    public int timer = 0;

    /** 历史节点数量，默认 4 */
    public int historyLength = 4;

    // ===================== 顶点收集（writeVertices 批量提交，26.2 移植） =====================

    /** 收集缓冲：每顶点 5 float（已变换 x,y,z + u,v）。 */
    private float[] trailVertexData = new float[1024 * 5];
    /** 收集缓冲：每顶点 1 int（ARGB）。 */
    private int[] trailColorData = new int[1024];
    /** 已收集顶点数。 */
    private int trailVertexCount;

    /** 每节点插值分段数，默认 8 */
    public int segmentsPerNode = 8;

    /** 拖尾起始索引，默认 0 */
    public int startIndex = 0;

    /** 基础颜色 RGB */
    public int colorRGB = 0xFF0000;

    /** 颜色函数 */
    public RenderContext.ColorFunction<T> colorFunction = (entity, progress, partialTick) -> colorRGB;

    /** 淡出函数 */
    public RenderContext.FadeFunction fadeOut = progress -> (float) Math.pow(Math.max(0.0f, 1.0f - progress), 1.5);

    /** timeShift 时间缩放魔法数（owner.tickCount + partialTick）× 此值 */
    private static final float TIME_SHIFT_SCALE = 0.015f;

    // ===================== 圆周缓存 =====================

    /** 圆形顶点缓存 */
    protected static final Map<Integer, float[]> COS_CACHE = new HashMap<>();
    protected static final Map<Integer, float[]> SIN_CACHE = new HashMap<>();

    protected static float[] getCosArray(int resolution) {
        return COS_CACHE.computeIfAbsent(resolution, r -> {
            float[] arr = new float[r + 1];
            float delta = (float) (2.0 * Math.PI / r);
            for (int i = 0; i <= r; i++) {
                arr[i] = (float) Math.cos(i * delta);
            }
            return arr;
        });
    }

    protected static float[] getSinArray(int resolution) {
        return SIN_CACHE.computeIfAbsent(resolution, r -> {
            float[] arr = new float[r + 1];
            float delta = (float) (2.0 * Math.PI / r);
            for (int i = 0; i <= r; i++) {
                arr[i] = (float) Math.sin(i * delta);
            }
            return arr;
        });
    }

    // ===================== 链式配置方法 =====================

    @SuppressWarnings("unchecked")
    protected SELF self() {
        return (SELF) this;
    }

    public SELF timer(int timer) {
        this.timer = timer;
        return self();
    }

    public SELF historyLength(int length) {
        this.historyLength = length;
        return self();
    }

    public SELF segmentsPerNode(int segments) {
        this.segmentsPerNode = segments;
        return self();
    }

    public SELF startIndex(int index) {
        this.startIndex = index;
        return self();
    }

    public SELF colorRGB(int color) {
        this.colorRGB = color;
        return self();
    }

    public SELF colorFunction(RenderContext.ColorFunction<T> function) {
        this.colorFunction = function;
        return self();
    }

    public SELF fadeOut(RenderContext.FadeFunction function) {
        this.fadeOut = function;
        return self();
    }

    // ===================== 渲染入口 =====================

    /**
     * 渲染拖尾。
     *
     * @param entity       实体
     * @param poseStack    姿态栈
     * @param bufferSource 缓冲源
     * @param partialTick  部分刻
     * @param visualNode   视觉节点
     * @param renderType   渲染类型
     */
    public abstract void render(T entity, PoseStack poseStack, MultiBufferSource bufferSource, float partialTick, PathNode visualNode, RenderType renderType);

    /**
     * 渲染样板：一次性算好子类所需的全部上下文。
     * <p>
     * 子类 {@code render} 开头调用此方法，若返回 {@code null}（节点不足）则直接 return。
     * 直接从 PoseStack 取出 Matrix4f，绕过后续所有 PoseStack 操作。
     * </p>
     */
    protected final RenderSetup<T> beginRender(T entity, PoseStack poseStack, MultiBufferSource bufferSource, float partialTick, PathNode visualNode, RenderType renderType) {
        List<InterpolatedNode> smoothNodes = buildSmoothNodes(entity, visualNode, partialTick);
        if (smoothNodes.size() < 2) {
            return null;
        }
        VertexConsumer consumer = bufferSource.getBuffer(renderType);
        Matrix4f matrix = new Matrix4f(poseStack.last().pose());
        Vec3 renderPos = visualNode.pos();
        this.trailVertexCount = 0; // 清空收集缓冲
        return new RenderSetup<>(entity, consumer, matrix, partialTick, renderPos, smoothNodes);
    }

    /**
     * 渲染上下文（渲染样板产物），供子类直接取用。
     */
    protected static final class RenderSetup<T extends AttachmentEntity> {
        public final T entity;
        public final VertexConsumer consumer;
        public final Matrix4f matrix;
        public final float partialTick;
        public final Vec3 renderPos;
        public final List<InterpolatedNode> smoothNodes;

        RenderSetup(T entity, VertexConsumer consumer, Matrix4f matrix, float partialTick, Vec3 renderPos, List<InterpolatedNode> smoothNodes) {
            this.entity = entity;
            this.consumer = consumer;
            this.matrix = matrix;
            this.partialTick = partialTick;
            this.renderPos = renderPos;
            this.smoothNodes = smoothNodes;
        }

        /** 节点数（含头尾） */
        public int nodeCount() {
            return smoothNodes.size();
        }
    }

    // ===================== 颜色工具 =====================

    /**
     * 将 RGB 颜色与 alpha 打包为 ARGB 顶点色。
     */
    protected static int packColor(int rgb, float alpha) {
        int a = clampByte(alpha * 255f);
        return FastColor.ARGB32.color(a, (rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
    }

    /**
     * 将 RGB 颜色与 alpha、亮度增强打包为 ARGB 顶点色。
     */
    protected static int packColor(int rgb, float alpha, float brightness) {
        int a = clampByte(alpha * 255f);
        int r = Math.min(255, Math.round(((rgb >> 16) & 0xFF) * brightness));
        int g = Math.min(255, Math.round(((rgb >> 8) & 0xFF) * brightness));
        int b = Math.min(255, Math.round((rgb & 0xFF) * brightness));
        return FastColor.ARGB32.color(a, r, g, b);
    }

    // ===================== 平滑节点构建 =====================

    protected List<InterpolatedNode> buildSmoothNodes(T entity, PathNode visualNode, float partialTick) {
        ArrayList<PathNode> history = new ArrayList<>(entity.getHistoryNodes());
        history.set(0, visualNode);
        int actualLength = Math.min(history.size(), historyLength);
        if (actualLength < 2) {
            return List.of();
        }
        PathNode[] nodes = new PathNode[actualLength];
        for (int i = 0; i < actualLength; i++) {
            nodes[i] = history.get(i).lerp(history.get(Math.max(0, i - 1)), partialTick);
        }
        int endIndex = nodes.length - 1;
        int startIdx = Math.max(0, Math.min(startIndex, endIndex - 1));

        List<InterpolatedNode> result = new ArrayList<>((endIndex - startIdx) * segmentsPerNode + 1);
        Quaternionf tempQuat = new Quaternionf();

        for (int i = startIdx; i < endIndex; i++) {
            PathNode p0 = nodes[Math.max(i - 1, startIdx)];
            PathNode p1 = nodes[i];
            PathNode p2 = nodes[i + 1];
            PathNode p3 = nodes[Math.min(i + 2, endIndex)];

            Quaternionf q1 = eulerToQuaternion(p1.yaw(), p1.pitch(), p1.roll());
            Quaternionf q2 = eulerToQuaternion(p2.yaw(), p2.pitch(), p2.roll());

            for (int j = 0; j < segmentsPerNode; j++) {
                float t = ((float) j / segmentsPerNode);
                result.add(catmullRomInterpolate(p0, p1, p2, p3, q1, q2, t, tempQuat));
            }
        }

        PathNode lastNode = nodes[endIndex];
        result.add(new InterpolatedNode(lastNode.pos(), eulerToQuaternion(lastNode.yaw(), lastNode.pitch(), lastNode.roll())));

        return result;
    }

    protected InterpolatedNode catmullRomInterpolate(PathNode p0, PathNode p1, PathNode p2, PathNode p3,
                                                     Quaternionf q1, Quaternionf q2, float t, Quaternionf tempQuat) {
        float t2 = t * t, t3 = t2 * t;
        float f0 = -0.5f * t3 + t2 - 0.5f * t;
        float f1 = 1.5f * t3 - 2.5f * t2 + 1.0f;
        float f2 = -1.5f * t3 + 2.0f * t2 + 0.5f * t;
        float f3 = 0.5f * t3 - 0.5f * t2;

        Vec3 pos = new Vec3(
                p0.pos().x * f0 + p1.pos().x * f1 + p2.pos().x * f2 + p3.pos().x * f3,
                p0.pos().y * f0 + p1.pos().y * f1 + p2.pos().y * f2 + p3.pos().y * f3,
                p0.pos().z * f0 + p1.pos().z * f1 + p2.pos().z * f2 + p3.pos().z * f3
        );

        tempQuat.set(q1).slerp(q2, t);
        return new InterpolatedNode(pos, new Quaternionf(tempQuat));
    }

    protected Quaternionf eulerToQuaternion(float yaw, float pitch, float roll) {
        return new Quaternionf()
                .rotateY((float) Math.toRadians(-yaw))
                .rotateX((float) Math.toRadians(pitch))
                .rotateZ((float) Math.toRadians(roll));
    }

    // ===================== 四边形发射（收集 + 批量提交） =====================

    /**
     * 收集一个四边形到缓冲（矩阵变换后暂存），由 {@link #flushVertices} 批量提交。
     */
    protected void emitQuad(VertexConsumer consumer, Matrix4f matrix,
                            float x1, float y1, float z1, int c1,
                            float x2, float y2, float z2, int c2,
                            float x3, float y3, float z3, int c3,
                            float x4, float y4, float z4, int c4) {
        ensureTrailCapacity(this.trailVertexCount + 4);
        Vector3f v = new Vector3f();
        appendTrailVertex(matrix, x1, y1, z1, c1, 0, 0, v);
        appendTrailVertex(matrix, x2, y2, z2, c2, 1, 0, v);
        appendTrailVertex(matrix, x3, y3, z3, c3, 1, 1, v);
        appendTrailVertex(matrix, x4, y4, z4, c4, 0, 1, v);
    }

    private void appendTrailVertex(Matrix4f matrix, float x, float y, float z, int color, float u, float v, Vector3f scratch) {
        matrix.transformPosition(x, y, z, scratch);
        int vertexIndex = this.trailVertexCount * 5;
        this.trailVertexData[vertexIndex] = scratch.x;
        this.trailVertexData[vertexIndex + 1] = scratch.y;
        this.trailVertexData[vertexIndex + 2] = scratch.z;
        this.trailVertexData[vertexIndex + 3] = u;
        this.trailVertexData[vertexIndex + 4] = v;
        this.trailColorData[this.trailVertexCount] = color;
        this.trailVertexCount++;
    }

    /** 收集完成：批量提交（Unsafe 直写）并清空缓冲。子类 render 末尾调用。 */
    protected void flushVertices(VertexConsumer consumer) {
        if (this.trailVertexCount > 0) {
            RenderUtil.writeVertices(consumer, this.trailVertexData, this.trailColorData, LightTexture.FULL_BRIGHT, this.trailVertexCount);
            this.trailVertexCount = 0;
        }
    }

    private void ensureTrailCapacity(int requiredVertexCount) {
        if (requiredVertexCount * 5 > this.trailVertexData.length) {
            int newVertexCapacity = Math.max(requiredVertexCount, this.trailVertexData.length / 5 * 2);
            float[] newVertexData = new float[newVertexCapacity * 5];
            System.arraycopy(this.trailVertexData, 0, newVertexData, 0, this.trailVertexCount * 5);
            this.trailVertexData = newVertexData;
            int[] newColorData = new int[newVertexCapacity];
            System.arraycopy(this.trailColorData, 0, newColorData, 0, this.trailVertexCount);
            this.trailColorData = newColorData;
        }
    }

    // ===================== 插值节点记录 =====================

    public record InterpolatedNode(Vec3 pos, Quaternionf rot) {
    }

    // ===================== 工具 =====================

    private static int clampByte(float v) {
        return Math.max(0, Math.min(255, Math.round(v)));
    }
}
