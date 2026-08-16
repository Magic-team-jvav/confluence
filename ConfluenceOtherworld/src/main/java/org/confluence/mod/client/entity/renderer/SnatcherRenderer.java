package org.confluence.mod.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.monster.Snatcher;
import org.confluence.mod.common.init.entity.MonsterEntities;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/// 在抓人草头部与同步根部之间绘制连续藤蔓。
///
/// <p>藤蔓由若干交叉面片组成，长度随实体插值位置实时变化，因此头部伸缩时不会出现整段
/// 瞬移。抓人草与食人怪分别使用自己的叶片纹理，但共享相同的分段和朝向算法。</p>
public final class SnatcherRenderer
        extends GeoNormalRenderer<Snatcher> {
    private static final ResourceLocation SNATCHER_VINE =
            Confluence.asResource(
                    "textures/item/snatcher/snatcher_leaf.png");
    private static final ResourceLocation MAN_EATER_VINE =
            Confluence.asResource(
                    "textures/item/snatcher/man_eater_leaf.png");
    private static final double MAX_SEGMENT_LENGTH = 0.8;
    private static final float HALF_WIDTH = 0.22F;

    public SnatcherRenderer(
            EntityRendererProvider.Context context,
            ResourceLocation path) {
        super(context, path);
    }

    @Override
    public void render(
            Snatcher entity,
            float entityYaw,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource buffers,
            int packedLight) {
        if (entity.isAnchored()) {
            renderVine(
                    entity, partialTick, poseStack, buffers, packedLight);
        }
        super.render(
                entity,
                entityYaw,
                partialTick,
                poseStack,
                buffers,
                packedLight);
    }

    private static void renderVine(
            Snatcher entity,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource buffers,
            int packedLight) {
        double entityX = Mth.lerp(
                partialTick, entity.xOld, entity.getX());
        double entityY = Mth.lerp(
                partialTick, entity.yOld, entity.getY());
        double entityZ = Mth.lerp(
                partialTick, entity.zOld, entity.getZ());
        Vec3 start = new Vec3(
                0.0, entity.getBbHeight() * 0.5, 0.0);
        Vec3 end = entity.getAnchor().subtract(
                entityX, entityY, entityZ);
        Vec3 fullDifference = end.subtract(start);
        double fullLength = fullDifference.length();
        if (fullLength < 1.0E-5) {
            return;
        }
        int count = Math.max(
                1, Mth.ceil(fullLength / MAX_SEGMENT_LENGTH));
        Vec3 segment = fullDifference.scale(1.0 / count);
        double segmentLength = segment.length();
        Vec3 direction = segment.normalize();
        Quaternionf rotation = new Quaternionf().rotationTo(
                new Vector3f(0.0F, 1.0F, 0.0F),
                direction.toVector3f());
        ResourceLocation texture =
                entity.getType() == MonsterEntities.MAN_EATER.get()
                        ? MAN_EATER_VINE : SNATCHER_VINE;
        VertexConsumer vertices = buffers.getBuffer(
                RenderType.entityCutoutNoCull(texture));

        for (int index = 0; index < count; index++) {
            Vec3 center = start.add(
                    segment.scale(index + 0.5));
            poseStack.pushPose();
            poseStack.translate(center.x, center.y, center.z);
            poseStack.mulPose(rotation);
            poseStack.mulPose(Axis.YP.rotation(
                    index * 0.47F));
            renderCrossedSegment(
                    poseStack,
                    vertices,
                    (float) segmentLength,
                    packedLight);
            poseStack.popPose();
        }
    }

    private static void renderCrossedSegment(
            PoseStack poseStack,
            VertexConsumer vertices,
            float length,
            int packedLight) {
        renderPlane(
                poseStack, vertices, length, packedLight);
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        renderPlane(
                poseStack, vertices, length, packedLight);
    }

    private static void renderPlane(
            PoseStack poseStack,
            VertexConsumer vertices,
            float length,
            int packedLight) {
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();
        Matrix3f normal = pose.normal();
        float lower = -length * 0.5F;
        float upper = length * 0.5F;
        vertex(vertices, matrix, normal,
                -HALF_WIDTH, lower, 0.0F, 0.0F, 1.0F, packedLight);
        vertex(vertices, matrix, normal,
                HALF_WIDTH, lower, 0.0F, 1.0F, 1.0F, packedLight);
        vertex(vertices, matrix, normal,
                HALF_WIDTH, upper, 0.0F, 1.0F, 0.0F, packedLight);
        vertex(vertices, matrix, normal,
                -HALF_WIDTH, upper, 0.0F, 0.0F, 0.0F, packedLight);
    }

    private static void vertex(
            VertexConsumer vertices,
            Matrix4f matrix,
            Matrix3f normal,
            float x,
            float y,
            float z,
            float u,
            float v,
            int packedLight) {
        vertices.vertex(matrix, x, y, z)
                .color(255, 255, 255, 255)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(normal, 0.0F, 0.0F, 1.0F)
                .endVertex();
    }
}
