package org.confluence.mod.client.summoner.trail;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.FastColor;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.client.summoner.RenderContext;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntity;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 拖尾上下文基类：只负责公共参数与平滑节点构建，顶点提交交给子类自行处理。
 * <p>
 * 形态对齐 Lyra 1.21.1.13（{@code first.lyra.client.render.trail.TrailContext}）：
 * 去掉了配置类的 SELF 泛型参数与 {@code startIndex}，也去掉了顶点收集缓冲与 Unsafe 批提交，
 * 子类在 {@link #render(PoseStack, MultiBufferSource, RenderContext)} 里自行取
 * {@code VertexConsumer} 并直接提交顶点。
 * </p>
 *
 * @param <T> 实体类型
 */
public abstract class TrailContext<T extends AttachmentEntity> {

    /** 拖尾计时器值，>0 时显示拖尾 */
    public int timer = 0;

    /** 历史节点数量 */
    public int historyLength = 4;

    /** 每节点插值分段数 */
    public int segmentsPerNode = 8;

    /** 基础颜色 RGB */
    public int colorRGB = 0xFF0000;

    /** 颜色函数 */
    public RenderContext.ColorFunction<T> colorFunction = (entity, progress, partialTick) -> colorRGB;

    /** 淡出函数 */
    public RenderContext.FadeFunction fadeOut = progress -> (float) Math.pow(Math.max(0.0F, 1.0F - progress), 1.5);

    /** 圆形顶点缓存 */
    protected static final Map<Integer, float[]> COS_CACHE = new HashMap<>();
    protected static final Map<Integer, float[]> SIN_CACHE = new HashMap<>();

    /** 位置变换用的临时向量（单渲染线程，复用避免每帧分配） */
    private static final Vector3f SCRATCH = new Vector3f();

    /**
     * 写入单个顶点：位置先经 {@code matrix} 变换到模型视图空间，
     * 再存 5 float（x,y,z,u,v）与 1 int（ARGB），返回下一个顶点下标。
     * <p>
     * 子类统一走「收集到 float[]/int[] → {@link org.confluence.mod.client.summoner.RenderUtil#writeVertices}」
     * 的提交方式；不要改用 {@code VertexConsumer#addVertex(pose, …)} 链式写法，
     * 那条链与 {@code NEW_ENTITY} 的 7 个元素不匹配。
     * </p>
     */
    protected static int putVertex(float[] xyzuv, int[] colors, int index, Matrix4f matrix, float x, float y, float z, float u, float v, int color) {
        matrix.transformPosition(x, y, z, SCRATCH);
        int base = index * 5;
        xyzuv[base] = SCRATCH.x;
        xyzuv[base + 1] = SCRATCH.y;
        xyzuv[base + 2] = SCRATCH.z;
        xyzuv[base + 3] = u;
        xyzuv[base + 4] = v;
        colors[index] = color;
        return index + 1;
    }

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

    // ===================== 链式配置 =====================

    public TrailContext<T> timer(int timer) {
        this.timer = timer;
        return this;
    }

    public TrailContext<T> historyLength(int length) {
        this.historyLength = length;
        return this;
    }

    public TrailContext<T> segmentsPerNode(int segments) {
        this.segmentsPerNode = segments;
        return this;
    }

    public TrailContext<T> colorRGB(int color) {
        this.colorRGB = color;
        return this;
    }

    public TrailContext<T> colorFunction(RenderContext.ColorFunction<T> function) {
        this.colorFunction = function;
        return this;
    }

    public TrailContext<T> fadeOut(RenderContext.FadeFunction function) {
        this.fadeOut = function;
        return this;
    }

    // ===================== 渲染入口 =====================

    /** 渲染拖尾：子类自行取顶点消费者并提交顶点 */
    public abstract void render(PoseStack poseStack, MultiBufferSource bufferSource, RenderContext<T> context);

    // ===================== 平滑节点构建 =====================

    /**
     * 构建跨帧平滑并细分后的节点列表，节点不足 2 个时返回空列表。
     * <p>
     * 历史节点是逐 tick 的离散值，只有头尾需要跨帧平滑：头用视觉节点，尾在自身与新一号节点间插值，
     * 使窗口每 tick 前移时尾端连续滑出而不是直接跳变；中间节点保持原值，避免整条链被分刻二次压缩。
     * </p>
     */
    protected List<InterpolatedNode> buildSmoothNodes(T entity, PathNode visualNode, float partialTick) {
        ArrayList<PathNode> history = new ArrayList<>(entity.getHistoryNodes());
        history.set(0, visualNode);
        int actualLength = Math.min(history.size(), historyLength);
        if (actualLength < 2) {
            return List.of();
        }
        PathNode[] nodes = new PathNode[actualLength];
        nodes[0] = visualNode;
        for (int i = 1; i < actualLength - 1; i++) {
            nodes[i] = history.get(i);
        }
        nodes[actualLength - 1] = history.get(actualLength - 1).lerp(history.get(actualLength - 2), partialTick);
        int endIndex = nodes.length - 1;

        List<InterpolatedNode> result = new ArrayList<>(endIndex * segmentsPerNode + 1);
        Quaternionf tempQuat = new Quaternionf();
        for (int i = 0; i < endIndex; i++) {
            PathNode p0 = nodes[Math.max(i - 1, 0)];
            PathNode p1 = nodes[i];
            PathNode p2 = nodes[i + 1];
            PathNode p3 = nodes[Math.min(i + 2, endIndex)];
            Quaternionf q1 = eulerToQuaternion(p1.yaw(), p1.pitch(), p1.roll());
            Quaternionf q2 = eulerToQuaternion(p2.yaw(), p2.pitch(), p2.roll());
            for (int j = 0; j < segmentsPerNode; j++) {
                result.add(catmullRomInterpolate(p0, p1, p2, p3, q1, q2, (float) j / segmentsPerNode, tempQuat));
            }
        }
        PathNode lastNode = nodes[endIndex];
        result.add(new InterpolatedNode(lastNode.pos(), eulerToQuaternion(lastNode.yaw(), lastNode.pitch(), lastNode.roll())));
        return result;
    }

    /** Catmull-Rom 插值位置，朝向走四元数球面插值 */
    protected InterpolatedNode catmullRomInterpolate(PathNode p0, PathNode p1, PathNode p2, PathNode p3,
                                                     Quaternionf q1, Quaternionf q2, float t, Quaternionf tempQuat) {
        float t2 = t * t, t3 = t2 * t;
        float f0 = -0.5F * t3 + t2 - 0.5F * t;
        float f1 = 1.5F * t3 - 2.5F * t2 + 1.0F;
        float f2 = -1.5F * t3 + 2.0F * t2 + 0.5F * t;
        float f3 = 0.5F * t3 - 0.5F * t2;
        Vec3 pos = new Vec3(
                p0.pos().x * f0 + p1.pos().x * f1 + p2.pos().x * f2 + p3.pos().x * f3,
                p0.pos().y * f0 + p1.pos().y * f1 + p2.pos().y * f2 + p3.pos().y * f3,
                p0.pos().z * f0 + p1.pos().z * f1 + p2.pos().z * f2 + p3.pos().z * f3);
        tempQuat.set(q1).slerp(q2, t);
        return new InterpolatedNode(pos, new Quaternionf(tempQuat));
    }

    protected Quaternionf eulerToQuaternion(float yaw, float pitch, float roll) {
        return new Quaternionf()
                .rotateY((float) Math.toRadians(-yaw))
                .rotateX((float) Math.toRadians(pitch))
                .rotateZ((float) Math.toRadians(roll));
    }

    /** 插值节点：位置 + 朝向四元数 */
    public record InterpolatedNode(Vec3 pos, Quaternionf rot) {
    }

    // ===================== 颜色工具 =====================

    /** RGB 颜色与 alpha 打包为 ARGB 顶点色 */
    protected static int packColor(int rgb, float alpha) {
        return FastColor.ARGB32.color(clampByte(alpha * 255F), (rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
    }

    /** RGB 颜色与 alpha、亮度增强打包为 ARGB 顶点色 */
    protected static int packColor(int rgb, float alpha, float brightness) {
        return FastColor.ARGB32.color(
                clampByte(alpha * 255F),
                Math.min(255, Math.round(((rgb >> 16) & 0xFF) * brightness)),
                Math.min(255, Math.round(((rgb >> 8) & 0xFF) * brightness)),
                Math.min(255, Math.round((rgb & 0xFF) * brightness)));
    }

    /** Java 17 没有 Math.clamp，用 Math.max/Math.min 实现 */
    private static int clampByte(float value) {
        return Math.max(0, Math.min(255, Math.round(value)));
    }
}
