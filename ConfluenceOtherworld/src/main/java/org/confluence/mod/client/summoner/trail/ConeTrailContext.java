package org.confluence.mod.client.summoner.trail;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.client.summoner.LyraRenderTypes;
import org.confluence.mod.client.summoner.RenderContext;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntity;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;

public class ConeTrailContext<T extends AttachmentEntity> extends TrailContext<T> {

    public float maxRadius = 0.2F;

    public float minRadiusRatio = 0.1F;

    public int resolution = 6;

    public ConeTrailContext<T> maxRadius(float radius) {
        this.maxRadius = radius;
        return this;
    }

    public ConeTrailContext<T> minRadiusRatio(float ratio) {
        this.minRadiusRatio = ratio;
        return this;
    }

    public ConeTrailContext<T> resolution(int resolution) {
        this.resolution = resolution;
        return this;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, RenderContext<T> context) {
        renderCone(poseStack, bufferSource, context, buildSmoothNodes(context.entity, context.visualNode, context.partialTick));
    }

    protected void renderCone(PoseStack poseStack, MultiBufferSource bufferSource, RenderContext<T> context, List<InterpolatedNode> nodes) {
        int nodeCount = nodes.size();
        if (nodeCount > 1) {
            VertexConsumer buffer = bufferSource.getBuffer(LyraRenderTypes.getTrail());
            Matrix4f matrix = new Matrix4f(poseStack.last().pose());
            Vec3 renderPos = context.visualNode.pos();
            float[] cosArr = getCosArray(resolution);
            float[] sinArr = getSinArray(resolution);
            Vector3f currV1 = new Vector3f(), currV2 = new Vector3f(), prevV1 = new Vector3f(), prevV2 = new Vector3f();
            for (int i = 0; i < nodeCount - 1; i++) {
                InterpolatedNode curr = nodes.get(i);
                InterpolatedNode prev = nodes.get(i + 1);
                float currProgress = (float) i / (nodeCount - 1);
                float prevProgress = (float) (i + 1) / (nodeCount - 1);

                float currFade = fadeOut.getFade(currProgress);
                float prevFade = fadeOut.getFade(prevProgress);
                float currRadius = maxRadius * (minRadiusRatio + (1 - minRadiusRatio) * currFade);
                float prevRadius = maxRadius * (minRadiusRatio + (1 - minRadiusRatio) * prevFade);

                int currARGB = packColor(colorFunction.getColor(context.entity, currProgress, context.partialTick), currFade * (200F / 255F));
                int prevARGB = packColor(colorFunction.getColor(context.entity, prevProgress, context.partialTick), prevFade * (200F / 255F));

                float crx = (float) (curr.pos().x - renderPos.x), cry = (float) (curr.pos().y - renderPos.y), crz = (float) (curr.pos().z - renderPos.z);
                float prx = (float) (prev.pos().x - renderPos.x), pry = (float) (prev.pos().y - renderPos.y), prz = (float) (prev.pos().z - renderPos.z);

                for (int j = 0; j < resolution; j++) {
                    float cos1 = cosArr[j], sin1 = sinArr[j], cos2 = cosArr[j + 1], sin2 = sinArr[j + 1];
                    currV1.set(cos1 * currRadius, sin1 * currRadius, 0).rotate(curr.rot());
                    currV2.set(cos2 * currRadius, sin2 * currRadius, 0).rotate(curr.rot());
                    prevV1.set(cos1 * prevRadius, sin1 * prevRadius, 0).rotate(prev.rot());
                    prevV2.set(cos2 * prevRadius, sin2 * prevRadius, 0).rotate(prev.rot());

                    Vector3f v1 = matrix.transformPosition(crx + currV1.x, cry + currV1.y, crz + currV1.z, new Vector3f());
                    Vector3f v2 = matrix.transformPosition(crx + currV2.x, cry + currV2.y, crz + currV2.z, new Vector3f());
                    Vector3f v3 = matrix.transformPosition(prx + prevV2.x, pry + prevV2.y, prz + prevV2.z, new Vector3f());
                    Vector3f v4 = matrix.transformPosition(prx + prevV1.x, pry + prevV1.y, prz + prevV1.z, new Vector3f());

                    Vector3f normal = new Vector3f(v3).sub(v1).cross(new Vector3f(v2).sub(v1));
                    if (normal.lengthSquared() > 1.0E-6F) {
                        normal.normalize();
                    } else {
                        normal.set(0, 1, 0);
                    }
                    Vector3f direction = matrix.transformDirection(normal, new Vector3f());

                    buffer.vertex(v1.x, v1.y, v1.z).color(currARGB).uv(0F, 0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(direction.x, direction.y, direction.z).endVertex();
                    buffer.vertex(v2.x, v2.y, v2.z).color(currARGB).uv(1F, 0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(direction.x, direction.y, direction.z).endVertex();
                    buffer.vertex(v3.x, v3.y, v3.z).color(prevARGB).uv(1F, 1F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(direction.x, direction.y, direction.z).endVertex();
                    buffer.vertex(v4.x, v4.y, v4.z).color(prevARGB).uv(0F, 1F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(direction.x, direction.y, direction.z).endVertex();
                }
            }
        }
    }
}
