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

public class DropletTrailContext<T extends AttachmentEntity> extends ConeTrailContext<T> {

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, RenderContext<T> context) {
        List<InterpolatedNode> nodes = buildSmoothNodes(context.entity, context.visualNode, context.partialTick);
        int nodeCount = nodes.size();
        if (nodeCount > 1) {
            renderCone(poseStack, bufferSource, context, nodes);
            renderHead(poseStack, bufferSource, context, nodes.get(0));
        }
    }

    private void renderHead(PoseStack poseStack, MultiBufferSource bufferSource, RenderContext<T> context, InterpolatedNode headNode) {
        VertexConsumer buffer = bufferSource.getBuffer(LyraRenderTypes.getTrail());
        Matrix4f matrix = new Matrix4f(poseStack.last().pose());
        float[] cosArr = getCosArray(resolution);
        float[] sinArr = getSinArray(resolution);

        float headFade = fadeOut.getFade(0);
        float headRadius = maxRadius * (minRadiusRatio + (1 - minRadiusRatio) * headFade);
        int headARGB = packColor(colorFunction.getColor(context.entity, 0, context.partialTick), headFade * (200F / 255F));

        Vec3 renderPos = context.visualNode.pos();
        float hrx = (float) (headNode.pos().x - renderPos.x), hry = (float) (headNode.pos().y - renderPos.y), hrz = (float) (headNode.pos().z - renderPos.z);
        int hemisphereSegments = Math.max(2, resolution / 2);
        Vector3f v1 = new Vector3f(), v2 = new Vector3f(), v3 = new Vector3f(), v4 = new Vector3f();

        for (int lat = 0; lat < hemisphereSegments; lat++) {
            float latAngle1 = (float) (Math.PI / 2 * lat / hemisphereSegments);
            float latAngle2 = (float) (Math.PI / 2 * (lat + 1) / hemisphereSegments);
            float r1 = (float) Math.cos(latAngle1) * headRadius;
            float r2 = (float) Math.cos(latAngle2) * headRadius;
            float h1 = (float) Math.sin(latAngle1) * headRadius;
            float h2 = (float) Math.sin(latAngle2) * headRadius;

            for (int lon = 0; lon < resolution; lon++) {
                float cos1 = cosArr[lon], sin1 = sinArr[lon], cos2 = cosArr[lon + 1], sin2 = sinArr[lon + 1];
                v1.set(cos1 * r1, sin1 * r1, h1).rotate(headNode.rot());
                v2.set(cos2 * r1, sin2 * r1, h1).rotate(headNode.rot());
                v3.set(cos2 * r2, sin2 * r2, h2).rotate(headNode.rot());
                v4.set(cos1 * r2, sin1 * r2, h2).rotate(headNode.rot());

                float v1x = hrx + v1.x, v1y = hry + v1.y, v1z = hrz + v1.z;
                float v2x = hrx + v2.x, v2y = hry + v2.y, v2z = hrz + v2.z;
                float v3x = hrx + v3.x, v3y = hry + v3.y, v3z = hrz + v3.z;
                float v4x = hrx + v4.x, v4y = hry + v4.y, v4z = hrz + v4.z;

                Vector3f normal = new Vector3f(v3x - v1x, v3y - v1y, v3z - v1z).cross(new Vector3f(v2x - v1x, v2y - v1y, v2z - v1z));
                if (normal.lengthSquared() > 1.0E-6F) {
                    normal.normalize();
                } else {
                    normal.set(0, 1, 0);
                }
                Vector3f direction = matrix.transformDirection(normal, new Vector3f());

                Vector3f p1 = matrix.transformPosition(v1x, v1y, v1z, new Vector3f());
                Vector3f p2 = matrix.transformPosition(v2x, v2y, v2z, new Vector3f());
                Vector3f p3 = matrix.transformPosition(v3x, v3y, v3z, new Vector3f());
                Vector3f p4 = matrix.transformPosition(v4x, v4y, v4z, new Vector3f());

                buffer.vertex(p1.x, p1.y, p1.z).color(headARGB).uv(0F, 0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(direction.x, direction.y, direction.z).endVertex();
                buffer.vertex(p2.x, p2.y, p2.z).color(headARGB).uv(1F, 0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(direction.x, direction.y, direction.z).endVertex();
                buffer.vertex(p3.x, p3.y, p3.z).color(headARGB).uv(1F, 1F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(direction.x, direction.y, direction.z).endVertex();
                buffer.vertex(p4.x, p4.y, p4.z).color(headARGB).uv(0F, 1F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(direction.x, direction.y, direction.z).endVertex();
            }
        }
    }
}
