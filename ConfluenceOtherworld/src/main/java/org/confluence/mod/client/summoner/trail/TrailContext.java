package org.confluence.mod.client.summoner.trail;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.FastColor;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.client.summoner.RenderContext;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntity;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/// 附件实体拖尾共用的历史插值、颜色及淡出参数。
public abstract class TrailContext<T extends AttachmentEntity> {

    public int timer = 0;

    public int historyLength = 4;

    public int segmentsPerNode = 8;

    public int colorRGB = 0xFF0000;

    public RenderContext.ColorFunction<T> colorFunction = (entity, progress, partialTick) -> colorRGB;

    public RenderContext.FadeFunction fadeOut = progress -> (float) Math.pow(Math.max(0.0F, 1.0F - progress), 1.5);

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


    public abstract void render(PoseStack poseStack, MultiBufferSource bufferSource, RenderContext<T> context);


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

    public record InterpolatedNode(Vec3 pos, Quaternionf rot) {
    }


    protected static int packColor(int rgb, float alpha) {
        return FastColor.ARGB32.color(clampByte(alpha * 255F), (rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
    }

    protected static int packColor(int rgb, float alpha, float brightness) {
        return FastColor.ARGB32.color(
                clampByte(alpha * 255F),
                Math.min(255, Math.round(((rgb >> 16) & 0xFF) * brightness)),
                Math.min(255, Math.round(((rgb >> 8) & 0xFF) * brightness)),
                Math.min(255, Math.round((rgb & 0xFF) * brightness)));
    }

    private static int clampByte(float value) {
        return Math.max(0, Math.min(255, Math.round(value)));
    }
}
