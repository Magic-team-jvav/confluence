package org.confluence.mod.client.renderer.entity.projectile.sword;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.client.effect.RenderStateShardAccessor;
import org.confluence.mod.common.entity.projectile.sword.NightEdgeProjectile;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

/**
 * 永夜刃的客户端局部挥砍与双层轨迹渲染器。
 *
 * <p>轨迹由实体年龄和固定关键帧即时推导，不把客户端专用轨迹对象放进公共实体，
 * 也不会在渲染器单例中保存逐实体可变历史。</p>
 */
public final class NightEdgeProjectileRenderer
        extends SwordItemProjectileRenderer<NightEdgeProjectile> {
    public NightEdgeProjectileRenderer(EntityRendererProvider.Context context) {
        super(context, 0.8F);
    }

    @Override
    public void render(
            NightEdgeProjectile entity,
            float entityYaw,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight
    ) {
        float age = Math.min(entity.tickCount + partialTick, NightEdgeProjectile.maxTrailTime());
        renderTrail(entity, age, poseStack, bufferSource, 0xB4570EFD, 0.32F, 0.0F);
        renderTrail(entity, age, poseStack, bufferSource, 0x80E4E0FF, 0.18F, 0.14F);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    protected void applyTransform(NightEdgeProjectile entity, float partialTick, PoseStack poseStack) {
        float age = Math.min(entity.tickCount + partialTick, NightEdgeProjectile.maxTrailTime());
        Vec3 point = rotateForOwner(entity, NightEdgeProjectile.sampleLocalPoint(age));
        poseStack.translate(point.x, point.y, point.z);
        Entity owner = entity.getOwner();
        float ownerYaw = owner == null
                ? entity.getYRot()
                : Mth.lerp(partialTick, owner.yRotO, owner.getYRot());
        poseStack.mulPose(Axis.YP.rotationDegrees(-ownerYaw + 70.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(NightEdgeProjectile.sampleRoll(age)));
    }

    private static void renderTrail(
            NightEdgeProjectile entity,
            float age,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int argb,
            float width,
            float yOffset
    ) {
        List<Vec3> points = new ArrayList<>();
        float start = Math.max(0.0F, age - 5.0F);
        for (float time = start; time < age; time += 0.5F) {
            points.add(rotateForOwner(entity, NightEdgeProjectile.sampleLocalPoint(time)).add(0.0, yOffset, 0.0));
        }
        points.add(rotateForOwner(entity, NightEdgeProjectile.sampleLocalPoint(age)).add(0.0, yOffset, 0.0));
        if (points.size() < 2) {
            return;
        }

        VertexConsumer consumer = bufferSource.getBuffer(RenderStateShardAccessor.TRAIL_RENDER_TYPE);
        Matrix4f matrix = poseStack.last().pose();
        for (int index = 1; index < points.size(); index++) {
            Vec3 previous = points.get(index - 1);
            Vec3 current = points.get(index);
            Vec3 segment = current.subtract(previous);
            if (segment.lengthSqr() <= 1.0E-10) {
                continue;
            }
            float progress = index / (float) (points.size() - 1);
            Vec3 side = segment.normalize().cross(new Vec3(0.0, 1.0, 0.0));
            if (side.lengthSqr() <= 1.0E-10) {
                side = new Vec3(1.0, 0.0, 0.0);
            }
            side = side.normalize().scale(width * progress);
            int alpha = Math.round(((argb >>> 24) & 0xFF) * progress);
            int color = (argb & 0x00FFFFFF) | (alpha << 24);
            vertex(consumer, matrix, previous.add(side), color);
            vertex(consumer, matrix, previous.subtract(side), color);
            vertex(consumer, matrix, current.subtract(side), color);
            vertex(consumer, matrix, current.add(side), color);
        }
    }

    private static Vec3 rotateForOwner(NightEdgeProjectile entity, Vec3 point) {
        Entity owner = entity.getOwner();
        float yaw = owner == null ? entity.getYRot() : owner.getYRot();
        return NightEdgeProjectile.rotateLocalPoint(yaw, point);
    }

    private static void vertex(VertexConsumer consumer, Matrix4f matrix, Vec3 point, int argb) {
        consumer.vertex(matrix, (float) point.x, (float) point.y, (float) point.z)
                .color(argb)
                .endVertex();
    }
}
