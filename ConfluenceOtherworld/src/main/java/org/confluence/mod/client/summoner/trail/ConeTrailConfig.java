package org.confluence.mod.client.summoner.trail;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntity;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * 圆锥拖尾配置。
 * <p>
 * 适用于球状、圆形、能量弹、魔法球等实体。
 * 使用 10 参数 addVertex 快速路径 + matrix.transformPosition 预变换。
 * </p>
 *
 * @param <T> 实体类型
 */
public class ConeTrailConfig<T extends AttachmentEntity> extends TrailConfig<T, ConeTrailConfig<T>> {

    /** 拖尾头部最大半径 */
    public float maxRadius = 0.2f;

    /** 最小半径比例，控制尾端不会完全缩成一点 */
    public float minRadiusRatio = 0.0f;

    /** 圆锥截面正多边形边数 */
    public int resolution = 6;

    public ConeTrailConfig<T> maxRadius(float radius) {
        this.maxRadius = radius;
        return this;
    }

    public ConeTrailConfig<T> minRadiusRatio(float ratio) {
        this.minRadiusRatio = ratio;
        return this;
    }

    public ConeTrailConfig<T> resolution(int resolution) {
        this.resolution = resolution;
        return this;
    }

    @Override
    public void render(T entity, PoseStack poseStack, MultiBufferSource bufferSource, float partialTick, PathNode visualNode, RenderType renderType) {
        RenderSetup<T> setup = beginRender(entity, poseStack, bufferSource, partialTick, visualNode, renderType);
        if (setup == null) {
            return;
        }
        renderConeBody(setup);
        flushVertices(setup.consumer);
    }

    /**
     * 渲染圆锥主体：相邻平滑节点间画 {@link #resolution} 边截面壳面。
     * <p>
     * 半径 = maxRadius × (minRadiusRatio + (1 - minRadiusRatio) × fadeOut(progress))，
     * 头粗尾细；颜色与透明度由 {@link #colorFunction} / {@link #fadeOut} 决定。
     * </p>
     */
    protected void renderConeBody(RenderSetup<T> setup) {
        VertexConsumer consumer = setup.consumer;
        Matrix4f matrix = setup.matrix;
        T entity = setup.entity;
        float partialTick = setup.partialTick;

        float[] cosArr = getCosArray(resolution);
        float[] sinArr = getSinArray(resolution);

        int nodeCount = setup.nodeCount();
        Vec3 renderPos = setup.renderPos;

        // 复用向量，避免循环内分配
        Vector3f currV1 = new Vector3f(), currV2 = new Vector3f();
        Vector3f prevV1 = new Vector3f(), prevV2 = new Vector3f();

        for (int i = 0; i < nodeCount - 1; i++) {
            InterpolatedNode curr = setup.smoothNodes.get(i);
            InterpolatedNode prev = setup.smoothNodes.get(i + 1);

            float currProgress = (float) i / (nodeCount - 1);
            float prevProgress = (float) (i + 1) / (nodeCount - 1);

            float currFade = fadeOut.getFade(currProgress);
            float prevFade = fadeOut.getFade(prevProgress);
            float currRadius = maxRadius * (minRadiusRatio + (1 - minRadiusRatio) * currFade);
            float prevRadius = maxRadius * (minRadiusRatio + (1 - minRadiusRatio) * prevFade);

            int currColor = colorFunction.getColor(entity, currProgress, partialTick);
            int prevColor = colorFunction.getColor(entity, prevProgress, partialTick);
            int currARGB = packColor(currColor, currFade * (200f / 255f));
            int prevARGB = packColor(prevColor, prevFade * (200f / 255f));

            float crx = (float)(curr.pos().x - renderPos.x);
            float cry = (float)(curr.pos().y - renderPos.y);
            float crz = (float)(curr.pos().z - renderPos.z);
            float prx = (float)(prev.pos().x - renderPos.x);
            float pry = (float)(prev.pos().y - renderPos.y);
            float prz = (float)(prev.pos().z - renderPos.z);

            for (int j = 0; j < resolution; j++) {
                float cos1 = cosArr[j], sin1 = sinArr[j];
                float cos2 = cosArr[j + 1], sin2 = sinArr[j + 1];

                currV1.set(cos1 * currRadius, sin1 * currRadius, 0).rotate(curr.rot());
                currV2.set(cos2 * currRadius, sin2 * currRadius, 0).rotate(curr.rot());
                prevV1.set(cos1 * prevRadius, sin1 * prevRadius, 0).rotate(prev.rot());
                prevV2.set(cos2 * prevRadius, sin2 * prevRadius, 0).rotate(prev.rot());

                emitQuad(consumer, matrix,
                        crx + currV1.x, cry + currV1.y, crz + currV1.z, currARGB,
                        crx + currV2.x, cry + currV2.y, crz + currV2.z, currARGB,
                        prx + prevV2.x, pry + prevV2.y, prz + prevV2.z, prevARGB,
                        prx + prevV1.x, pry + prevV1.y, prz + prevV1.z, prevARGB);
            }
        }
    }
}
